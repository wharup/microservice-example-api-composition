package microservices.examples.ServiceService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import microservices.examples.Application;
import microservices.examples.gateway.DepartmentGateway; 

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class DepartmentGatewayTest {

	@Autowired
	DepartmentGateway departmentGateway;
	
	@BeforeEach
	public void setup() {
		DepartmentGateway.setUpdateDuration(1L);
	}
	
	@Test
	void 업데이트주기이전에는_체크하지않음() throws InterruptedException {
		departmentGateway.loadAllDepartments();
		Instant lastCheckedTime = departmentGateway.getLastCheckedTime();
		Thread.sleep(10);
		departmentGateway.loadAllDepartments();
		Instant lastCheckedTime2 = departmentGateway.getLastCheckedTime();
		assertEquals(lastCheckedTime, lastCheckedTime2);
	}
	
	
	@Test
	void 업데이트주기에는_체크() throws InterruptedException {
		departmentGateway.loadAllDepartments();
		Instant lastCheckedTime = departmentGateway.getLastCheckedTime();
		Thread.sleep(1010);
		departmentGateway.loadAllDepartments();
		Instant lastCheckedTime2 = departmentGateway.getLastCheckedTime();
		assertNotEquals(lastCheckedTime, lastCheckedTime2);
	}

	@Test
	void 서버에서변경된경우_재로딩() throws InterruptedException {
		departmentGateway.loadAllDepartments();
		long ifModifiedSince = departmentGateway.getIfModifiedSince();
		departmentGateway.setIfModifiedSince(ifModifiedSince - 1000000);
		Thread.sleep(1010);
		departmentGateway.loadAllDepartments();
		long ifModifiedSince2 = departmentGateway.getIfModifiedSince();
		assertEquals(ifModifiedSince, ifModifiedSince2);
	}

	@Test
	void 서버에서변경된경우_로딩안함() throws InterruptedException {
		departmentGateway.loadAllDepartments();
		long ifModifiedSince = departmentGateway.getIfModifiedSince();
		Thread.sleep(1010);
		departmentGateway.loadAllDepartments();
		long ifModifiedSince2 = departmentGateway.getIfModifiedSince();
		assertEquals(ifModifiedSince, ifModifiedSince2);
	}
	
}

















