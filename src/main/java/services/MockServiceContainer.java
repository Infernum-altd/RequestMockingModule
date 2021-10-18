package services;

import models.RequestMock;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class MockServiceContainer {
    private final Map<String, RequestMock> restMocks = new ConcurrentHashMap<>();

    private final List<String> restExcludeParameters = new ArrayList<>();

    public RequestMock getRestMock(String mockIdentifier) {
        return restMocks.getOrDefault(mockIdentifier, null);
    }

    public void addRestMock(RequestMock requestMock) { //TODO REST SOAP
        URI request = URI.create(requestMock.getIdentifierOfRequest());
        String sortedUrl = request.getHost() + request.getPath() + sortRequestParameters(request.getQuery());
        restMocks.put(sortedUrl, requestMock);
    }

    public void removeRestMock(RequestMock requestMock) {
        restMocks.remove(requestMock.getIdentifierOfRequest());
    }

    public List<String> getRestExcludeParameters() {
        return restExcludeParameters;
    }

    private String sortRequestParameters(String parameters) {
        return Arrays.stream(parameters.split("&"))
                .filter(this::removeExcludedParameters)
                .sorted()
                .collect(Collectors.joining("&", "", ""));
    }

    private boolean removeExcludedParameters(String parameterValue) {
        return !restExcludeParameters.contains(parameterValue.split("=")[0]);
    }
}
