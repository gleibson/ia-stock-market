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

package net.sourceforge.jasa.agent;

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;
import net.sourceforge.jabm.util.SummaryStats;
import net.sourceforge.jasa.agent.strategy.RandomConstrainedStrategy;
import net.sourceforge.jasa.market.MarketFacade;
import net.sourceforge.jasa.market.auctioneer.ClearingHouseAuctioneer;
import net.sourceforge.jasa.report.PriceStatisticsReport;
import net.sourceforge.jasa.sim.PRNGTestSeeds;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RandomConstrainedStrategyTest extends TestCase {

	/**
	 * @uml.property name="testStrategy"
	 * @uml.associationEnd
	 */
	protected RandomConstrainedStrategy testStrategy;

	/**
	 * @uml.property name="testAgent"
	 * @uml.associationEnd
	 */
	protected TokenTradingAgent testAgent;

	/**
	 * @uml.property name="auctioneer"
	 * @uml.associationEnd
	 */
	protected ClearingHouseAuctioneer auctioneer;

	/**
	 * @uml.property name="market"
	 * @uml.associationEnd
	 */
	protected MarketFacade auction;

	/**
	 * @uml.property name="report"
	 * @uml.associationEnd
	 */
	protected PriceStatisticsReport report;
	
	protected RandomEngine prng;

	static final double MAX_MARKUP = 100.0;

	static final double PRIV_VALUE = 7.0;

	static final int MAX_ROUNDS = 200000;

	public RandomConstrainedStrategyTest(String name) {
		super(name);
	}

	public void setUp() {
		prng = new MersenneTwister64(PRNGTestSeeds.UNIT_TEST_SEED);
		auction = new MarketFacade(new MersenneTwister64(
				PRNGTestSeeds.UNIT_TEST_SEED));
		testAgent = new TokenTradingAgent(PRIV_VALUE, 100, auction);
		testStrategy = new RandomConstrainedStrategy(testAgent);
		testStrategy.setBuy(false);
		testStrategy.setMarkupDistribution(new Uniform(0, MAX_MARKUP, prng));
		testAgent.setStrategy(testStrategy);
		auctioneer = new ClearingHouseAuctioneer(auction);
		auction.setAuctioneer(auctioneer);
		report = new PriceStatisticsReport();
		auction.addReport(report);
		auction.register(testAgent);
		auction.setMaximumRounds(MAX_ROUNDS);
	}

	public void testBidRange() {
		System.out.println(getClass() + ": testBidRange()");
		System.out.println("testAgent = " + testAgent);
		System.out.println("testStrategy = " + testStrategy);
		auction.run();
		report.produceUserOutput();
		SummaryStats askStats = report.getAskPriceStats();
		assertTrue(approxEqual(askStats.getMin(), PRIV_VALUE));
		assertTrue(approxEqual(askStats.getMax(), MAX_MARKUP + PRIV_VALUE));
		assertTrue(approxEqual(askStats.getMean(), (MAX_MARKUP / 2) + PRIV_VALUE));
	}

	public void testBidFloor() {
		testAgent.setIsSeller(false);
		System.out.println(getClass() + ": testBidFloor()");
		System.out.println("testAgent = " + testAgent);
		System.out.println("testStrategy = " + testStrategy);
		auction.run();
		report.produceUserOutput();
		SummaryStats bidStats = report.getBidPriceStats();
		assertTrue(bidStats.getMin() >= 0);
		assertTrue(bidStats.getMax() <= PRIV_VALUE);
	}

	public boolean approxEqual(double x, double y) {
		return Math.abs(x - y) < 0.1;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(RandomConstrainedStrategyTest.class);
	}

}
