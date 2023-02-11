package microservices.examples.gateway;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CodeGateway {

	private static final String commonUriPrefix = "http://localhost:8090";
	
	private static long updateDuration = 6;//60 * 10;

	private Map<String, Map<String, String>> codeCache = new HashMap<>();

	RestTemplateUtil restUtil = null;

	private long ifModifiedSince = 0L;

	private Instant lastCheckedTime = Instant.MIN;

	@Autowired
	public CodeGateway(RestTemplate restTemplate) {
		super();
		this.restUtil = new RestTemplateUtil(restTemplate, commonUriPrefix);
	}

	@PostConstruct
	public void initCache() {
		loadCodeNamesIfUpdated();
	}

	private void loadCodeNamesIfUpdated() {
		loadCodeNamesIfUpdated("SR_STATUS", "SR_TYPE");
	}

	public void loadCodeNamesIfUpdated(String... codeTypes) {
		//1. 마지막 체크 후 일정 시간이 지났는지 확인
		if (!checkIfUpdateDurationPassed()) {
			return;
		}
		synchronized (codeCache) {
			//1.1 마지막 체크 시간 재확인
			if (!checkIfUpdateDurationPassed()) {
				return;
			}
			//1.2 마지막 체크 시간 업데이트
			setLastCheckedTime(Instant.now());

			//2 REST API로 코드 조회, 변경 시에만 조회됨
			CodeDTO[] codes = getCodesIfChanged(codeTypes);
			if (codes != null) {
				//2.1 변경된 코드로 신규 캐시 생성
				Map<String, Map<String, String>> codeMap = new HashMap<>();
				for (CodeDTO c : codes) {
					Map<String, String> map = createOrGet(codeMap, c.getCodeType());
					map.put(c.getCode(), c.getValue());
				}
				//2.2 기존 캐시와 교체
				codeCache = codeMap;
			}
		}
	}

	private boolean checkIfUpdateDurationPassed() {
		Instant lastCheckedTime = getLastCheckedTime();
		if (lastCheckedTime == null) {
			return true;
		}
		return lastCheckedTime.plusSeconds(getUpdateDuration()).isBefore(Instant.now());
	}

	private Map<String, String> createOrGet(Map<String, Map<String, String>> codeMap, String codeType) {
		Map<String, String> map = codeMap.get(codeType);
		if (map == null) {
			map = new HashMap<>();
			codeMap.put(codeType, map);
		}
		return map;
	}

	public CodeDTO[] getCodesIfChanged(String... codeTypes) {
		if (codeTypes == null || codeTypes.length == 0) {
			return null;
		}
		//1. 코드타입 목록을 스트링으로 변환 
		String typeString = getIdString(codeTypes);
	
		HttpHeaders headers = new HttpHeaders();
		//2. 'If-Modified-Since'헤더 세팅 
		headers.setIfModifiedSince(ifModifiedSince);
		//3. REST API 호출 
		ResponseEntity<CodeDTO[]> responseEntity = 
				restUtil.exchangeGet("/code-types/%s?batchapi", 
							typeString, 
							new HttpEntity<>(headers), 
							CodeDTO[].class);
	
		if(responseEntity == null) {
			return null;
		}
		
		//4. 'Last-Modified'헤더에서 변경시간 추출하여 저장 
		ifModifiedSince = responseEntity.getHeaders().getLastModified();
		HttpStatus statusCode = responseEntity.getStatusCode();
		//5. 상태 코드 확인 
		if (statusCode.equals(HttpStatus.NOT_MODIFIED)) {
			log.debug("codes are not changed {}", typeString);
		}
		return responseEntity.getBody();
	}

	private String getIdString(String... codeTypes) {
		String typeString = "";
		for (String codeType : codeTypes) {
			typeString += codeType + ",";
		}
		return typeString;
	}

	public Map<String, String> getCodeNamesByType(String codeType) {
		//1. 코드 변경시 재로
		loadCodeNamesIfUpdated();
		
		//2. 캐시에서 코드 타입 조회
		Map<String, String> codes = getCodeCache().get(codeType);
		if (codes == null) {
			return new HashMap<>();
		}
		return codes;
	}

	private Map<String, Map<String, String>> getCodeCache() {
		//캐시 교체 시 기존 Map에 대한 레퍼런스를 유지
		return codeCache;
	}

	public static long getUpdateDuration() {
		return updateDuration;
	}

	public static void setUpdateDuration(long updateDuration) {
		CodeGateway.updateDuration = updateDuration;
	}

	public Instant getLastCheckedTime() {
		return lastCheckedTime;
	}

	public void setLastCheckedTime(Instant lastCheckedTime) {
		this.lastCheckedTime = lastCheckedTime;
	}

	public long getIfModifiedSince() {
		return ifModifiedSince;
	}

	public void setIfModifiedSince(long ifModifiedSince) {
		this.ifModifiedSince = ifModifiedSince;
	}

}
