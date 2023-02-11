package microservices.examples.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.util.StopWatch;
import org.springframework.util.StopWatch.TaskInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StopWatchUtil {
	static List<StopWatch> sws = new ArrayList<>();

	static StopWatch sw = new StopWatch();
	
	public static void create(String msg) {
		sw = new StopWatch(msg);
	}
	
	public static void start(String msg) {
		sw.start(msg);
	}

	public static void stop() {
		sw.stop();
	}

	public static StopWatch get() {
		return sw;
	}

	public static void log(StopWatch sw) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n----- ----- ----- ----- ----- ----- -----\n");
		sb.append(String.format("Watch Name: %s\n", sw.getId()));
		int index = 0;
		for (TaskInfo t : sw.getTaskInfo() ) {
			float insec = (float)t.getTimeMillis() / 1000;
			sb.append(String.format("[%d]	%3f	%s\n", ++index, insec, t.getTaskName()));
		}
		sb.append("----- ----- ----- ----- ----- ----- -----\n");
		log.error("{}", sb.toString());
	}

	public static void logGroupByTaskName(StopWatch sw) {
		SortedMap<String, Stat> stats = new TreeMap<>();
		for (TaskInfo t : sw.getTaskInfo() ) {
			Stat stat = get(stats, t);
			stat.count++;
			stat.totalTime += t.getTimeMillis();
		}
		
		int index = 0;
		StringBuffer sb = new StringBuffer();
		sb.append("\n----- ----- ----- ----- ----- ----- -----\n");
		sb.append("watch name : " + sw.getId() + "\n");
		for (String taskName : stats.keySet()) {
			Stat stat = stats.get(taskName);
			float insec = (float)stat.totalTime / 1000;
			sb.append(String.format("[%d]	%3f	%s (%d)\n", 
					++index, insec, taskName, stat.count));
		}
		sb.append("----- ----- ----- ----- ----- ----- -----\n");
		log.error("{}", sb.toString());
	}

	private static Stat get(Map<String, Stat> stats, TaskInfo t) {
		Stat stat = stats.get(t.getTaskName());
		if (stat == null) {
			stat = new Stat();
			stats.put(t.getTaskName(), stat);
		}
		return stat;
	}

	public static void add(StopWatch sw) {
		sws.add(sw);
	}

	public static void reset() {
		sws = new ArrayList<>();
	}
	
	public static void log() {
		for(StopWatch sw : sws) {
			log(sw);
		}
		if (sw.getTaskCount() > 0) {
			log(sw);
		}
	}
}

class Stat {
	int count;
	long totalTime;
}