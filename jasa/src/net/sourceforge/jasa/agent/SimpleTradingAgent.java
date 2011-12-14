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

import java.util.Date;

import cern.jet.random.engine.MersenneTwister64;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.SimEvent;
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
	
	private double precoDeCompra;
	private double precoDeVendaPositivo;
	private double precoDeVendaNegativo;
	private double alpha;
	private double beta;
	private MersenneTwister64 randomEngine;
	
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
		super.eventOccurred(ev);
		if(ev instanceof TransactionExecutedEvent) {
			TransactionExecutedEvent transaction = (TransactionExecutedEvent)ev;
			if(transaction.getBid().getAgent() == this) {
				setPrecoDeCompra(transaction.getPrice());
				setPrecoDeVendaPositivo(getPrecoDeCompra()*(1+getAlpha()));
				setPrecoDeVendaNegativo(getPrecoDeCompra()*(1-getBeta()));
				
				((TruthTellingStrategy)getStrategy()).setBuy(false);
				
				setValuationPolicy(new DailyRandomValuer(getPrecoDeVendaNegativo(), getPrecoDeVendaPositivo(),getRandomEngine()));
				
			}
			else if(transaction.getAsk().getAgent() == this) {
				setPrecoDeCompra(transaction.getPrice());
				setPrecoDeVendaPositivo(getPrecoDeCompra()*(1+getAlpha()));
				setPrecoDeVendaNegativo(getPrecoDeCompra()*(1-getBeta()));
				
				((TruthTellingStrategy)getStrategy()).setBuy(true);
				
				setValuationPolicy(new DailyRandomValuer(getPrecoDeVendaNegativo(), getPrecoDeVendaPositivo(),getRandomEngine()));
			}
		}
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

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
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
	
//	@Override
//	public double calculateProfit(Market auction, int quantity, double price) {
//		if (isBuyer()) {
//			return (getValuation(auction) - price) * quantity;
//		} else {
//			return  (price - getValuation(auction)) * quantity;
//		}
//	}
	
}