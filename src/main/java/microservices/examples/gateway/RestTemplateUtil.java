package microservices.examples.gateway;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import microservices.examples.system.CustomPageImpl;


//TODO: 각 Gateway 클래스로 보낼 것!

@Slf4j
public class RestTemplateUtil {

	private String uriPrefix = "http://localhost:8090";

	private RestTemplate restTemplate;

	public RestTemplateUtil(RestTemplate restTemplate, String uriPrefix) {
		this.restTemplate = restTemplate;
		this.uriPrefix = uriPrefix;
	}

	public ResponseEntity<CodeDTO[]> exchangeGet(String format, String typeString, HttpEntity<Object> httpEntity,
			Class<CodeDTO[]> class1) {
		ResponseEntity<CodeDTO[]> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(getUri(format, typeString), HttpMethod.GET, httpEntity, class1);
		} catch (HttpServerErrorException e) {
			log.error("{}", e);
			return null;
		} catch (ResourceAccessException e) {
			Throwable cause = e.getCause();
			if (cause instanceof ConnectException) {
				log.error("{}", cause.getMessage());
			} else if (cause instanceof SocketTimeoutException) {
				log.error("{}", cause.getMessage());
			} else if (cause instanceof InterruptedException) {
				log.error("{}", cause.getMessage());
			} else {
				log.error("{}", cause.getMessage());
			}
			return null;
		}
		return responseEntity;
	}
	
	public ResponseEntity<CustomerDTO[]> exchangeGet2(String uri, HttpEntity<Object> httpEntity,
			Class<CustomerDTO[]> class1) {
		ResponseEntity<CustomerDTO[]> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, class1);
		} catch (HttpServerErrorException e) {
			log.error("{}", e);
			return null;
		} catch (ResourceAccessException e) {
			Throwable cause = e.getCause();
			if (cause instanceof ConnectException) {
				log.error("{}", cause.getMessage());
			} else if (cause instanceof SocketTimeoutException) {
				log.error("{}", cause.getMessage());
			} else if (cause instanceof InterruptedException) {
				log.error("{}", cause.getMessage());
			} else {
				log.error("{}", cause.getMessage());
			}
			return null;
		}
		return responseEntity;
	}
	
	private String getUri(String format, Object... args) {
		return String.format(uriPrefix + format, args);
	}

	public ResponseEntity<CustomPageImpl<CustomerDTO>> exchange3(String uri, HttpEntity<Object> httpEntity,
			ParameterizedTypeReference<CustomPageImpl<CustomerDTO>> parameterizedTypeReference) {
		ResponseEntity<CustomPageImpl<CustomerDTO>> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, parameterizedTypeReference);
		} catch (HttpServerErrorException e) {
			log.error("{}", e);
			return null;
		} catch (ResourceAccessException e) {
			Throwable cause = e.getCause();
			if (cause instanceof ConnectException) {
				log.error("{}", cause.getMessage());
			} else if (cause instanceof SocketTimeoutException) {
				log.error("{}", cause.getMessage());
			} else if (cause instanceof InterruptedException) {
				log.error("{}", cause.getMessage());
			} else {
				log.error("{}", cause.getMessage());
			}
			return null;
		}
		return responseEntity;
	}

	public ResponseEntity<DepartmentDTO[]> exchangeGet5(String uri, HttpEntity<Object> httpEntity, Class<DepartmentDTO[]> class1) {
		ResponseEntity<DepartmentDTO[]> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, class1);
		} catch (HttpServerErrorException e) {
			log.error("{}", e);
			return null;
		} catch (ResourceAccessException e) {
			Throwable cause = e.getCause();
			if (cause instanceof ConnectException) {
				log.error("{}", cause.getMessage());
			} else if (cause instanceof SocketTimeoutException) {
				log.error("{}", cause.getMessage());
			} else if (cause instanceof InterruptedException) {
				log.error("{}", cause.getMessage());
			} else {
				log.error("{}", cause.getMessage());
			}
			return null;
		}
		return responseEntity;
	}

	public ResponseEntity<UserDTO[]> exchangeGet6(String uri, HttpEntity<Object> httpEntity, Class<UserDTO[]> class1) {
		ResponseEntity<UserDTO[]> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, class1);
		} catch (HttpServerErrorException e) {
			log.error("{}", e);
			return null;
		} catch (ResourceAccessException e) {
			Throwable cause = e.getCause();
			if (cause instanceof ConnectException) {
				log.error("{}", cause.getMessage());
			} else if (cause instanceof SocketTimeoutException) {
				log.error("{}", cause.getMessage());
			} else if (cause instanceof InterruptedException) {
				log.error("{}", cause.getMessage());
			} else {
				log.error("{}", cause.getMessage());
			}
			return null;
		}
		return responseEntity;
	}


}
