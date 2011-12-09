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

package net.sourceforge.jasa.market;

/**
 * This exception is thrown if an agent attempts to examine the shouts of other
 * agents in an market where the auctioneer does not permit agents to see each
 * others shouts.
 * 
 * @author Steve Phelps
 * @version $Revision: 1.1 $
 */

public class ShoutsNotVisibleException extends DataUnavailableException {

	public ShoutsNotVisibleException(String message) {
		super(message);
	}

	public ShoutsNotVisibleException() {
		super();
	}

}
