package services;

import models.RequestMock;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class MockServiceContainer {
    private final Map<String, RequestMock> restMocks = new ConcurrentHashMap<>();

    private final Map<String, RequestMock> soapMocks = new ConcurrentHashMap<>();

    private final List<String> restExcludeParameters = Collections.synchronizedList(new ArrayList<>());

    private final List<String> soapExcludeParameters = Collections.synchronizedList(new ArrayList<>());

    public RequestMock getRestMock(String mockIdentifier) {
        return restMocks.getOrDefault(mockIdentifier, null);
    }

    public void addRestMock(RequestMock requestMock) {
        URI request = URI.create(requestMock.getIdentifierOfRequest());
        String sortedUrl = request.getHost() + request.getPath() + sortRequestParameters(request.getQuery());
        restMocks.put(sortedUrl, requestMock);
    }

    public void removeRestMock(RequestMock requestMock) {
        URI request = URI.create(requestMock.getIdentifierOfRequest());
        String sortedUrl = request.getHost() + request.getPath() + sortRequestParameters(request.getQuery());
        restMocks.remove(sortedUrl);
    }

    public void addSoapMock(RequestMock requestMock) {
        restMocks.put(removeExcludedParametersSOAP(requestMock.getIdentifierOfRequest()), requestMock);
    }

    public void removeSoapMock(RequestMock requestMock) {
        restMocks.remove(removeExcludedParametersSOAP(requestMock.getIdentifierOfRequest()));
    }

    public List<String> getRestExcludeParameters() {
        return restExcludeParameters;
    }

    public List<String> getSoapExcludeParameters() {
        return soapExcludeParameters;
    }

    public String sortRequestParameters(String parameters) {
        return Arrays.stream(parameters.split("&"))
                .filter(this::removeExcludedParameters)
                .sorted()
                .collect(Collectors.joining("&", "", ""));
    }

    private boolean removeExcludedParameters(String parameterValue) {
        return !restExcludeParameters.contains(parameterValue.split("=")[0]);
    }

    public RequestMock getSoapMock(String removeExcludedParametersSOAP) {
        return soapMocks.getOrDefault(removeExcludedParametersSOAP, null);
    }

    public String removeExcludedParametersSOAP(String soapEnvelope) {
        Document doc = Jsoup.parse(soapEnvelope);
        for (String excludeNode : soapExcludeParameters) {
            ArrayList<Element> els = doc.getElementsByTag(excludeNode);
            for (Element el : els) {
                el.remove();
            }
            doc = Jsoup.parse(doc.body().children().toString());
        }

        return doc.toString();
    }
}

