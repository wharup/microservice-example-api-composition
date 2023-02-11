package microservices.examples.gateway;

import java.time.Instant;

import lombok.Data;

@Data
public class DepartmentDTO {
	private String id;
	private String name;
	private Instant created;
	private Instant updated;
}
