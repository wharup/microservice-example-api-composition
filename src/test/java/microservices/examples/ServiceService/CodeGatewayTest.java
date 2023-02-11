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
import microservices.examples.gateway.CodeGateway; 

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class CodeGatewayTest {

	@Autowired
	CodeGateway codeGateway;
	
	@BeforeEach
	public void setup() {
		CodeGateway.setUpdateDuration(1L);
	}
	
	@Test
	void 업데이트주기이전에는_체크하지않음() throws InterruptedException {
		codeGateway.loadCodeNamesIfUpdated("SR_TYPE", "SR_STATUS");
		Instant lastCheckedTime = codeGateway.getLastCheckedTime();
		Thread.sleep(10);
		codeGateway.loadCodeNamesIfUpdated("SR_TYPE", "SR_STATUS");
		Instant lastCheckedTime2 = codeGateway.getLastCheckedTime();
		assertEquals(lastCheckedTime, lastCheckedTime2);
	}
	
	@Test
	void 업데이트주기이에는_체크() throws InterruptedException {
		codeGateway.loadCodeNamesIfUpdated("SR_TYPE", "SR_STATUS");
		Instant lastCheckedTime = codeGateway.getLastCheckedTime();
		Thread.sleep(1010);
		codeGateway.loadCodeNamesIfUpdated("SR_TYPE", "SR_STATUS");
		Instant lastCheckedTime2 = codeGateway.getLastCheckedTime();
		assertNotEquals(lastCheckedTime, lastCheckedTime2);
	}

	@Test
	void 서버에서변경된경우_재로딩() throws InterruptedException {
		codeGateway.loadCodeNamesIfUpdated("SR_TYPE", "SR_STATUS");
		long ifModifiedSince = codeGateway.getIfModifiedSince();
		codeGateway.setIfModifiedSince(ifModifiedSince - 1000000);
		Thread.sleep(1010);
		codeGateway.loadCodeNamesIfUpdated("SR_TYPE", "SR_STATUS");
		long ifModifiedSince2 = codeGateway.getIfModifiedSince();
		assertEquals(ifModifiedSince, ifModifiedSince2);
	}

	@Test
	void 서버에서변경된경우_로딩안함() throws InterruptedException {
		codeGateway.loadCodeNamesIfUpdated("SR_TYPE", "SR_STATUS");
		long ifModifiedSince = codeGateway.getIfModifiedSince();
		Thread.sleep(1010);
		codeGateway.loadCodeNamesIfUpdated("SR_TYPE", "SR_STATUS");
		long ifModifiedSince2 = codeGateway.getIfModifiedSince();
		assertEquals(ifModifiedSince, ifModifiedSince2);
	}
	
}

















