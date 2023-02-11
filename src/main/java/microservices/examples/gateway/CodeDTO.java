package microservices.examples.gateway;

import java.time.Instant;

import lombok.Data;

@Data
public class CodeDTO {
	private String codeType;
	private String code;
	private String value;
	private String active;
	private Instant created;
	private Instant updated;
}
