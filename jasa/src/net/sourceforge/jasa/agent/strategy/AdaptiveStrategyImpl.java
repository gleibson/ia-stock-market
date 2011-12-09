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

package net.sourceforge.jasa.agent.strategy;

import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.market.Market;

/**
 * @author Steve Phelps
 * @version $Revision: 1.3 $
 */

public abstract class AdaptiveStrategyImpl extends FixedDirectionStrategy
    implements AdaptiveStrategy {

	public AdaptiveStrategyImpl(AbstractTradingAgent agent) {
		super(agent);
	}

	public AdaptiveStrategyImpl() {
		super();
	}

	public void onRoundClosed(Market auction) {
		// super.endOfRound(market);
		getLearner().monitor();
	}

}