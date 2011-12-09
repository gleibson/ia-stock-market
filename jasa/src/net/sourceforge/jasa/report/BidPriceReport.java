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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.util.Distribution;
import net.sourceforge.jabm.util.Resetable;
import net.sourceforge.jabm.util.SummaryStats;
import net.sourceforge.jasa.event.MarketClosedEvent;
import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.event.RoundClosedEvent;
import net.sourceforge.jasa.event.OrderPlacedEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;

import org.apache.log4j.Logger;

/**
 * <p>
 * A historicalDataReport that keeps cummulative statistics on bid prices, ask prices,
 * transaction prices, and market quote prices.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision: 1.3 $
 */

public class BidPriceReport extends AbstractAuctionReport implements
    Serializable, Cloneable, Resetable {

	protected SummaryStats stats;

	static Logger logger = Logger.getLogger(BidPriceReport.class);

	public BidPriceReport() {
		initialise();
	}

	public void eventOccurred(SimEvent event) {
		super.eventOccurred(event);
		if (event instanceof OrderPlacedEvent) {
			Order order = ((OrderPlacedEvent) event).getOrder();
			if (order.isBid()) {
				stats.newData(order.getPrice());
			}
		}
	}

	public void initialise() {
		stats = new SummaryStats("Bid Price");
	}

	public void reset() {
		stats.reset();
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

//	public TransactionPriceReport newCopy() {
//		TransactionPriceReport copy = null;
//		try {
//			copy = (TransactionPriceReport) clone();
//			for (int i = 0; i < stats.length; i++) {
//				copy.stats[i] = (SummaryStats) stats[i].clone();
//			}
//		} catch (CloneNotSupportedException e) {
//			logger.error(e.getMessage());
//			e.printStackTrace();
//			throw new Error(e.getMessage());
//		}
//		return copy;
//	}

	public void produceUserOutput() {
		reportHeader();
		printStats(stats);
	}

	@Override
	public Map<Object,Number> getVariableBindings() {
		Map<Object,Number> vars = super.getVariableBindings();
		createReportVars(vars, "bidprice", stats);
		return vars;
	}

	protected void reportHeader() {
		logger.info("");
		logger.info("Bid price statistics");
		logger.info("--------------------");
		logger.info("");
	}

	protected void printStats(SummaryStats stats) {
		stats.log();
		logger.info("");
	}

	protected void createReportVars(Map<Object,Number> vars, String var, Distribution stats) {
		vars.put(makeVar(var, "mean"), new Double(stats.getMean()));
		vars.put(makeVar(var, "min"), new Double(stats.getMin()));
		vars.put(makeVar(var, "max"), new Double(stats.getMax()));
		vars.put(makeVar(var, "stdev"), new Double(stats.getStdDev()));
	}

	protected ReportVariable makeVar(String varName, String moment) {
		return new ReportVariable("pricestats." + varName + "." + moment, varName
		    + " distribution " + moment);
	}
}
