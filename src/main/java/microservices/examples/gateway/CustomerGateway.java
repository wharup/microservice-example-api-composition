package microservices.examples.gateway;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import microservices.examples.system.CacheProxy;
import microservices.examples.system.CustomPageImpl;

@Component
public class CustomerGateway {

	private static final String commonUriPrefix = "http://localhost:8090";

	private CacheProxy<CustomerDTO> customerCache;
	
	RestTemplateUtil restUtil = null;

	@Autowired
	public CustomerGateway(RestTemplate restTemplate, CacheManager cacheManager) {
		super();
		this.restUtil = new RestTemplateUtil(restTemplate, commonUriPrefix);
	    this.customerCache = new CacheProxy<>(cacheManager, "customers");
	}

	public CustomerDTO[] getCustomers(Set<String> customerIds) {
		if (customerIds.isEmpty()) {
			return new CustomerDTO[0];
		}
		ResponseEntity<CustomerDTO[]> responseEntity = 
				restUtil.exchangeGet2(
						getUri("/customers/%s?batchapi", customerIds), 
						null,
						CustomerDTO[].class
						);
		if(responseEntity == null) {
			return new CustomerDTO[0];
		}
		return responseEntity.getBody();
	}

	public Page<CustomerDTO> getAllCustomers() {
		String url = getUri("/customers?sort=name,asc");
		ResponseEntity<CustomPageImpl<CustomerDTO>> responseEntity = 
				restUtil.exchange3(
						url, 
						null,
						new ParameterizedTypeReference<CustomPageImpl<CustomerDTO>>() {}
						);
		if(responseEntity == null) {
			return new CustomPageImpl<CustomerDTO>();
		}		
		return responseEntity.getBody();
	}

public Map<String, String> getCustomerNames(Set<String> customerIds) {
	
	Set<String> requestingCustomerIds = new HashSet<>();
	Map<String, String> customerNames = new HashMap<>(customerIds.size());
	
	//1. 캐시에서 고객 정보 조회 
	Iterator<String> iterator = customerIds.iterator();
	while(iterator.hasNext()) {
		String id = iterator.next();
		CustomerDTO customer = customerCache.get(id);
		if (customer != null) {
			//1.1 결과에 고객이름 저장
			customerNames.put(customer.getId(), customer.getName());
		} else {
			//1.2. 캐시에 정보가 없는 경우 조회할 목록에 추가 
			requestingCustomerIds.add(id);
		}
	}
	//2. REST API로 고객 조회 
	CustomerDTO[] customers = getCustomers(requestingCustomerIds);
	for (CustomerDTO customer : customers) {
		//2.1 결과에 고객이름 저장
		customerNames.put(customer.getId(), customer.getName());
		//2.2 캐시에 고객정보 저장 
		customerCache.put(customer.getId(), customer);
	}
	//3. 결과 반환
	return customerNames;
}

private String getIdString(Set<String> ids) {
		String typeString = "";
		for (String codeType : ids) {
			typeString += codeType + ",";
		}
		return typeString;
	}
	
	private String getUri(String format, Object... args) {
		return String.format(commonUriPrefix + format, args);
	}
	
	private String getUri(String format, Set<String> ids) {
		return String.format(commonUriPrefix + format, getIdString(ids));
	}
	
}
