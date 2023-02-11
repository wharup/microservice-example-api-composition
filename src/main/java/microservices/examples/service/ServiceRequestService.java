package microservices.examples.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;
import microservices.examples.gateway.CodeGateway;
import microservices.examples.gateway.CustomerDTO;
import microservices.examples.gateway.CustomerGateway;
import microservices.examples.gateway.DepartmentGateway;
import microservices.examples.gateway.UserGateway;
import microservices.examples.system.StopWatchUtil;

@Transactional
@Service
@Slf4j
public class ServiceRequestService {

	protected ServiceRequestDAO serviceRequestDAO;

	protected CustomerGateway customerGateway;
	protected UserGateway userGateway;
	protected DepartmentGateway departmentGateway;
	protected CodeGateway codeGateway;
	
	public ServiceRequestService() {
		super();
	}
	
	@Autowired
	public ServiceRequestService(ServiceRequestDAO serviceRequestDAO,
								 CustomerGateway customerGateway, 
								 UserGateway userGateway,
								 DepartmentGateway departmentGateway, 
								 CodeGateway codeGateway) {
		super();
		this.serviceRequestDAO = serviceRequestDAO;
		
		this.customerGateway = customerGateway;
		this.userGateway = userGateway;
		this.departmentGateway = departmentGateway;
		this.codeGateway = codeGateway;
	}

    @Autowired
    PlatformTransactionManager transactionManager;
    
	public List<ServiceRequest> findAllWithBatchRESTApi(Pageable pageable) {
	    StopWatch sw = new StopWatch("findAllBySQl");
		
		//1. 사용자 정보로 접근 제어 수행
		UserDetails user = ExampleSecurityContext.getCurrentLoginUser();
		ensureUserCanReadAllServiceRequests(user);
		
		//2. 전체 상담이력 조회
		sw.start("1. 상담이력 쿼리");
		List<ServiceRequest> result = serviceRequestDAO.selectAll(pageable);
		sw.stop();
		
		sw.start("2. 부가정보 조회&조합 - code_type");
		//3. 고객 ID, 사용자 ID, 부서 ID 수집
		Set<String> customerIds = new HashSet<>();
		Set<String> userIds = new HashSet<>();
		Set<String> departmentIds = new HashSet<>();
		for (ServiceRequest sr : result) {
			customerIds.add(sr.getCustomerId());
			userIds.add(sr.getCallAgentId());
			userIds.add(sr.getVocAssgneeId());
			departmentIds.add(sr.getVocAssgneeDeptId());
		}
		
		//4. 코드, 고객, 사용자, 부서 정보 조회
		Map<String, String> srTypeCodes = codeGateway.getCodeNamesByType("SR_TYPE");
		Map<String, String> srStatusCodes = codeGateway.getCodeNamesByType("SR_STATUS");
		Map<String, String> customerNames = customerGateway.getCustomerNames(customerIds);
		Map<String, String> userNames = userGateway.getUserNames(userIds);
		Map<String, String> departmentNames = departmentGateway.getDepartmentNames(departmentIds);
		
		//5. 상담이력과 부가정보 조회
		for (ServiceRequest sr : result) {
			sr.setStatusName(get(srStatusCodes, sr.getStatus()));
			sr.setTypeName(get(srTypeCodes, sr.getType()));
			sr.setCustomerName(get(customerNames, sr.getCustomerId()));
			sr.setCallAgentName(get(userNames, sr.getCallAgentId()));
			sr.setVocAssgneeName(get(userNames, sr.getVocAssgneeId()));
			sr.setVocAssgneeDeptName(get(departmentNames, sr.getVocAssgneeDeptId()));
		}
		sw.stop();
		StopWatchUtil.logGroupByTaskName(sw);
		return result;
	}
	
	private void ensureUserCanReadAllServiceRequests(UserDetails user) {
		if (checkIfCanReadAllServiceRequetsByDeptId(user.getDepartmentId())) {
			return;
		}
		if (checkIfCanReadAllServiceRequestsByRoles(user.getRoles())) {
			return;
		}
		throw new ExampleAccessDeniedException();
	}

	private boolean checkIfCanReadAllServiceRequestsByRoles(String[] roles) {
		List<String> supervisorRoles = getSupervisorRoles();
		for (String role : roles) {
			if (supervisorRoles.contains(role)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkIfCanReadAllServiceRequetsByDeptId(String departmentId) {
		List<String> supervisingDepartments = getSupervisorDepartmentIds();
		if (supervisingDepartments.contains(departmentId)) {
			return true;
		}
		return false;
	}

	private List<String> getSupervisorDepartmentIds() {
		return new ArrayList<>();
	}

	private List<String> getSupervisorRoles() {
		ArrayList<String> ids = new ArrayList<>();
		ids.add("CALL_MANAGER");
		return ids;
	}


	public List<ServiceRequest> findAllWithBatchRESTApi(String type, Pageable pageable) {

		
		List<ServiceRequest> result = findServiceRequestSortedByCustomerName(type, pageable);
		
		Set<String> userIds = new HashSet<>();
		Set<String> departmentIds = new HashSet<>();
		for(ServiceRequest sr : result) {
			userIds.add(sr.getCallAgentId());
			userIds.add(sr.getVocAssgneeId());
			departmentIds.add(sr.getVocAssgneeDeptId());
		}
		
		StopWatchUtil.stop();
		StopWatchUtil.start("5");
		Map<String, String> srTypeCodes = codeGateway.getCodeNamesByType("SR_TYPE");
		Map<String, String> srStatusCodes = codeGateway.getCodeNamesByType("SR_STATUS");
		//Map<String, String> customerNames = customerGateway.getCustomerNames(customerIds);
		Map<String, String> userNames = userGateway.getUserNames(userIds);
		Map<String, String> departmentNames = departmentGateway.getDepartmentNames(departmentIds);
		
		StopWatchUtil.stop();
		StopWatchUtil.start("6");
		for (ServiceRequest sr : result) {
			sr.setStatusName(get(srStatusCodes, sr.getStatus()));
			sr.setTypeName(get(srTypeCodes, sr.getType()));
			//sr.setCustomerName(get(customerNames, sr.getCustomerId()));
			sr.setCallAgentName(get(userNames, sr.getCallAgentId()));
			sr.setVocAssgneeName(get(userNames, sr.getVocAssgneeId()));
			sr.setVocAssgneeDeptName(get(departmentNames, sr.getVocAssgneeDeptId()));
		}
		StopWatchUtil.stop();
		StopWatchUtil.log();
		
		return result;
	}

	public List<ServiceRequest> findServiceRequestSortedByCustomerName(String type, Pageable pageable) {
		StopWatchUtil.start("1");
		List<ServiceRequest> serviceRequests = serviceRequestDAO.selectAll(type, PageRequest.of(0, Integer.MAX_VALUE));
		if (serviceRequests.isEmpty()) {
			return serviceRequests;
		}
		StopWatchUtil.stop();
		StopWatchUtil.start("2");
		Map<String, List<ServiceRequest>> srByCustomerIds = new HashMap<>(serviceRequests.size());
		for (ServiceRequest sr : serviceRequests) {
			List<ServiceRequest> srs = srByCustomerIds.get(sr.getCustomerId());
			if (srs == null) {
				srs = new ArrayList<>();
				srByCustomerIds.put(sr.getCustomerId(),srs);
			}
			srs.add(sr);
		}
		StopWatchUtil.stop();
		StopWatchUtil.start("3");
		Map<String, String> customerNames1 = customerGateway.getCustomerNames(srByCustomerIds.keySet());
		List<Map.Entry<String, String>> sortedCustomerNames = new ArrayList<>(customerNames1.entrySet());
		Collections.sort(sortedCustomerNames, new Comparator<Map.Entry<String, String>>(){
			public int compare(Entry<String, String> o1, Entry<String, String> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		StopWatchUtil.stop();
		StopWatchUtil.start("4");
		
		List<ServiceRequest> result = new ArrayList<>(serviceRequests.size());
		int startIndex = pageable.getPageNumber() * pageable.getPageSize();
		int endIndex = startIndex + pageable.getPageSize();
		int i = 0;
		for (Map.Entry<String, String> e : sortedCustomerNames) {
			String key = e.getKey();
			List<ServiceRequest> srs = srByCustomerIds.get(key);
			if (srs == null || srs.isEmpty()) {
				log.error("{}", key);
			}
			for (ServiceRequest sr : srs) {
				if (i < startIndex) {
					i++;
					continue;
				}
				if (i >= endIndex) {
					break;
				}
				sr.setCustomerName(e.getValue());
				result.add(sr);
				i++;
			}
			if (i >= endIndex) {
				break;
			}
		}
		return result;
	}
	public List<ServiceRequest> findServiceRequestSortedByCustomerName2(String type, Pageable pageable) {
		Page<CustomerDTO> customers = customerGateway.getAllCustomers();
		if (customers == null) {
			return new ArrayList<>();
		}
		int startIndex = pageable.getPageNumber() * pageable.getPageSize();
		int endIndex = startIndex + pageable.getPageSize();
		int i = 0;
		List<ServiceRequest> result = new ArrayList<>();
		for (CustomerDTO c : customers) {
			List<ServiceRequest> selected = serviceRequestDAO.selectAll(type, c.getId(), PageRequest.of(0, Integer.MAX_VALUE));
			for (ServiceRequest s : selected) {
				if (i < startIndex) {
					i++;
					continue;
				}
				if (i >= endIndex) {
					break;
				}
				s.setCustomerName(c.getName());
				result.add(s);
				i++;
			}
			if (i >= endIndex) {
				break;
			}
		}
		
		return result;
	}

	public List<ServiceRequest> findAllWithBatchRESTApi2(String type, Pageable pageable) {
		/**
		 * 1. COMPLAIN유형의 SR을 읽어 고객ID 기준으로 소팅 -> 고객ID를 뽑아서 조회하고 -> 고객ID를 고객이름으로 정렬한 후에 
		 */
		StopWatchUtil.start("1");
		List<ServiceRequest> serviceRequests = serviceRequestDAO.selectAll(type, PageRequest.of(0, Integer.MAX_VALUE));
		if (serviceRequests.isEmpty()) {
			return serviceRequests;
		}
		StopWatchUtil.stop();
		StopWatchUtil.start("2");
		Map<String, List<ServiceRequest>> srByCustomerIds = new HashMap<>(serviceRequests.size());
		for (ServiceRequest sr : serviceRequests) {
			List<ServiceRequest> srs = srByCustomerIds.get(sr.getCustomerId());
			if (srs == null) {
				srs = new ArrayList<>();
				srByCustomerIds.put(sr.getCustomerId(),srs);
			}
			srs.add(sr);
		}
		StopWatchUtil.stop();
		StopWatchUtil.start("3");
		Map<String, String> customerNames = customerGateway.getCustomerNames(srByCustomerIds.keySet());
		List<Map.Entry<String, String>> entries = new ArrayList<>(customerNames.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<String, String>>(){
			public int compare(Entry<String, String> o1, Entry<String, String> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		StopWatchUtil.stop();
		StopWatchUtil.start("4");
		Set<String> userIds = new HashSet<>();
		Set<String> departmentIds = new HashSet<>();
		
		List<ServiceRequest> result = new ArrayList<>(serviceRequests.size());
		int startIndex = pageable.getPageNumber() * pageable.getPageSize();
		int endIndex = startIndex + pageable.getPageSize();
		int i = 0;
		for (Map.Entry<String, String> e : entries) {
			String key = e.getKey();
			List<ServiceRequest> srs = srByCustomerIds.get(key);
			if (srs == null || srs.isEmpty()) {
				log.error("{}", key);
			}
			for (ServiceRequest sr : srs) {
				if (i < startIndex) {
					i++;
					continue;
				}
				if (i >= endIndex) {
					break;
				}
				result.add(sr);
				userIds.add(sr.getCallAgentId());
				userIds.add(sr.getVocAssgneeId());
				departmentIds.add(sr.getVocAssgneeDeptId());
				i++;
			}
			if (i >= endIndex) {
				break;
			}
		}
		StopWatchUtil.stop();
		StopWatchUtil.start("5");
		Map<String, String> srTypeCodes = codeGateway.getCodeNamesByType("SR_TYPE");
		Map<String, String> srStatusCodes = codeGateway.getCodeNamesByType("SR_STATUS");
		Map<String, String> userNames = userGateway.getUserNames(userIds);
		Map<String, String> departmentNames = departmentGateway.getDepartmentNames(departmentIds);
		
		StopWatchUtil.stop();
		StopWatchUtil.start("6");
		for (ServiceRequest sr : result) {
			sr.setStatusName(get(srStatusCodes, sr.getStatus()));
			sr.setTypeName(get(srTypeCodes, sr.getType()));
			sr.setCustomerName(get(customerNames, sr.getCustomerId()));
			sr.setCallAgentName(get(userNames, sr.getCallAgentId()));
			sr.setVocAssgneeName(get(userNames, sr.getVocAssgneeId()));
			sr.setVocAssgneeDeptName(get(departmentNames, sr.getVocAssgneeDeptId()));
		}
		StopWatchUtil.stop();
		StopWatchUtil.log();
		return result;
	}
	private String get(Map<String, String> map, String key) {
		String value = map.get(key);
		if (value == null) {
			log.debug("couldn't find value for {}", key);
			value = "";
		}
		return value;
	}

	public long countAll() {
		return serviceRequestDAO.countAll();
	}


}
