package microservices.examples.gateway;

import lombok.Data;

@Data
public class CustomerDTO {
	private String id;
	private String name;
	private String birthday;
	private String gender;
	private String address;
	private String phone_number;
	private String type;
}
