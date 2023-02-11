package microservices.examples.gateway;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DepartmentGateway {

	private static final String commonUriPrefix = "http://localhost:8090";

	private static long updateDuration = 60 * 60;

    private Map<String, DepartmentDTO> departmentCache = new HashMap<>();;

	RestTemplateUtil restUtil = null;

	private long ifModifiedSince = 0L;

	private Instant lastCheckedTime = Instant.MIN;

	@Autowired
	public DepartmentGateway(RestTemplate restTemplate, CacheManager cacheManager) {
		super();
		this.restUtil = new RestTemplateUtil(restTemplate, commonUriPrefix);
	}

	@PostConstruct
	public void initCache() {
		loadAllDepartments();
	}
	
	public void loadAllDepartments() {
		if (!needToUpdate()) {
			return;
		}
		synchronized (lastCheckedTime) {
			if (!needToUpdate()) {
				return;
			}
			DepartmentDTO[] departments = getAllDepartmentsIfChanged();
			if (departments != null) {
				for (DepartmentDTO d : departments) {
					getDepartmentCache().put(d.getId(), d);
				}
			}
			setLastCheckedTime(Instant.now());
		}
	}

	private DepartmentDTO[] getAllDepartmentsIfChanged() {
		HttpHeaders headers = new HttpHeaders();
		if (ifModifiedSince != 0l) {
			headers.setIfModifiedSince(ifModifiedSince);
		}

		ResponseEntity<DepartmentDTO[]> responseEntity = restUtil.exchangeGet5(
				getUri("/departments"), 
				new HttpEntity<>(headers), 
				DepartmentDTO[].class);
		if(responseEntity == null) {
			return new DepartmentDTO[0];
		}
		
		ifModifiedSince = responseEntity.getHeaders().getLastModified();

		HttpStatus statusCode = responseEntity.getStatusCode();
		if (statusCode.isError()) {
			log.error("FAILED TO GET DEPARTMENTS, status:{}", statusCode);
		} else if (statusCode.equals(HttpStatus.NOT_MODIFIED)) {
			log.debug("departments are not changed");
		}
		
		return responseEntity.getBody();
	}

	public Map<String, String> getDepartmentNames(Set<String> departmentIds) {
		Map<String, DepartmentDTO> departmentCacheRef = getDepartmentCache();
		Map<String, String> names = new HashMap<>();
		for(String key : departmentIds) {
			DepartmentDTO department = departmentCacheRef.get(key);
			if (department == null) {
				log.debug("couldn't find department for {}", key);
				continue;
			}
			names.put(department.getId(), department.getName());
		}
		return names;
	}

	private Map<String, DepartmentDTO> getDepartmentCache() {
		return departmentCache;
	}
	
	private String getUri(String format, Object... args) {
		return String.format(commonUriPrefix + format, args);
	}

	private boolean needToUpdate() {
		Instant lastCheckedTime = getLastCheckedTime();
		if (lastCheckedTime == null) {
			return true;
		}
		return lastCheckedTime.plusSeconds(getUpdateDuration()).isBefore(Instant.now());
	}

	public long getIfModifiedSince() {
		return ifModifiedSince;
	}

	public void setIfModifiedSince(long ifModifiedSince) {
		this.ifModifiedSince = ifModifiedSince;
	}

	public Instant getLastCheckedTime() {
		return lastCheckedTime;
	}

	public void setLastCheckedTime(Instant lastCheckedTime) {
		this.lastCheckedTime = lastCheckedTime;
	}

	public static long getUpdateDuration() {
		return updateDuration;
	}

	public static void setUpdateDuration(long updateDuration) {
		DepartmentGateway.updateDuration = updateDuration;
	}
	
}
