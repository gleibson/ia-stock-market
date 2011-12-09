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

package net.sourceforge.jasa.report;

import java.util.Map;

import net.sourceforge.jabm.report.Report;
import net.sourceforge.jasa.market.MarketFacade;



/**
 * @author Steve Phelps
 * @version $Revision: 1.5 $
 */

public interface AuctionReport extends Report {

//	/**
//	 * Produce the final historicalDataReport for the user. Implementors can do whatever they
//	 * see fit, for example by writing a historicalDataReport on stdout, or they may choose to
//	 * do nothing.
//	 */
//	public void produceUserOutput();

	/**
	 * Specify the market to be used when generating the historicalDataReport.
	 */
//	public void setAuction(MarketFacade auction);

}
