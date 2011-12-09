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

package net.sourceforge.jasa.market.rules;

import net.sourceforge.jasa.market.Market;

/**
 * The interface for expressing the condition of closing a certain time
 * interval, such as an market, or a day, or whether it's time to do something,
 * i.e. clearing the market.
 * 
 * @author Jinzhong Niu
 * @version $Revision: 1.1 $
 * 
 */

public abstract class TimingCondition {

	private Market auction;

	public TimingCondition() {
		this(null);
	}

	public TimingCondition(Market auction) {
		setAuction(auction);
	}

	public void setAuction(Market auction) {
		this.auction = auction;
	}

	public Market getAuction() {
		return auction;
	}

	public abstract boolean eval();

}