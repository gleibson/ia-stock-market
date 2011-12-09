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

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.report.DataWriter;
import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.event.RoundClosedEvent;
import net.sourceforge.jasa.event.OrderPlacedEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.MarketFacade;

/**
 * This class writes market data to the specified DataWriter objects, and thus
 * can be used to log data to eg, CSV files, a database backend, etc.
 * 
 * @author Steve Phelps
 * @version $Revision: 1.6 $
 */

public class DataWriterReport extends AbstractAuctionReport {

	/**
	 * output for the ask component of market quotes as time series.
	 */
	protected DataWriter askQuoteLog = null;

	/**
	 * output for the bid component of market quotes as time series.
	 */
	protected DataWriter bidQuoteLog = null;

	/**
	 * output for bid data as time series.
	 */
	protected DataWriter bidLog = null;

	/**
	 * output for ask data as time series.
	 */
	protected DataWriter askLog = null;

	/**
	 * output for transaction price time series.
	 */
	protected DataWriter transPriceLog = null;

	/**
	 * The market we are keeping statistics on.
	 */
	protected MarketFacade auction;

	public DataWriterReport() {
		this(null, null, null, null, null);
	}

	public DataWriterReport(DataWriter askQuoteLog, DataWriter bidQuoteLog,
	    DataWriter bidLog, DataWriter askLog, DataWriter transPriceLog) {
		this.askQuoteLog = askQuoteLog;
		this.bidQuoteLog = bidQuoteLog;
		this.askLog = askLog;
		this.bidLog = bidLog;
		this.transPriceLog = transPriceLog;
	}


	public void eventOccurred(SimEvent event) {
		super.eventOccurred(event);
		if (event instanceof TransactionExecutedEvent) {
			updateTransPriceLog((TransactionExecutedEvent) event);
		} else if (event instanceof OrderPlacedEvent) {
			updateShoutLog((OrderPlacedEvent) event);
		} else if (event instanceof RoundClosedEvent) {
			updateQuoteLog((RoundClosedEvent) event);
		}
	}

	public void updateQuoteLog(RoundClosedEvent event) {
		int time = event.getTime();
		MarketQuote quote = event.getAuction().getQuote();
		if (askQuoteLog != null) {
			askQuoteLog.newData(time);
			askQuoteLog.newData(quote.getAsk());
		}
		if (bidQuoteLog != null) {
			bidQuoteLog.newData(time);
			bidQuoteLog.newData(quote.getBid());
		}
		dataUpdated();
	}

	public void updateTransPriceLog(TransactionExecutedEvent event) {
		if (transPriceLog != null) {
			transPriceLog.newData(event.getTime());
			transPriceLog.newData(event.getPrice());
		}
		dataUpdated();
	}

	public void updateShoutLog(OrderPlacedEvent event) {
		Order shout = event.getOrder();
		int time = event.getTime();
		if (shout.isBid()) {
			if (bidLog != null) {
				bidLog.newData(time);
				bidLog.newData(shout.getPrice());
			}
		} else {
			if (askLog != null) {
				askLog.newData(time);
				askLog.newData(shout.getPrice());
			}
		}
		dataUpdated();
	}

	public void dataUpdated() {
	}

	public void produceUserOutput() {
	}

	public void setAuction(MarketFacade auction) {
		this.auction = auction;
	}

	@Override
	public void reset() {
	}

}
