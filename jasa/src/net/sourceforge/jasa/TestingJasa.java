package net.sourceforge.jasa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

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

	
	private static float alphaA = 0.05f;
	private static float alphaB = 0.03f;
	private static float betaA = 0.5f;
	private static float betaB = 0.1f;
	private static int totalBuyers = 500;
	private static int totalSellers = 500;
	private static int totalNews = 3;
	private static ArrayList <News> news =new ArrayList <News> ();
	private static int maximumDays = 2;
	private static int lengthOfDay = 20;
	
	
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
		marketSimulation.setMaximumDays(maximumDays);
		marketSimulation.setLengthOfDay(lengthOfDay);
		marketSimulation.setPopulation(population);
		marketSimulation.setAgentMixer(agentMixer);
		marketSimulation.setAgentInitialiser(agentInitialiser);

		agentInitialiser.initialise(population);



		for (int i = 0; i < totalBuyers; i++) {

			SimpleTradingAgent agent = new SimpleTradingAgent(0, 1000,
					marketFacade);

			TruthTellingStrategy strategy = new TruthTellingStrategy();
			strategy.setAgent(agent);
			strategy.setQuantity(100);
			strategy.setBuy(true);

			agent.setStrategy(strategy);
			agent.setAlphaA(alphaA);
			agent.setAlphaB(alphaB);
			agent.setBetaA(betaA);
			agent.setBetaB(betaB);
			agent.calcAlpha();
			agent.calcBeta();
			
			agent.setRandomEngine(randomEngine);

			ValuationPolicy valuationPolicy = new DailyRandomValuer(50, 55,
					randomEngine);
			agent.setValuationPolicy(valuationPolicy);
			
			agent.setId(i);
			
			agentList.add(agent);
		}

		for (int i = 0; i < totalSellers; i++) {

			SimpleTradingAgent agent = new SimpleTradingAgent(0, 1000,
					marketFacade);

			TruthTellingStrategy strategy = new TruthTellingStrategy();
			strategy.setAgent(agent);
			strategy.setQuantity(100);
			strategy.setBuy(false);
			agent.setStrategy(strategy);
			agent.setAlphaA(alphaA);
			agent.setAlphaB(alphaB);
			agent.setBetaA(betaA);
			agent.setBetaB(betaB);
			agent.calcAlpha();
			agent.calcBeta();
			
			agent.setRandomEngine(randomEngine);

			ValuationPolicy valuationPolicy = new DailyRandomValuer(50, 55,
					randomEngine);
			agent.setValuationPolicy(valuationPolicy);

			agent.setId(totalBuyers + i);
			
			agentList.add(agent);
		}
		
		//gerar noticias
		
		//(double receiversPer, int totalAgents)
		Random random = new Random();
		int max = maximumDays * lengthOfDay;
		int [] times = new int[totalNews];
		for (int i =  0 ; i < totalNews; i++){
			times[i] = random.nextInt(max);
		}
		Arrays.sort(times);
		for (int i =  0 ; i < totalNews; i++){
			double receiversPer = 0.1 * random.nextInt(8) + 0.1;
			news.add(new News(receiversPer, (totalBuyers + totalSellers), times[i]));
			System.out.println(news.get(i));
		}
		

		population.setPrng(randomEngine);
		population.setAgentList(agentList);

		simulationController.setSimulation(marketSimulation);

		simulationController.run();

	}
}
