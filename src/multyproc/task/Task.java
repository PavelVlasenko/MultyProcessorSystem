package multyproc.task;

public class Task implements Comparable<Task> {

	private int number;
	private int computationCost;
	private Task parent;
	
	private int parentNumber;
	private int totalCost;
	
	public Task(int number, int computationCost) {
		this.number = number;
		this.computationCost = computationCost;
		totalCost = computationCost;
	}
	
	public Task(int number, int computationCost, Task parent) {
		this(number, computationCost);
		this.parent = parent;
	}

	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getComputationCost() {
		return computationCost;
	}
	public void setComputationCost(int computationCost) {
		this.computationCost = computationCost;
	}
	public Task getParent() {
		return parent;
	}
	public void setParent(Task parent) {
		this.parent = parent;
	}
	public int getParentNumber() {
		return parentNumber;
	}
	public void setParentNumber(int parentNumber) {
		this.parentNumber = parentNumber;
	}
	public int getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(int totalCost) {
		this.totalCost = totalCost;
	}
	
	@Override
	public String toString() {
		String value = "Task [number = " + number + ", cc = " + computationCost;
		if (parent != null) {
			value += ", parent = " + parent.getNumber(); 
		}
		value += ", total cost: " + totalCost;
		value +=  "]";
		return value;
	}

	@Override
	public int compareTo(Task o) {
		return totalCost - o.totalCost;
	}
}
