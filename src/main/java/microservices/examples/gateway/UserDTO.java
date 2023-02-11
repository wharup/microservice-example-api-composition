package microservices.examples.gateway;

import java.time.Instant;

import lombok.Data;

@Data
public class UserDTO {
	private String id;
	private String name;
	private String phoneNumber;
	private String email;
	private String departmentId;
	private Instant created;
	private Instant updated;
}
