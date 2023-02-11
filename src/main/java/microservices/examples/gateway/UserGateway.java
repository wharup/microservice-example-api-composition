package microservices.examples.gateway;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import microservices.examples.system.CacheProxy;

@Component
public class UserGateway {

	private static final String commonUriPrefix = "http://localhost:8090";

    private CacheProxy<UserDTO> userCache;
    
	RestTemplateUtil restUtil = null;

	@Autowired
	public UserGateway(RestTemplate restTemplate, CacheManager cacheManager) {
		super();
		this.restUtil = new RestTemplateUtil(restTemplate, commonUriPrefix);
	    this.userCache = new CacheProxy<>(cacheManager, "users");
	}

	private UserDTO[] getUsers(Set<String> userIds) {
		if (userIds.isEmpty()) {
			return new UserDTO[0];
		}
		String typeString = getIdString(userIds);
		String url = getCommonUri("/users/%s?batchapi", typeString);
		ResponseEntity<UserDTO[]> responseEntity = 
				restUtil.exchangeGet6(
						url, 
						null,
						UserDTO[].class
						);
		if(responseEntity == null) {
			return new UserDTO[0];
		}
		return responseEntity.getBody();
	}

	public Map<String, String> getUserNames(Set<String> userIds) {
		Map<String, String> names = new HashMap<>();
		Iterator<String> iterator = userIds.iterator();
		while(iterator.hasNext()) {
			UserDTO user = getUserCache().get(iterator.next());
			if (user != null) {
				names.put(user.getId(), user.getName());
				iterator.remove();
			}
		}
		UserDTO[] users = getUsers(userIds);
		for (UserDTO user : users) {
			names.put(user.getId(), user.getName());
			getUserCache().put(user.getId(), user);
		}
		return names;
	}
	
	private String getIdString(Set<String> ids) {
		String typeString = "";
		for (String codeType : ids) {
			typeString += codeType + ",";
		}
		return typeString;
	}

	private CacheProxy<UserDTO> getUserCache() {
		return userCache;
	}
	
	private String getCommonUri(String format, Object... args) {
		return String.format(commonUriPrefix + format, args);
	}
	

	
	
}
