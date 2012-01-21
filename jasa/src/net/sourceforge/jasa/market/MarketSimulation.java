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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.jabm.AbstractSimulation;
import net.sourceforge.jabm.Agent;
import net.sourceforge.jabm.AgentList;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.SimulationTime;
import net.sourceforge.jabm.event.SimulationFinishedEvent;
import net.sourceforge.jabm.event.SimulationStartingEvent;
import net.sourceforge.jasa.News;
import net.sourceforge.jasa.agent.SimpleTradingAgent;
import net.sourceforge.jasa.event.EndOfDayEvent;
import net.sourceforge.jasa.event.MarketClosedEvent;
import net.sourceforge.jasa.event.MarketOpenEvent;
import net.sourceforge.jasa.event.RoundClosedEvent;
import net.sourceforge.jasa.event.RoundClosingEvent;

import net.sourceforge.jasa.market.auctioneer.Auctioneer;

import net.sourceforge.jasa.market.rules.AuctionClosingCondition;
import net.sourceforge.jasa.market.rules.CombiTimingCondition;
import net.sourceforge.jasa.market.rules.DayEndingCondition;
import net.sourceforge.jasa.market.rules.MaxDaysAuctionClosingCondition;
import net.sourceforge.jasa.market.rules.MaxRoundsAuctionClosingCondition;
import net.sourceforge.jasa.market.rules.MaxRoundsDayEndingCondition;
import net.sourceforge.jasa.market.rules.NullAuctionClosingCondition;
import net.sourceforge.jasa.market.rules.TimingCondition;

import net.sourceforge.jasa.view.AuctionConsoleFrame;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import xml.manager.ParseXML;

/**
 * @author Steve Phelps
 * @version $Revision: 1.11 $
 * 
 */

public class MarketSimulation extends AbstractSimulation implements
		Serializable {

	protected ArrayList<News> news = new ArrayList<News>();

	protected Market market;

	protected boolean closed = false;

	/**
	 * The current round.
	 */
	protected int round;

	protected int age = 0;

	protected Account account = new Account();

	/**
	 * The current trading day (period)
	 */
	protected int day = 0;

	protected TimingCondition closingCondition = new NullAuctionClosingCondition();

	protected TimingCondition dayEndingCondition;

	protected boolean endOfRound = false;

	public static final String ERROR_SHOUTSVISIBLE = "Auctioneer does not permit shout inspection";

	static Logger logger = Logger.getLogger(MarketSimulation.class);

	public MarketSimulation(SimulationController controller) {
		super(controller);
		initialiseCounters();
		readFile();
	}

	public MarketSimulation() {
		this(null);
	}

	public void initialiseCounters() {
		day = 0;
		round = 0;
		endOfRound = false;
		age = 0;
		closed = false;
	}

	public void initialise() {
		initialiseCounters();
		addListener(market.getAuctioneer());
	}

	public void reset() {
		initialiseCounters();
	}

	public void informAuctionClosed() {
		fireEvent(new MarketClosedEvent(market, getRound()));
	}

	public void informEndOfDay() {
		fireEvent(new EndOfDayEvent(market, getRound()));
	}

	// public void informBeginOfDay() {
	// fireEvent(new DayOpeningEvent(market, getRound()));
	// }

	public void informAuctionOpen() {
		fireEvent(new MarketOpenEvent(market, getRound()));
	}

	public void beginRound() {
		if (closingCondition.eval()) {
			close();
		} else {
			endOfRound = false;
		}
	}

	/**
	 * Get the current round number
	 */
	public int getRound() {
		return round;
	}

	public int getAge() {
		return age;
	}

	public int getDay() {
		return day;
	}

	public Auctioneer getAuctioneer() {
		return market.getAuctioneer();
	}

	/**
	 * Get the last bid placed in the market.
	 */
	public Order getLastBid() throws ShoutsNotVisibleException {
		return getAuctioneer().getLastBid();
	}

	/**
	 * Get the last ask placed in the market.
	 */
	public Order getLastAsk() throws ShoutsNotVisibleException {
		return getAuctioneer().getLastAsk();
	}

	/**
	 * Runs the market.
	 */
	public void run() {

		initialise();

		if (getAuctioneer() == null) {
			throw new AuctionRuntimeException("No auctioneer has been assigned");
		}

		begin();

		try {
			while (!closed) {
				step();
			}

		} catch (AuctionClosedException e) {
			throw new AuctionRuntimeException(e);
		}

		end();
	}

	public void begin() {
		initialiseAgents();
		fireEvent(new SimulationStartingEvent(this));
		informAuctionOpen();
	}

	public void end() {
		informAuctionClosed();
		fireEvent(new SimulationFinishedEvent(this));
	}

	public void step() throws AuctionClosedException {
		runSingleRound();
	}

	public void endRound() {
		informRoundClosing();

		// getAuctioneer().endOfRoundProcessing();

		endOfRound = true;
		// System.out.println("round: "+round+" age: "+age);
		round++;

		age++;

		informRoundClosed();
		checkEndOfDay();
	}

	public void readFile() {

		try {
			ParseXML xml = new ParseXML();
			xml.ReadUserXmlFile(DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(new File("news.xml")));
			news = xml.getNews();

			/*
			 * for (News n : news) { System.out.println("DeliverTime: " +
			 * n.getDeliverTime()); System.out.println("ReceiversQuantity: " +
			 * n.getReceiversQuantity()); System.out.println("ReceiversPer: " +
			 * n.getReceiversPer()); System.out.println("StockNewValue: " +
			 * n.getStockNewValue());
			 * 
			 * }
			 */
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isClosed() {
		return closed;
	}

	public void runSingleRound() throws AuctionClosedException {
		if (isClosed()) {
			throw new AuctionClosedException("Auction is closed.");
		}
		if (closingCondition.eval()) {
			close();
		} else {
			beginRound();

			invokeAgentInteractions();
			endRound();
			deliverNews();
		}
	}

	public void deliverNews() {
		if (news.size() == 0) {
			return;
		}

		AgentList agents = this.getPopulation().getAgentList();
		SimpleTradingAgent agent;

		int contador = 0;
		if (this.age == news.get(0).getDeliverTime()) {
			for (int i = 0; i < news.get(0).getReceiversQuantity(); i++) {
				int espectedAgent = news.get(0).getReceivers().get(i);
				agent = (SimpleTradingAgent) agents.get(espectedAgent);
				if (agent.getId() == espectedAgent) {
					agent.deliverNews(news.get(0));
					contador++;
				} else {
					for (int j = 0; j < agents.size(); j++) {
						agent = (SimpleTradingAgent) agents.get(j);
						if (agent.getId() == espectedAgent) {
							agent.deliverNews(news.get(0));
							contador++;
							break;
						}
					}
				}
			}
			System.out.println("total: " + news.get(0).getReceiversQuantity()
					+ " contador: " + contador);
			news.remove(0);
		}

	}

	public void informRoundClosing() {
		fireEvent(new RoundClosingEvent(market, round));
	}

	public void informRoundClosed() {
		fireEvent(new RoundClosedEvent(market, round));
	}

	// public void placeOrder(Order shout) throws AuctionException {
	//
	// // TODO: to switch the following two lines?
	//
	// fireEvent(new OrderReceivedEvent(market, round, shout));
	// market.placeOrder(shout);
	// fireEvent(new OrderPlacedEvent(market, round, shout));
	//
	// // setChanged();
	// // notifyObservers();
	// }
	//
	// public void changeShout(Order shout) throws AuctionException {
	// removeOrder(shout);
	// placeOrder(shout);
	// }
	//
	// public void removeOrder(Order shout) {
	// // Remove this shout and all of its children.
	// for (Order s = shout; s != null; s = s.getChild()) {
	// getAuctioneer().removeShout(s);
	// // if ( s != shout ) {
	// // ShoutPool.release(s);
	// // }
	// }
	// shout.makeChildless();
	// }

	/**
	 * Return an iterator iterating over all traders registered (as opposed to
	 * actively trading) in the market.
	 */
	// public Iterator getTraderIterator() {
	// // return registeredTraders.iterator();
	// return null;
	// }

	// public Iterator getActiveTraderIterator() {
	// // return activeTraders.iterator();
	// return null;
	// }
	//
	// protected void initialise() {
	// round = 0;
	// day = 0;
	// age = 0;
	// }

	protected void checkEndOfDay() {
		if (dayEndingCondition != null && dayEndingCondition.eval())
			endDay();
	}

	public void close() {
		closed = true;
	}

	/**
	 * Terminate the current trading period (day)
	 */
	protected void endDay() {
		logger.debug("endDay()");
		// System.out.println("day: "+day);
		// report.debug("day = " + day + " of " + getMaximumDays());
		round = 0;
		informEndOfDay();
		// getAuctioneer().endOfDayProcessing();
		day++;
	}

	public int getRemainingTime() {
		TimingCondition cond = getAuctionClosingCondition(MaxRoundsAuctionClosingCondition.class);

		if (cond != null) {
			return ((MaxRoundsAuctionClosingCondition) cond)
					.getRemainingRounds();
		} else {
			cond = getDayEndingCondition(MaxRoundsDayEndingCondition.class);

			if (cond != null) {
				return ((MaxRoundsDayEndingCondition) cond)
						.getRemainingRounds();
			} else {
				throw new AuctionRuntimeException(
						getClass()
								+ " requires a TimingCondition knowing remaining time in the market to be configured");
			}
		}
	}

	public int getLengthOfDay() {
		TimingCondition cond = getDayEndingCondition(MaxRoundsDayEndingCondition.class);

		if (cond != null) {
			return ((MaxRoundsDayEndingCondition) cond).getLengthOfDay();
		} else {
			return -1;
		}
	}

	public void setLengthOfDay(int lengthOfDay) {
		MaxRoundsDayEndingCondition cond = new MaxRoundsDayEndingCondition(
				market);
		cond.setLengthOfDay(lengthOfDay);
		setDayEndingCondition(cond);
	}

	public int getMaximumDays() {
		TimingCondition cond = getAuctionClosingCondition(MaxDaysAuctionClosingCondition.class);

		if (cond != null) {
			return ((MaxDaysAuctionClosingCondition) cond).getMaximumDays();
		} else {
			return -1;
		}
	}

	public void setMaximumRounds(int maximumRounds) {
		MaxRoundsAuctionClosingCondition cond = new MaxRoundsAuctionClosingCondition(
				market);
		cond.setMaximumRounds(maximumRounds);
		setAuctionClosingCondition(cond);
	}

	public int getMaximumRounds() {
		TimingCondition cond = getAuctionClosingCondition(MaxRoundsAuctionClosingCondition.class);

		if (cond != null) {
			return ((MaxRoundsAuctionClosingCondition) cond).getMaximumRounds();
		} else {
			return -1;
		}
	}

	public void setMaximumDays(int maximumDays) {
		MaxDaysAuctionClosingCondition cond = new MaxDaysAuctionClosingCondition(
				market);
		cond.setMaximumDays(maximumDays);
		setAuctionClosingCondition(cond);
	}

	private TimingCondition getTimingCondition(TimingCondition cond,
			Class conditionClass) {
		if (cond != null) {
			if (cond.getClass().equals(conditionClass)) {
				return cond;
			} else if (cond instanceof CombiTimingCondition) {
				Iterator i = ((CombiTimingCondition) cond).conditionIterator();
				while (i.hasNext()) {
					TimingCondition c = (TimingCondition) i.next();
					if (c.getClass().equals(conditionClass)) {
						return c;
					}
				}
			}
		}

		return null;
	}

	public TimingCondition getAuctionClosingCondition(Class conditionClass) {
		return getTimingCondition(closingCondition, conditionClass);
	}

	public TimingCondition getDayEndingCondition(Class conditionClass) {
		return getTimingCondition(dayEndingCondition, conditionClass);
	}

	public void setAuctionClosingCondition(TimingCondition cond) {
		assert (cond instanceof AuctionClosingCondition);
		closingCondition = cond;
	}

	public void setDayEndingCondition(TimingCondition cond) {
		assert (cond instanceof DayEndingCondition);
		dayEndingCondition = cond;
	}

	public Market getMarket() {
		return market;
	}

	public void setMarket(Market market) {
		this.market = market;
	}

	@Override
	public SimulationTime getSimulationTime() {
		return new SimulationTime(age);
	}

}