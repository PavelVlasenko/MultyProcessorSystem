package multyproc.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class InputGenerator {
	
	private static Random random = new Random(System.currentTimeMillis());

	public static void main(String[] args) throws IOException {
		int processorCount = 4;
		int taskCount = 100;

		int maxTaskWithoutParent = taskCount / 4;
		int minTaskWithoutParent = taskCount / 8;
		
		int minCC = 5;
		int maxCC = 25;

		int taskWithoutParentCount = nextIntInRange(minTaskWithoutParent, maxTaskWithoutParent);

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("F:/input.txt")));
		
		writer.append(String.valueOf(processorCount) + "\r\n");
		
		//tasks without parent task
		for (int i = 0; i < taskWithoutParentCount; i++) {
			writer.append(getBaseTaskString(i + 1, nextIntInRange(minCC, maxCC)) + "\r\n");
		}
		
		//other tasks
		for (int i = taskWithoutParentCount; i < taskCount; i++) {
			int parentNumber = nextIntInRange(1, i);
			String taskString = getBaseTaskString(i + 1, nextIntInRange(minCC, maxCC)) + " " + 
				String.valueOf(parentNumber);
			writer.append(taskString + "\r\n");
		}
		
		writer.close();
	}
	
	private static int nextIntInRange(int min, int max) {
		return random.nextInt(max - min) + min;
	}
	
	private static String getBaseTaskString(int taskNumber, int cc) {
		return String.valueOf(taskNumber) + " " + String.valueOf(cc);
	}
}
