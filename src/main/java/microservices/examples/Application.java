package microservices.examples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@EnableSpringDataWebSupport
public class Application {

	private static ApplicationContext applicationContext;

	@Autowired
	ObjectMapper objectMapper;
	
	public static void main(String[] args) {
		applicationContext = SpringApplication.run(Application.class, args);
		//displayAllBeans();
    }
    
    public static void displayAllBeans() {
        String[] allBeanNames = applicationContext.getBeanDefinitionNames();
        for(String beanName : allBeanNames) {
            System.out.println(beanName);
        }
    }
}
