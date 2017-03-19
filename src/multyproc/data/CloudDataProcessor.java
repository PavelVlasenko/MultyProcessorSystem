package multyproc.data;

import multyproc.Config;
import multyproc.graph.DAG;
import multyproc.task.Task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CloudDataProcessor {

    private static Random random = new Random(System.currentTimeMillis());

    public static TreeMap<Long, DAG> generateDags() {
        TreeMap<Long, DAG> dags = new TreeMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(Config.cloudDataPath));
            String s;
            List<Task> taskList = new ArrayList<>();
            long arrivalTime = 0;
            SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat sd2 = new SimpleDateFormat("MM/dd-HH:mm:ss");
            while ((s = br.readLine()) != null) {
                String jobState = substringBetween(s, "JobState=", " ");
                String jobId = substringBetween(s, "JobId=", " ");
                String startTime = substringBetween(s, "StartTime=", " ");
                String endTime = substringBetween(s, "EndTime=", " ");
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-hh'T'HH:mm:ss");
                if (!"COMPLETED".equals(jobState) || startTime.equals(endTime)) {
                    continue;
                }
                Date startDate = null;
                Date endDate = null;
                try {
                    startDate = sd1.parse(startTime);
                    endDate = sd1.parse(endTime);
                } catch (ParseException e) {
                    try {
                        startDate = sd2.parse(startTime);
                        endDate = sd2.parse(endTime);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
                if (taskList.size() == 0) {
                    arrivalTime = startDate.getTime();
                }
                long executionTime = endDate.getTime() - startDate.getTime();
                if (executionTime <= 0 || executionTime > 1000000000) {
                    continue;
                }
                executionTime = executionTime / Config.executionTimeDelimeter;
                Task task = new Task(Integer.valueOf(jobId), (int)executionTime);
                taskList.add(task);
                if (taskList.size() == Config.taskCount) {
                    System.out.println("Add Task List");
                    System.out.println("Arrival time = " + arrivalTime);
                    setParentTasks(taskList);
                    dags.put(arrivalTime, new DAG(taskList));
                    System.out.println("Dags size = " + dags.size());
                    taskList = new ArrayList<>();
                }
            }
            return normalizeArrivalTime(dags);
        }
        catch (Exception e) {
            System.out.println("Exception while parse cloud data");
            throw new RuntimeException(e);
        }
    }

    private static void setParentTasks(List<Task> taskList) {
        int taskCount = taskList.size();
        int maxTaskWithoutParent = taskCount / 4;
        int minTaskWithoutParent = taskCount / 8;
        int taskWithoutParentCount = nextIntInRange(minTaskWithoutParent, maxTaskWithoutParent);

        for (int i = taskWithoutParentCount; i < taskCount; i++) {
            int parentNumber = nextIntInRange(0, i-1);
            taskList.get(i).setParentNumber(taskList.get(parentNumber).getNumber());
            System.out.println("Set parent number " + parentNumber + " to the task " + i);
        }
    }

    private static TreeMap<Long, DAG> normalizeArrivalTime(TreeMap<Long, DAG> dags) {
        long baseArrivalTime = dags.entrySet().iterator().next().getKey();
        TreeMap<Long, DAG> normalizedDags = new TreeMap<>();
        for(Map.Entry<Long, DAG> entry : dags.entrySet()) {
            normalizedDags.put((entry.getKey() - baseArrivalTime)/Config.arrivalTimeGap, entry.getValue());
        }
        return normalizedDags;
    }

    private static int nextIntInRange(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    private static String substringBetween(String str, String open, String close) {
        if(str != null && open != null && close != null) {
            int start = str.indexOf(open);
            if(start != -1) {
                int end = str.indexOf(close, start + open.length());
                if(end != -1) {
                    return str.substring(start + open.length(), end);
                }
            }

            return null;
        } else {
            return null;
        }
    }
}
