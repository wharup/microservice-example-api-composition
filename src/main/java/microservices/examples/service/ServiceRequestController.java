package microservices.examples.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/services")
@Slf4j
public class ServiceRequestController {
	
	@Autowired
	ServiceRequestService serviceRequestService;
	
	@GetMapping
	public Page<ServiceRequest> search(Pageable pageable){
		String msg = String.format("Service search : page=%d, size=%d", 
									pageable.getPageNumber(), 
									pageable.getPageSize());
		List<ServiceRequest> result = serviceRequestService.findAllWithBatchRESTApi(pageable);
		long totalCount = serviceRequestService.countAll();
		return new PageImpl<>(result, pageable, totalCount);
		
	}
}
