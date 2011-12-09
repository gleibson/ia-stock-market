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

package net.sourceforge.jasa.market.auctioneer;

import java.util.Iterator;

import net.sourceforge.jasa.event.MarketEventListener;
import net.sourceforge.jasa.market.Account;
import net.sourceforge.jasa.market.IllegalOrderException;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.QuoteProvider;
import net.sourceforge.jasa.market.ShoutsNotVisibleException;


/**
 * @author Steve Phelps
 * @version $Revision: 1.6 $
 */

public interface Auctioneer extends QuoteProvider, MarketEventListener {

	/**
	 * Perform the clearing operation for the market; match buyers with sellers
	 * and inform the market of any deals.
	 */
	public void clear();

	/**
	 * Code for handling a new shout in the market. Subclasses should override
	 * this method if they wish to provide different handling for different
	 * market rules.
	 * 
	 * @param shout
	 *          The new shout to be processed
	 * 
	 * @exception IllegalOrderException
	 *              Thrown if the shout is invalid in some way.
	 */
	public void newOrder(Order order) throws IllegalOrderException;

	/**
	 * Handle a request to retract a shout.
	 */
	public void removeShout(Order order);

	/**
	 * Log the current state of the market.
	 */
	public void printState();

	/**
	 * Specify which market we are the auctioneer for.
	 */
	public void setMarket(Market auction);

	/**
	 * Find out which market we are the auctioneer for.
	 */
	public Market getMarket();

	public Iterator<Order> askIterator();

	public Iterator<Order> bidIterator();

	/**
	 * Return true if the shouts of others are visible.
	 */
	public boolean shoutsVisible();

	public boolean orderFilled(Order shout) throws ShoutsNotVisibleException;

	public boolean transactionsOccurred() throws ShoutsNotVisibleException;

	public Order getLastAsk() throws ShoutsNotVisibleException;

	public Order getLastBid() throws ShoutsNotVisibleException;

	public Order getLastShout() throws ShoutsNotVisibleException;

	/**
	 * Get the account which holds any budget surplus or deficit for the
	 * auctioneer. This is useful when implementing non-budget-balanced
	 * mechanisms.
	 */
	public Account getAccount();

}