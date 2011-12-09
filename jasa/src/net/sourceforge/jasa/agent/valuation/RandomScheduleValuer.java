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

package net.sourceforge.jasa.agent.valuation;

import java.io.Serializable;

import cern.jet.random.engine.RandomEngine;

import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jasa.market.Market;


/**
 * A valuation policy which specifies a randomly-generated series of valuations
 * for each unit of commodity.
 * 
 * @author Steve Phelps
 * @version $Revision: 1.4 $
 */

public class RandomScheduleValuer extends RandomValuer implements Serializable {

	public RandomScheduleValuer(double min, double max, RandomEngine prng) {
		super(min, max, prng);
	}

	public RandomScheduleValuer() {
		super();
	}

	public void consumeUnit(Market auction) {
		drawRandomValue();
	}

	@Override
	public void subscribeToEvents(EventScheduler scheduler) {
		// Do nothing
	}

	@Override
	public void eventOccurred(SimEvent event) {
		// Do nothing
	}

}