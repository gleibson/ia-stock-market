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
 * This is exception is thrown when requesting data from an market that is not
 * available due to the way that the market is configured.
 * 
 * @author Steve Phelps
 * @version $Revision: 1.1 $
 */

public class DataUnavailableException extends AuctionException {

	public DataUnavailableException(String message) {
		super(message);
	}

	public DataUnavailableException() {
		super();
	}

}
