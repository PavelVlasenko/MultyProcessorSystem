package multyproc.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import multyproc.task.Task;

public class DAG {
	
	private List<List<Task>> tasksByLevel = new ArrayList<>();
	
	public static DAG fromLines(List<String> lines) {
		List<Task> tasks = new ArrayList<>();
		
		for (String line : lines) {
			String[] parts = line.split(" ");
			Task task = new Task(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
			if (parts.length == 3) {
				task.setParentNumber(Integer.parseInt(parts[2]));
			}
			tasks.add(task);
		}
		
		return new DAG(tasks);
	}
	
	public DAG(List<Task> tasks) {
		List<Task> currentTopList = new ArrayList<>();
		
		List<Task> topLevel = new ArrayList<>();
		tasksByLevel.add(topLevel);
		for (Iterator<Task> iterator = tasks.iterator(); iterator.hasNext();) {
			Task task = (Task) iterator.next();
			if (task.getParentNumber() == 0) {
				topLevel.add(task);
				iterator.remove();
				currentTopList.add(task);
			}
		}

		while (!tasks.isEmpty()) {
			List<Task> currentLevel = new ArrayList<>();
			tasksByLevel.add(currentLevel);
			
			List<Task> newTopList = new ArrayList<>();
			
			for (Iterator<Task> iterator = tasks.iterator(); iterator.hasNext();) {
				Task currentTask = (Task) iterator.next();
				int parentNumber = currentTask.getParentNumber();

				for (Task parentTask : currentTopList) {
					if (parentTask.getNumber() == parentNumber) {
						currentLevel.add(currentTask);
						iterator.remove();
						currentTask.setTotalCost(currentTask.getComputationCost() + parentTask.getTotalCost());
						currentTask.setParent(parentTask);
						newTopList.add(currentTask);
						break;
					}
				}
			}
			currentTopList = newTopList;
		}
	}
	
	private List<Task> getSortedTaskInLevel(List<Task> tasks) {
		Collections.sort(tasks);
		return tasks;
	}
	
	public List<Task> getSortedTaskList() {
		List<Task> sortedList = new ArrayList<>();
		
		for (List<Task> taskList : tasksByLevel) {
			sortedList.addAll(getSortedTaskInLevel(taskList));
		}
		
		return sortedList;
	}
}
