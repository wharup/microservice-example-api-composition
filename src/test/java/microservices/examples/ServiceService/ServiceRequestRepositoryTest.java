package microservices.examples.ServiceService;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;
import microservices.examples.Application;
import microservices.examples.service.ServiceRequest;
import microservices.examples.service.ServiceRequestDAO;
import microservices.examples.service.ServiceRequestService;
import microservices.examples.system.StopWatchUtil; 

@SpringBootTest(classes = Application.class)
@Slf4j
@TestInstance(Lifecycle.PER_CLASS)
class ServiceRequestRepositoryTest {

	@Autowired
	ServiceRequestDAO dao;
	
	@Autowired
	ServiceRequestService service;
	
	@BeforeAll
	void setup() {
		PageRequest pageable = PageRequest.of(0, Integer.MAX_VALUE);
		List<ServiceRequest> selectAll = service.findAllWithBatchRESTApi(pageable);
	}
	
//	void test1() throws InterruptedException {
//		PageRequest pageable = PageRequest.of(0, 3, Sort.by("updated").descending());
//		List<ServiceRequest> selectAll = dao.selectAll(pageable);
//		for (ServiceRequest s : selectAll) {
//			log.error("{}", s);
//		}
//		selectAll = dao.selectAll(PageRequest.of(0, 3, Sort.by("updated").ascending()));
//		for (ServiceRequest s : selectAll) {
//			log.error("{}", s);
//		}
//	}
//	
//	@Test
//	void test2() throws InterruptedException {
//		PageRequest pageable = PageRequest.of(0, 100, Sort.by("statusName").descending());
//		List<ServiceRequest> selectAll = dao.selectAllWithJoin(null, pageable);
//		for (ServiceRequest s : selectAll) {
//			log.error("{}:{}", s.getStatusName(), s);
//		}
//	}
	
	@Test
	void test3() throws InterruptedException {
		PageRequest pageable = PageRequest.of(2, 100000, Sort.by("customerName").ascending());
		List<ServiceRequest> selectAll = dao.selectAllWithJoin("COMPLAIN", pageable);
		for (ServiceRequest s : selectAll) {
//			System.out.println(String.format("%s, %s", s.getCustomerName(), s.toString()));
		}
		log.error("size {}", selectAll.size());
	}
	
	@Test
	void test4() throws InterruptedException {
		PageRequest pageable = PageRequest.of(2, 100);
		List<ServiceRequest> selectAll = service.findAllWithBatchRESTApi("COMPLAIN", pageable);
		int i = 0;
		for (ServiceRequest s : selectAll) {
			System.out.println(String.format("%s, %s", s.getCustomerName(), s.toString()));
		}
		log.error("size {}", selectAll.size());
	}
	
	@Test
	void test5() throws Exception {
		
		PageRequest pageable = PageRequest.of(0, 10000);
		StopWatch sw = new StopWatch();
		sw.start("1");
		List<ServiceRequest> selectAll = service.findServiceRequestSortedByCustomerName("COMPLAIN", pageable);
		sw.stop();
		sw.start("2");
		List<ServiceRequest> selectAll2 = service.findServiceRequestSortedByCustomerName2("COMPLAIN", pageable);
		sw.stop();
		for (ServiceRequest s : selectAll) {
			System.out.println(String.format("%s, %s", s.getCustomerName(), s.toString()));
		}
		System.out.println(String.format("--- --- --- --- --- --- --- --- --- ---"));
		for (ServiceRequest s : selectAll2) {
			System.out.println(String.format("%s, %s", s.getCustomerName(), s.toString()));
		}
		StopWatchUtil.log(sw);
		assertArrayEquals(selectAll, selectAll2);
	}
	
	@Test
	void h() {
		PageRequest pageable = PageRequest.of(0, 1);
		List<ServiceRequest> selectAll2 = service.findServiceRequestSortedByCustomerName2("COMPLAIN", pageable);
	}

	private void assertArrayEquals(List<ServiceRequest> l, List<ServiceRequest> r) {
		assertEquals(l.size(), r.size());
		for (int i = 0; i < l.size(); i++) {
			assertEquals(l, r);
		}
		
	}
}

















