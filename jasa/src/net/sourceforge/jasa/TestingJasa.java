package net.sourceforge.jasa;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.Normal;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister64;

import net.sourceforge.jabm.AgentList;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.mixing.RandomArrivalAgentMixer;
import net.sourceforge.jabm.report.BatchMetaReport;
import net.sourceforge.jabm.report.PayoffByStrategy;
import net.sourceforge.jabm.report.Report;
import net.sourceforge.jabm.util.AbsoluteContinuousDistribution;
import net.sourceforge.jabm.util.SummaryStats;
import net.sourceforge.jasa.agent.MarketAgentInitialiser;
import net.sourceforge.jasa.agent.SimpleTradingAgent;
import net.sourceforge.jasa.agent.TokenTradingAgent;
import net.sourceforge.jasa.agent.strategy.FixedPriceStrategy;
import net.sourceforge.jasa.agent.strategy.RandomConstrainedStrategy;
import net.sourceforge.jasa.agent.strategy.RandomUnconstrainedStrategy;
import net.sourceforge.jasa.agent.strategy.ReturnForecastStrategy;
import net.sourceforge.jasa.agent.strategy.TruthTellingStrategy;
import net.sourceforge.jasa.agent.valuation.DailyRandomValuer;
import net.sourceforge.jasa.agent.valuation.FundamentalistForecaster;
import net.sourceforge.jasa.agent.valuation.NoiseTraderForecaster;
import net.sourceforge.jasa.agent.valuation.RandomValuer;
import net.sourceforge.jasa.agent.valuation.ValuationPolicy;
import net.sourceforge.jasa.market.MarketFacade;
import net.sourceforge.jasa.market.MarketSimulation;
import net.sourceforge.jasa.market.auctioneer.ContinuousDoubleAuctioneer;
import net.sourceforge.jasa.market.rules.TimePriorityPricingPolicy;
import net.sourceforge.jasa.report.AgentPayoffReport;
import net.sourceforge.jasa.report.DailyStatsReport;
import net.sourceforge.jasa.report.EquilibriumReport;
import net.sourceforge.jasa.report.HistoricalDataReport;
import net.sourceforge.jasa.report.MidPointTimeSeriesReport;
import net.sourceforge.jasa.report.OrderFlowTimeSeriesReport;
import net.sourceforge.jasa.report.PriceStatisticsReport;
import net.sourceforge.jasa.report.TransactionPriceTimeSeriesReport;

public class TestingJasa {


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		MarketFacade marketFacade = new MarketFacade();

		TimePriorityPricingPolicy pricingPolicy = new TimePriorityPricingPolicy();
		ContinuousDoubleAuctioneer auctioneer = new ContinuousDoubleAuctioneer(
				marketFacade);
		auctioneer.setPricingPolicy(pricingPolicy);

		SimulationController simulationController = new SimulationController();
		simulationController.setNumSimulations(1);

		TransactionPriceTimeSeriesReport transPriceReport = new TransactionPriceTimeSeriesReport(
				"data/Price");


		ArrayList<Report> reports = new ArrayList<Report>();
		reports.add(transPriceReport);


		simulationController.setReports(reports);

		marketFacade.setController(simulationController);
		marketFacade.setAuctioneer(auctioneer);

		MersenneTwister64 randomEngine = new MersenneTwister64(new Date());
		RandomArrivalAgentMixer agentMixer = new RandomArrivalAgentMixer(
				randomEngine);

		agentMixer.setArrivalProbability(0.2);

		MarketAgentInitialiser agentInitialiser = new MarketAgentInitialiser();
		agentInitialiser.setMarket(marketFacade);

		AgentList agentList = new AgentList();

		Population population = new Population();

		MarketSimulation marketSimulation = new MarketSimulation();
		marketSimulation.setSimulationController(simulationController);
		marketSimulation.setMarket(marketFacade);
		marketSimulation.setMaximumDays(2);
		marketSimulation.setLengthOfDay(10000);
		marketSimulation.setPopulation(population);
		marketSimulation.setAgentMixer(agentMixer);
		marketSimulation.setAgentInitialiser(agentInitialiser);

		agentInitialiser.initialise(population);



		for (int i = 0; i < 500; i++) {

			TokenTradingAgent agent = new TokenTradingAgent(0, 1000,
					marketFacade);

			TruthTellingStrategy strategy = new TruthTellingStrategy();
			strategy.setAgent(agent);
			strategy.setQuantity(100);

			agent.setStrategy(strategy);
			agent.setIsBuyer(true);

			ValuationPolicy valuationPolicy = new DailyRandomValuer(50, 55,
					randomEngine);
			agent.setValuationPolicy(valuationPolicy);

			agentList.add(agent);
		}

		for (int i = 0; i < 500; i++) {

			TokenTradingAgent agent = new TokenTradingAgent(0, 1000,
					marketFacade);

			TruthTellingStrategy strategy = new TruthTellingStrategy();
			strategy.setAgent(agent);
			strategy.setQuantity(100);

			agent.setStrategy(strategy);
			agent.setIsBuyer(false);

			ValuationPolicy valuationPolicy = new DailyRandomValuer(50, 55,
					randomEngine);
			agent.setValuationPolicy(valuationPolicy);

			agentList.add(agent);
		}

		population.setPrng(randomEngine);
		population.setAgentList(agentList);

		simulationController.setSimulation(marketSimulation);

		simulationController.run();

	}
}