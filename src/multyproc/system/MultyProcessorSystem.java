package multyproc.system;

import java.util.*;

import multyproc.Config;
import multyproc.graph.DAG;
import multyproc.task.Task;

public class MultyProcessorSystem {

	private LinkedHashMap<Long, DAG> dags;

	private class Processor {
		
		private int number;

		private Queue<Task> processorTaskQueue;
		private ProcessorState state = ProcessorState.IDLE;
		
		private Task currentTask;
		private int computationCostRemaining; 
		
		public Processor(int number) {
			this.number = number;
			processorTaskQueue = new LinkedList<>();
		}
		
		@Override
		public String toString() {
			return "Processor-" + number;
		}
		
		private boolean isParentCompleted() {
			if (currentTask.getParent() == null) {
				return true;
			}
			return completedTaskQueue.contains(currentTask.getParent());
		}
		
		private void startTask() {
			System.out.println(this + " starts " + currentTask + " at " + systemTime);
			computationCostRemaining = currentTask.getComputationCost();
			state = ProcessorState.IN_WORK;
			processorTaskQueue.poll(); //remove task only if processor is ready to execute it
		}
		
		private void doTaskWork() {
			computationCostRemaining--;
			if (computationCostRemaining == 0) {
				completedTaskQueue.add(currentTask);
				System.out.println(this + " completes " + currentTask + " at " + systemTime);
				currentTask = null;
				completedTaskCount++;
				state = ProcessorState.IDLE;
				doProcessorWork(); //get next task
			}
		}
		
		public void doProcessorWork() {
			if (state == ProcessorState.IDLE) {
				currentTask = processorTaskQueue.peek();
				if (currentTask == null) {
					return;
				} else {
					if (!isParentCompleted()) {
						state = ProcessorState.WAIT;
					} else {
						startTask();
					}
				}
			} else if (state == ProcessorState.WAIT) {
				if (isParentCompleted()) {
					startTask();
				}
			} else { //IN_WORK
				doTaskWork();
			}
		}
	}
	
	private Queue<Task> completedTaskQueue = new LinkedList<>();
	private List<Processor> processors = new ArrayList<>();
	
	private int systemTime;
	
	private int totalTaskCount;
	private int totalComputationCost;
	private int completedTaskCount;
	private List<Task> sortedTaskList = new ArrayList<>();

	public MultyProcessorSystem(int processorCount, LinkedHashMap<Long, DAG> dags) {
		if (processorCount <= 0) {
			throw new IllegalArgumentException("Processor count");
		}
		for (int i = 0; i < processorCount; i++) {
			processors.add(new Processor(i + 1));
		}
		this.dags = dags;
		this.totalTaskCount = dags.size()* Config.taskCount;
	}
	
	public void startSimulation() {
		System.out.println("sorted task list: ");
		for (Task task : sortedTaskList) {
			System.out.println(task);
		}
		
		systemTime = 0;
		completedTaskCount = 0;
		while (completedTaskCount < totalTaskCount) {
			systemTime++;
			processDag();
			for (Processor processor : processors) {
				processor.doProcessorWork();
			}
		}
		System.out.println("simulation finished in " + systemTime);
		System.out.println("total computation cost: " + totalComputationCost);
		System.out.println("speedup: " + String.format("%.2f", 1.0 * totalComputationCost / systemTime));
	}

	private void processDag() {
		if(dags.isEmpty()) {
			return;
		}
		Map.Entry<Long, DAG> entry = dags.entrySet().iterator().next();
		if(entry.getKey() >= systemTime) {
			putTasks(entry.getValue().getSortedTaskList());
			dags.remove(entry.getKey());
		}
	}
	
	public void putTasks(List<Task> sortedTaskList) {
		this.sortedTaskList.addAll(sortedTaskList);
		int processorIndex = 0;
		for (Task task : sortedTaskList) {
			totalComputationCost += task.getComputationCost();
			Processor processor = getProcessor(processorIndex);
			processor.processorTaskQueue.add(task);
			processorIndex++;
			if (processorIndex == processors.size()) {
				processorIndex = 0;
			}
		}
	}
	
	public Processor getProcessor(int index) {
		return processors.get(index);
	}
	
	public Queue<Task> getCompletedTaskQueue() {
		return completedTaskQueue;
	}
}
