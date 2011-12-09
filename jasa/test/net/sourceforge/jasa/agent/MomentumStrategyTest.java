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

import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;
import net.sourceforge.jasa.agent.strategy.MomentumStrategy;
import net.sourceforge.jasa.sim.PRNGTestSeeds;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Steve Phelps
 * @version $Revision: 1.4 $
 */

public class MomentumStrategyTest extends TestCase {

	/**
	 * @uml.property name="testTrader"
	 * @uml.associationEnd
	 */
	MockTrader testTrader;

	/**
	 * @uml.property name="testStrategy"
	 * @uml.associationEnd
	 */
	MockMomentumStrategy testStrategy;
	
	RandomEngine prng;

	/**
	 * @uml.property name="pRIV_VALUE"
	 */
	protected final double PRIV_VALUE = 100;

	public MomentumStrategyTest(String arg0) {
		super(arg0);
	}

	public void setUp() {
		prng = new MersenneTwister64(PRNGTestSeeds.UNIT_TEST_SEED);
		testTrader = new MockTrader(this, 0, 0, PRIV_VALUE, null);
		testStrategy = new MockMomentumStrategy(testTrader, prng);
		testStrategy.setBuy(false);
		testTrader.setStrategy(testStrategy);
		testStrategy.setAgent(testTrader);
	}

	public void testZeroTargetMargin() {
		double margin = testStrategy.targetMargin(PRIV_VALUE);
		assertTrue(margin == 0);
	}

	public void testBuyerMargin() {
		testStrategy.setBuy(true);

		double margin = testStrategy.targetMargin(PRIV_VALUE + 10);
		assertTrue(margin == 10 / PRIV_VALUE);

		margin = testStrategy.targetMargin(PRIV_VALUE - 10);
		assertTrue(margin == -10 / PRIV_VALUE);
	}

	public void testSellerMargin() {
		testStrategy.setBuy(false);
		double margin = testStrategy.targetMargin(PRIV_VALUE + 10);
		assertTrue(margin == 10 / PRIV_VALUE);

		margin = testStrategy.targetMargin(PRIV_VALUE - 10);
		assertTrue(margin == -10 / PRIV_VALUE);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(MomentumStrategyTest.class);
	}
}

class MockMomentumStrategy extends MomentumStrategy {

	public MockMomentumStrategy(AbstractTradingAgent agent, RandomEngine prng) {
		super(agent, prng);
		// TODO Auto-generated constructor stub
	}

	protected void adjustMargin() {
		// For a mock strategy do nothing
	}

	/**
	 * A publically accessible tagetMargin() method.
	 */
	public double targetMargin(double price) {
		return super.targetMargin(price);
	}

}
