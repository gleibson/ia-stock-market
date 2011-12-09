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

import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;

/**
 * Classes implementing this interface define pricing policies for auctioneers.
 * A pricing policy determines the price of an individual transaction in the
 * market as a function of the individual bid and ask prices and the current
 * market quote.
 * 
 * @author Steve Phelps
 * @version $Revision: 1.1 $
 */

public interface PricingPolicy {

	public double determineClearingPrice(Order bid, Order ask,
	    MarketQuote clearingQuote);

}