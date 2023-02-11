package microservices.examples.ServiceService;

import static org.mockito.Mockito.after;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class TempTest {

	Map<String, String> data = new HashMap<>();

	@BeforeEach
	void setup() {
//		for (int i = 0; i < 100000; i++) {
//			data.put(String.valueOf(i), "hshshshsh");
//		}
	}
//	
//	@Test
//	void test() {
//		for (String key : data.keySet()) {
//			String a = data.get(key);
//		}
//	}

//	@Test
//	void checkMemorySize_1000000000() {
//		long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
//		int count = 1000 * 1000 * 1000;
//		for (int i = 0; i < count; i++) {
//			data.put(String.valueOf(i), "hshshshsh");
//		}
//		long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
//		log.error("{}:{}-{}={}", count, beforeUsedMem/1024, afterUsedMem/1024, (afterUsedMem - beforeUsedMem)/1024);
//	}

	@Test
	void checkMemorySize_100000000() {
		long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		int count = 1000 * 1000 * 100;
		for (int i = 0; i < count; i++) {
			data.put(String.valueOf(i), "hshshshsh");
		}
		long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		log.error("{}:{}-{}={}", count, beforeUsedMem/1024, afterUsedMem/1024, (afterUsedMem - beforeUsedMem)/1024);
	}
	
	@Test
	void checkMemorySize_10000000() {
		long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		int count = 1000 * 1000 * 10;
		for (int i = 0; i < count; i++) {
			data.put(String.valueOf(i), "hshshshsh");
		}
		long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		log.error("{}:{}-{}={}", count, beforeUsedMem/1024, afterUsedMem/1024, (afterUsedMem - beforeUsedMem)/1024);
	}
	
	
	@Test
	void checkMemorySize_1000000() {
		long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		int count = 1000 * 1000;
		for (int i = 0; i < count; i++) {
			data.put(String.valueOf(i), "hshshshsh");
		}
		long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		log.error("{}:{}-{}={}", count, beforeUsedMem/1024, afterUsedMem/1024, (afterUsedMem - beforeUsedMem)/1024);
	}
	@Test
	void checkMemorySize_100000() {
		long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		int count = 1000 * 100;
		for (int i = 0; i < count; i++) {
			data.put(String.valueOf(i), "hshshshsh");
		}
		long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		log.error("{}:{}-{}={}", count, beforeUsedMem/1024, afterUsedMem/1024, (afterUsedMem - beforeUsedMem)/1024);
	}
	@Test
	void checkMemorySize_10000() {
		long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		int count = 1000 * 10;
		for (int i = 0; i < count; i++) {
			data.put(String.valueOf(i), "hshshshsh");
		}
		long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		log.error("{}:{}-{}={}", count, beforeUsedMem/1024, afterUsedMem/1024, (afterUsedMem - beforeUsedMem)/1024);
	}
	
	@Test
	void checkMemorySize_1000() {
		long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		int count = 1000;
		for (int i = 0; i < count; i++) {
			data.put(String.valueOf(i), "hshshshsh");
		}
		long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		log.error("{}:{}-{}={}", count, beforeUsedMem/1024, afterUsedMem/1024, (afterUsedMem - beforeUsedMem)/1024);
	}
	
}
