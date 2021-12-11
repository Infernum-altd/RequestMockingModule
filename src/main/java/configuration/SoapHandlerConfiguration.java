package configuration;

import interceptors.SoapInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;

import java.util.List;

@Configuration
@EnableWs
public class SoapHandlerConfiguration extends WsConfigurerAdapter {

    private final ApplicationContext context;

    @Autowired
    public SoapHandlerConfiguration(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors)
    {
        interceptors.add(context.getBean(SoapInterceptor.class));
    }
}
