package multyproc.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import multyproc.graph.DAG;
import multyproc.system.MultyProcessorSystem;
import multyproc.task.Task;

public class MainClass {

	public static void main(String[] args) throws IOException {
		//read input data
		BufferedReader br = new BufferedReader(new FileReader(new File("F:\\input.txt")));
		int processorCount = Integer.parseInt(br.readLine());
		List<String> lines = new ArrayList<>();
		String line = null;
		while ((line = br.readLine()) != null) {
			lines.add(line);
		}
		br.close();
		
		//create random dag
		DAG dag = DAG.fromLines(lines);

		//create multy processor system
		MultyProcessorSystem multyProcessorSystem = new MultyProcessorSystem(processorCount);
		
		//sort tasks
		List<Task> sortedTasksList = dag.getSortedTaskList();
		
		//put tasks into system
		multyProcessorSystem.putTasks(sortedTasksList);
		
		//redirect output info into log file
		System.setOut(new PrintStream(new File("F://output.txt")));
		
		//start simulation
		multyProcessorSystem.startSimulation();
	}
}
