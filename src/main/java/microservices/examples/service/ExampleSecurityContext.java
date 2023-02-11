package microservices.examples.service;

public class ExampleSecurityContext {

	public static UserDetails getCurrentLoginUser() {
		return new UserDetails();
	}

}
