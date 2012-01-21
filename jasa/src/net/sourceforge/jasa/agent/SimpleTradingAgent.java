/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2009 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package net.sourceforge.jasa.agent;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jasa.News;
import net.sourceforge.jasa.agent.strategy.TruthTellingStrategy;
import net.sourceforge.jasa.agent.valuation.DailyRandomValuer;
import net.sourceforge.jasa.agent.valuation.ValuationPolicy;

import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;

import net.sourceforge.jasa.market.Market;

/**
 * <p>
 * Agents of this type have a fixed volume, and they trade units equal to their
 * volume in each round of the market.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision: 1.8 $
 */

public class SimpleTradingAgent extends AbstractTradingAgent {
	
	private int id;
	private boolean lossAversion = false;
	private double precoDeCompra;
	private double precoDeVendaPositivo;
	private double precoDeVendaNegativo;
	private double alpha;
	private double beta;
	private MersenneTwister64 randomEngine;
	
	private int minValue;
	private int maxValue;
	private RandomEngine prng;
	
	private float alphaA;
	private float alphaB;
	private float betaA;
	private float betaB;
	
	private static ArrayList <News> news =new ArrayList <News> ();
	
	public SimpleTradingAgent(int stock, double funds, double privateValue,
			EventScheduler scheduler) {
		super(stock, funds, privateValue, scheduler);
	}

	public SimpleTradingAgent(int stock, double funds, double privateValue,
			TradingStrategy strategy, EventScheduler scheduler) {
		super(stock, funds, privateValue, strategy, scheduler);
	}

	public SimpleTradingAgent(int stock, double funds, EventScheduler scheduler) {
		super(stock, funds, scheduler);
	}

	public SimpleTradingAgent(double privateValue,
			boolean isSeller, TradingStrategy strategy, EventScheduler scheduler) {
		super(0, 0, privateValue, strategy, scheduler);
	}

	public SimpleTradingAgent(double privateValue, EventScheduler scheduler) {
		super(0, 0, privateValue, scheduler);
	}

	public SimpleTradingAgent(EventScheduler scheduler) {
		this(0, scheduler);
	}
	
	public SimpleTradingAgent() {
		this(null);
	}
	
	public void onAgentArrival(Market auction) {
		super.onAgentArrival(auction);
		lastPayoff = 0;
	}

	public boolean acceptDeal(Market auction, double price, int quantity) {
		return price >= valuer.determineValue(auction);
	}
//	
//	public void setVolume(int volume) {
//		this.volume = volume;
//	}

	public double getLastPayoff() {
		return lastPayoff;
	}

	public boolean active() {
		return true;
	}

	public void onEndOfDay(MarketEvent event) {
		// reset();
	}

	public String toString() {
		return "(" + getClass() + " id:" + hashCode() 
		    + " valuer:" + valuer + 
		    + totalPayoff + " lastProfit:" + lastPayoff
		    + " strategy:" + strategy + ")";
	}
	
	@Override
	public void eventOccurred(SimEvent ev) {
		
		if(this.lossAversion)
			lossAversion(ev);
		else
			externalEvent(ev);		
	}
	
	private void lossAversion(SimEvent ev){
		
		super.eventOccurred(ev);
		if(ev instanceof TransactionExecutedEvent) {
			TransactionExecutedEvent transaction = (TransactionExecutedEvent)ev;
			if(transaction.getBid().getAgent() == this) {
				setPrecoDeCompra(transaction.getPrice());
				setPrecoDeVendaPositivo(getPrecoDeCompra()*(1+getAlpha()));
				setPrecoDeVendaNegativo(getPrecoDeCompra()*(1-getBeta()));
				calcAlpha();
				calcBeta();
				
				((TruthTellingStrategy)getStrategy()).setBuy(false);
				
				setValuationPolicy(new DailyRandomValuer(getPrecoDeVendaNegativo(), getPrecoDeVendaPositivo(),getRandomEngine()));
				
			}
			else if(transaction.getAsk().getAgent() == this) {
				setPrecoDeCompra(transaction.getPrice());
				setPrecoDeVendaPositivo(getPrecoDeCompra()*(1+getAlpha()));
				setPrecoDeVendaNegativo(getPrecoDeCompra()*(1-getBeta()));
				calcAlpha();
				calcBeta();
				
				((TruthTellingStrategy)getStrategy()).setBuy(true);
				
				setValuationPolicy(new DailyRandomValuer(getPrecoDeVendaNegativo(), getPrecoDeVendaPositivo(),getRandomEngine()));
			}
		}
	}
	private void externalEvent(SimEvent ev){
		super.eventOccurred(ev);
		if(ev instanceof TransactionExecutedEvent) {
			TransactionExecutedEvent transaction = (TransactionExecutedEvent)ev;
		}
	}

	
	public void calcAlpha(){
		setAlpha(getGaussian(getAlphaA(), getAlphaB())); 
	}
	
	public void calcBeta(){
		setBeta(getGaussian(getBetaA(), getBetaB())); 
	}

	private static double getGaussian(double aMean, double aVariance){
		double gaussian=-0.1;
		do{
			gaussian=aMean + new Random().nextGaussian() * aVariance;
		} while(gaussian<0);
		return gaussian;
	}
	
	@Override
	public double calculateProfit(Market auction, int quantity, double price) {
		if (currentOrder == null) {
			return 0;
		}
		return super.calculateProfit(auction, quantity, price);
	}

	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	public void setBeta() {
		this.beta = getGaussian(this.betaA, this.betaB);
	}	
	
	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	
	public void setAlpha() {
		this.alpha = getGaussian(this.alphaA, this.alphaB);
	}

	public double getPrecoDeCompra() {
		return precoDeCompra;
	}

	public void setPrecoDeCompra(double precoDeCompra) {
		this.precoDeCompra = precoDeCompra;
	}

	public double getPrecoDeVendaPositivo() {
		return precoDeVendaPositivo;
	}

	public void setPrecoDeVendaPositivo(double precoDeVendaPositivo) {
		this.precoDeVendaPositivo = precoDeVendaPositivo;
	}

	public double getPrecoDeVendaNegativo() {
		return precoDeVendaNegativo;
	}

	public void setPrecoDeVendaNegativo(double precoDeVendaNegativo) {
		this.precoDeVendaNegativo = precoDeVendaNegativo;
	}

	public MersenneTwister64 getRandomEngine() {
		return randomEngine;
	}

	public void setRandomEngine(MersenneTwister64 randomEngine) {
		this.randomEngine = randomEngine;
	}

	public float getAlphaA() {
		return alphaA;
	}

	public void setAlphaA(float alphaA) {
		this.alphaA = alphaA;
	}

	public float getAlphaB() {
		return alphaB;
	}

	public void setAlphaB(float alphaB) {
		this.alphaB = alphaB;
	}

	public float getBetaA() {
		return betaA;
	}

	public void setBetaA(float betaA) {
		this.betaA = betaA;
	}

	public float getBetaB() {
		return betaB;
	}

	public void setBetaB(float betaB) {
		this.betaB = betaB;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void deliverNews(News news){
		
		this.minValue = (int) (this.minValue  * (1 + news.getStockNewValue()));
		this.maxValue = (int) (this.maxValue  * (1 + news.getStockNewValue()));
		System.out.println("Agent: "+this.id+" min: "+this.minValue+" max: "+this.maxValue);
		setValuationPolicy(new DailyRandomValuer(this.minValue, this.maxValue,this.getRandomEngine()));
	}

	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public RandomEngine getPrng() {
		return prng;
	}

	public void setPrng(RandomEngine prng) {
		this.prng = prng;
	}
	
	
//	@Override
//	public double calculateProfit(Market auction, int quantity, double price) {
//		if (isBuyer()) {
//			return (getValuation(auction) - price) * quantity;
//		} else {
//			return  (price - getValuation(auction)) * quantity;
//		}
//	}
	
}