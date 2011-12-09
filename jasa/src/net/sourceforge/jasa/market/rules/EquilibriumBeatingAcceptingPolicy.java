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

import net.sourceforge.jabm.learning.MimicryLearner;
import net.sourceforge.jabm.learning.SelfKnowledgable;
import net.sourceforge.jabm.util.Parameterizable;
import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;
import net.sourceforge.jasa.market.IllegalOrderException;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.report.ReportVariableBoard;

import org.apache.log4j.Logger;

/**
 * implements the shout-accepting rule under which a shout must be more
 * competitive than an estimated equilibrium.
 * 
 * The equilibrium is estimated through some learning algorithm, e.g.
 * sliding-window-average learning and widrowhoff learning, by training with
 * transaction prices.
 * 
 * @author Jinzhong Niu
 * @version $Revision: 1.3 $
 */

public class EquilibriumBeatingAcceptingPolicy extends
    QuoteBeatingAcceptingPolicy {

	static Logger logger = Logger
	    .getLogger(EquilibriumBeatingAcceptingPolicy.class);

	/**
	 * Reusable exceptions for performance
	 */
	protected static IllegalOrderException bidException = null;

	protected static IllegalOrderException askException = null;

	private double expectedHighestAsk;

	private double expectedLowestBid;

	/**
	 * A parameter used to adjust the equilibrium price estimate so as to relax
	 * the restriction.
	 */
	protected double delta = 0;

	public static final String P_DELTA = "delta";

	protected MimicryLearner learner;

	public static final String P_LEARNER = "learner";

	public static final String P_DEF_BASE = "equilibriumbeatingaccepting";

	public static final String EST_EQUILIBRIUM_PRICE = "estimated.equilibrium.price";

//	public void setup(ParameterDatabase parameters, Parameter base) {
//		super.setup(parameters, base);
//
//		Parameter defBase = new Parameter(P_DEF_BASE);
//
//		delta = parameters.getDoubleWithDefault(base.push(P_DELTA), defBase
//		    .push(P_DELTA), delta);
//		assert (0 <= delta);
//
//		learner = (MimicryLearner) parameters.getInstanceForParameter(base
//		    .push(P_LEARNER), defBase.push(P_LEARNER), MimicryLearner.class);
//		if (learner instanceof Parameterizable) {
//			((Parameterizable) learner).setup(parameters, base.push(P_LEARNER));
//		}
//	}

	public void initialise() {
		expectedHighestAsk = Double.MAX_VALUE;
		expectedLowestBid = 0;
	}

	public void reset() {
		initialise();
	}

	/**
	 * checks whether
	 * <p>
	 * shout
	 * </p>
	 * can beat the estimated equilibrium.
	 */
	public void check(Order shout) throws IllegalOrderException {
		super.check(shout);

		if (shout.isBid()) {
			if (shout.getPrice() < expectedLowestBid) {
				bidNotAnImprovementException();
			}
		} else {
			if (shout.getPrice() > expectedHighestAsk) {
				askNotAnImprovementException();
			}
		}
	}

	protected void bidNotAnImprovementException() throws IllegalOrderException {
		if (bidException == null) {
			// Only construct a new exception the once (for improved
			// performance)
			bidException = new IllegalOrderException(
			    "Bid cannot beat the estimated equilibrium!");
		}
		throw bidException;
	}

	protected void askNotAnImprovementException() throws IllegalOrderException {
		if (askException == null) {
			// Only construct a new exception the once (for improved
			// performance)
			askException = new IllegalOrderException(
			    "Ask cannot beat the estimated equilibrium!");
		}
		throw askException;
	}

	public void eventOccurred(MarketEvent event) {
		super.eventOccurred(event);

		if (event instanceof TransactionExecutedEvent) {
			learner.train(((TransactionExecutedEvent) event).getPrice());

			if (learner instanceof SelfKnowledgable
			    && ((SelfKnowledgable) learner).goodEnough()) {

				expectedLowestBid = learner.act() - delta;
				expectedHighestAsk = learner.act() + delta;

				ReportVariableBoard.getInstance().reportValue(EST_EQUILIBRIUM_PRICE,
				    learner.act(), event);
			}
		}
	}

	public void setDelta(double delta) {
		this.delta = delta;
	}

	public double getDelta() {
		return delta;
	}

	public MimicryLearner getLearner() {
		return learner;
	}

	public void setLearner(MimicryLearner learner) {
		this.learner = learner;
	}

	public String toString() {
		return "(" + getClass().getSimpleName() + " delta:" + delta + " " + learner
		    + ")";
	}
}
