package interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import services.RequestMockService;
import utils.RequestMockProperties;

import java.io.IOException;

@Component
public class RestInterceptor implements ClientHttpRequestInterceptor {

    private final RequestMockProperties requestMockProperties;

    private final RequestMockService requestMockService;

    @Autowired
    public RestInterceptor(RequestMockProperties requestMockProperties, RequestMockService requestMockService) {
        this.requestMockProperties = requestMockProperties;
        this.requestMockService = requestMockService;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {

        if (Boolean.getBoolean(requestMockProperties.getProperty("is.mocking.enables"))) {
            return requestMockService.executeRestMockIfExists(httpRequest, bytes, httpRequest.getURI(), clientHttpRequestExecution);
        }
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}
