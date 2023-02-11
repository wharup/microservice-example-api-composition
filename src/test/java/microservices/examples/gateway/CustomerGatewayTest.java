package microservices.examples.gateway;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;
import microservices.examples.Application;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@Slf4j
class CustomerGatewayTest {
	@Autowired
	CustomerGateway gateway;

	@Test
	void test() {
		Set<String> ids = new HashSet<>();
		ids.add("9248b186-fb79-4bc3-bc7e-c9b404fc6038");
		ids.add("0e1b3a09-d858-476a-af10-41140bb447db");
		ids.add("7738a5af-7212-41e4-a53a-6be8d9da533a");
		ids.add("ef2bc56c-faee-4494-938e-3f3c3e8c1bb6");
		ids.add("d38e6e64-a45b-4fbf-8470-8436d3d0ca0c");
		ids.add("746f72ca-179f-434f-ba4e-7b68867d635c");
		ids.add("d522a6ce-8e26-409b-89ab-b9a807f74ea3");
		
		CustomerDTO[] customers = gateway.getCustomers(ids);
		for (CustomerDTO customer : customers) {
			log.error("customer {}", customer);
		}
	}

}
