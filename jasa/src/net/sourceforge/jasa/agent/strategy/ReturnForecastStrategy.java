package net.sourceforge.jasa.agent.strategy;

import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.engine.RandomEngine;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.valuation.ReturnForecaster;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;

public class ReturnForecastStrategy extends FixedQuantityStrategyImpl {
	
	protected RandomEngine prng;
	
	protected double markup;
	
	protected AbstractContinousDistribution markupDistribution;
	
	public double getReturnForecast() {
		ReturnForecaster forecaster = 
			(ReturnForecaster) agent.getValuationPolicy();
		return forecaster.determineValue(auction);
	}
	
	public double getPriceForecast(double currentPrice) {
		if (currentPrice < 10E-5) {
			currentPrice = 10E-5;
		}
		double forecastedReturn = getReturnForecast();
		return currentPrice * Math.exp(forecastedReturn);
	}
	
	public boolean decideDirection(double currentPrice, 
									double forecastedPrice) {
		if (Double.isNaN(currentPrice)) {
			return prng.nextDouble() >= 0.5;
		} else if (Math.abs(forecastedPrice - currentPrice) < 10E-5) {
			return prng.nextDouble() >= 0.5;
		} else {
			return forecastedPrice > currentPrice;
		}
	}
	
	@Override
	public boolean modifyShout(Order shout) {
		boolean result = super.modifyShout(shout);
		double currentPrice = auction.getCurrentPrice();
		if (!(currentPrice >= 0)) {
			assert currentPrice >= 0;
		}
		double forecastedPrice = getPriceForecast(currentPrice);
		assert forecastedPrice >= -10E-5;
		boolean isBid = decideDirection(currentPrice, forecastedPrice);
		shout.setIsBid(isBid);
		if (isBid) {
			shout.setPrice(forecastedPrice * (1 - markup));
		} else {
			shout.setPrice(forecastedPrice * (1 + markup));
		}
		return result;
	}

	@Override
	public void onRoundClosed(Market auction) {
	}

//	public ReturnForecaster getForecaster() {
//		return forecaster;
//	}
//
//	public void setForecaster(ReturnForecaster forecaster) {
//		this.forecaster = forecaster;
//	}

	public RandomEngine getPrng() {
		return prng;
	}

	public void setPrng(RandomEngine prng) {
		this.prng = prng;
	}

	public AbstractContinousDistribution getMarkupDistribution() {
		return markupDistribution;
	}

	public void setMarkupDistribution(
			AbstractContinousDistribution markupDistribution) {
		this.markupDistribution = markupDistribution;
		initialiseMarkup();
	}
	
	public void initialiseMarkup() {
		this.markup = markupDistribution.nextDouble();
	}

}
