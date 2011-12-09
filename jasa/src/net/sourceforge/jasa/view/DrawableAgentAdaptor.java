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

package net.sourceforge.jasa.view;

import java.awt.Color;

import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.TokenTradingAgent;
import net.sourceforge.jasa.agent.TradingStrategy;
import net.sourceforge.jasa.agent.strategy.AdaptiveStrategy;
import net.sourceforge.jasa.agent.valuation.AbstractRandomValuer;
import net.sourceforge.jasa.agent.valuation.ValuationPolicy;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;



/**
 * An adaptor that allows an AbstractTraderAgent to be drawn on a RePast
 * Object2DDisplay. Each agent is represented by a coloured bar. Profitable
 * buyers are shown as red, and profitable sellers are shown as blue. The
 * brighter the colour, the more profitable the agent. Agents with zero profits
 * are shown as white bars. The height of the bar represents the current margin
 * offered by the agent; that is, the absolute difference between their current
 * valuation of the commodity on offer and their last shout price.
 * 
 * @author Steve Phelps
 * @version $Revision: 1.5 $
 */

public class DrawableAgentAdaptor implements Drawable {

	protected TokenTradingAgent agent;

	protected Market auction;

	protected static float minProfit = Float.POSITIVE_INFINITY;

	protected static float maxProfit = Float.NEGATIVE_INFINITY;

	protected static float maxMarkup = Float.NEGATIVE_INFINITY;

	public float scale = 1000;

	public DrawableAgentAdaptor(Market auction) {
		this(auction, null);
	}

	public DrawableAgentAdaptor(Market auction, TokenTradingAgent agent) {
		this.agent = agent;
		this.auction = auction;
		if (agent != null) {
			ValuationPolicy valuer = agent.getValuationPolicy();
			if (valuer instanceof AbstractRandomValuer) {
//				scale = (float) ((AbstractRandomValuer) valuer).getMaxValue() / 2;
				//TODO
			}
		}
	}

	public void draw(SimGraphics g) {
		float price = Math.abs(getLastShoutPrice() - getCurrentValuation());
		if (price > DrawableAgentAdaptor.maxMarkup) {
			DrawableAgentAdaptor.maxMarkup = price;
		}
		float profit = getTotalProfits();
		if (profit < DrawableAgentAdaptor.minProfit) {
			DrawableAgentAdaptor.minProfit = profit;
		}
		if (profit > DrawableAgentAdaptor.maxProfit) {
			DrawableAgentAdaptor.maxProfit = profit;
		}
		float relProfit = profit / DrawableAgentAdaptor.maxProfit;
		float relPrice = price / DrawableAgentAdaptor.maxMarkup;
		int y = 1 + (int) (relPrice * 4);
		g.setDrawingParameters(5, y, 1);
		Color color = Color.BLACK;

		if (relProfit > 0.01) {
			relProfit = 0.4f + 0.6f * relProfit;
			if (agent.isBuyer()) {
				color = new Color(relProfit, 0, 0);
			} else {
				color = new Color(0, 0, relProfit);
			}
		} else {
			color = Color.WHITE;
		}

		g.drawRect(color);
		g.setDrawingParameters(5, 5, 5);
		g.drawHollowRect(Color.WHITE);
	}

	public int getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getY() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getLastProfit() {
		float profit = 0;
		if (agent != null) {
			profit = (float) agent.getLastPayoff();
		}
		return profit;
	}

	public float getTotalProfits() {
		float profit = 0;
		if (agent != null) {
			profit = (float) agent.getTotalPayoff();
		}
		return profit;
	}

	public float getCurrentValuation() {
		float valuation = 0;
		if (agent != null) {
			valuation = (float) agent.getValuation(auction);
		}
		return valuation;
	}

	public long getId() {
		if (agent != null) {
			return agent.hashCode();
		} else {
			return -1;
		}
	}

	public String getRole() {
		if (agent != null) {
			if (agent.isSeller()) {
				return "Seller";
			} else {
				return "Buyer";
			}
		} else {
			return "Null";
		}
	}

	public float getLastShoutPrice() {
		float price = 0;
		if (agent != null) {
			Order shout = agent.getCurrentOrder();
			if (shout != null) {
				price = (float) shout.getPrice();
			}
		}
		return price;
	}

	public Object getAgentType() {
		return agent;
	}

	public boolean getLastShoutAccepted() {
		if (agent != null) {
			return agent.lastOrderFilled();
		} else {
			return false;
		}
	}

	public float getLearningDelta() {
		if (agent != null) {
			TradingStrategy s = agent.getStrategy();
			if (s instanceof AdaptiveStrategy) {
				return (float) ((AdaptiveStrategy) s).getLearner().getLearningDelta();
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

}
