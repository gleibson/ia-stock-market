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

package net.sourceforge.jasa.agent.strategy;

import java.io.Serializable;

import net.sourceforge.jabm.learning.Learner;
import net.sourceforge.jabm.learning.StimuliResponseLearner;
import net.sourceforge.jabm.util.Parameterizable;
import net.sourceforge.jabm.util.Prototypeable;
import net.sourceforge.jabm.util.Resetable;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.market.Market;


/**
 * <p>
 * A trading strategy that uses a stimuli-response learning algorithm, such as
 * the Roth-Erev algorithm, to adapt its trading behaviour in successive market
 * rounds by using the agent's profits in the last round as a reward signal.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision: 1.4 $
 */

public class StimuliResponseStrategy extends DiscreteLearnerStrategy implements
    Serializable {

	/**
	 * The learning algorithm to use.
	 */
	protected StimuliResponseLearner learner = null;

	public StimuliResponseStrategy(AbstractTradingAgent agent) {
		super(agent);
	}

	public StimuliResponseStrategy() {
		super();
	}

//	public void setup(ParameterDatabase parameters, Parameter base) {
//
//		super.setup(parameters, base);
//
//		Parameter learnerParameter = base.push(P_LEARNER);
//		learner = (StimuliResponseLearner) parameters.getInstanceForParameter(
//		    learnerParameter, new Parameter(P_DEF_BASE).push(P_LEARNER),
//		    StimuliResponseLearner.class);
//
//		((Parameterizable) learner).setup(parameters, learnerParameter);
//	}

	public Object protoClone() {
		StimuliResponseStrategy clonedStrategy;
		try {
			clonedStrategy = (StimuliResponseStrategy) clone();
			clonedStrategy.learner = (StimuliResponseLearner) ((Prototypeable) this.learner)
			    .protoClone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
		return clonedStrategy;
	}

	public int act() {
		return learner.act();
	}

	public void learn(Market auction) {
		learner.reward(agent.getLastPayoff());
	}

	public void reset() {
		super.reset();
		((Resetable) learner).reset();
	}

	public Learner getLearner() {
		return learner;
	}

	public void setLearner(Learner learner) {
		this.learner = (StimuliResponseLearner) learner;
	}

	public String toString() {
		return "(" + getClass() + " markupscale:" + markupScale + " learner:"
		    + learner + " quantity:" + quantity + ")";
	}

}
