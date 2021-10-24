import interceptors.RestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class BeanConfiguration {

    @Autowired
    private ApplicationContext context;

    @Bean
    public RestTemplate restTemplate() { //TODO ADD SOAP INTERCEPTOR CONFIGURATION https://stackoverflow.com/questions/62573785/spring-ws-endpointinterceptor-not-invoked
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(context.getBean(RestInterceptor.class)));
        return restTemplate;
    }
}
