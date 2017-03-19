package multyproc.main;

import java.io.IOException;
import java.util.TreeMap;

import multyproc.Config;
import multyproc.data.CloudDataProcessor;
import multyproc.graph.DAG;
import multyproc.system.MultyProcessorSystem;

public class MainClass {

	public static void main(String[] args) throws IOException {
		//generate data from cloud file
		TreeMap<Long, DAG> dags = CloudDataProcessor.generateDags();

		//create multy processor system
		MultyProcessorSystem multyProcessorSystem = new MultyProcessorSystem(Config.processorCount, dags);

		//start simulation
		multyProcessorSystem.startSimulation();
	}
}
