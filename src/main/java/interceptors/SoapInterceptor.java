package interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.context.MessageContext;
import services.RequestMockService;
import utils.RequestMockProperties;
import org.springframework.ws.server.EndpointInterceptor;

@Component
public class SoapInterceptor implements EndpointInterceptor {

    private final RequestMockProperties requestMockProperties;

    private final RequestMockService requestMockService;

    @Autowired
    public SoapInterceptor(RequestMockProperties requestMockProperties, RequestMockService requestMockService) {
        this.requestMockProperties = requestMockProperties;
        this.requestMockService = requestMockService;
    }


    @Override
    public boolean handleRequest(MessageContext messageContext, Object o) throws Exception {
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext, Object o) throws Exception {

        if (Boolean.getBoolean(requestMockProperties.getProperty("is.mocking.enables"))) {
            requestMockService.executeSoapMockIfExists(messageContext);
        }

        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext, Object o) throws Exception {
        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Object o, Exception e) throws Exception {
    }
}
