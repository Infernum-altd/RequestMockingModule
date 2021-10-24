package services;

import models.RequestMock;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import utils.RequestMockProperties;

import org.springframework.ws.soap.saaj.SaajSoapMessage;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RequestMockService {

    private final RequestMockProperties requestMockProperties;

    private final MockServiceContainer mockServiceContainer;

    private final SaajSoapMessageFactory saajMessageFactory;

    @Autowired
    public RequestMockService(RequestMockProperties requestMockProperties, MockServiceContainer mockServiceContainer, SaajSoapMessageFactory saajMessageFactory) {
        this.requestMockProperties = requestMockProperties;
        this.mockServiceContainer = mockServiceContainer;
        this.saajMessageFactory = saajMessageFactory;
    }

    public ClientHttpResponse executeRestMockIfExists(HttpRequest httpRequest, byte[] defaultResponse, URI uri, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        RequestMock mock = mockServiceContainer.getRestMock(uri.getHost() + uri.getPath() + sortRequestParameters(uri.getQuery()));
        return processRestMocksInMemoryMode(httpRequest, defaultResponse, clientHttpRequestExecution, mock);
    }

    private String sortRequestParameters(String parameters) {
        return Arrays.stream(parameters.split("&"))
                .filter(this::removeExcludedParameters)
                .sorted()
                .collect(Collectors.joining("&", "", ""));
    }

    private boolean removeExcludedParameters(String parameterValue) {
        return !mockServiceContainer.getRestExcludeParameters().contains(parameterValue.split("=")[0]);
    }

    private ClientHttpResponse processRestMocksInMemoryMode(HttpRequest httpRequest, byte[] defaultResponse, ClientHttpRequestExecution clientHttpRequestExecution, RequestMock requestMock) throws IOException {
        return processRestMock(httpRequest, defaultResponse, clientHttpRequestExecution, requestMock);
    }

    private ClientHttpResponse processRestMock(HttpRequest httpRequest, byte[] defaultResponse, ClientHttpRequestExecution clientHttpRequestExecution, RequestMock requestMock) throws IOException {
        if (Objects.nonNull(requestMock)) {
            clientHttpRequestExecution.execute(httpRequest, requestMock.getResponse().getBytes(StandardCharsets.UTF_8));
        }
        return executeDefaultBehaviourOfRest(httpRequest, defaultResponse, clientHttpRequestExecution);
    }

    private ClientHttpResponse executeDefaultBehaviourOfRest(HttpRequest httpRequest, byte[] defaultResponse, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        return clientHttpRequestExecution.execute(httpRequest, defaultResponse);
    }

    public void executeSoapMockIfExists(MessageContext messageContext) throws IOException, SOAPException {
        OutputStream outputStream = new ByteArrayOutputStream();
        messageContext.getRequest().writeTo(outputStream);
        String soapRequest = outputStream.toString();

        RequestMock mock = mockServiceContainer.getSoapMock(removeExcludedParametersSOAP(soapRequest));
        if (Objects.nonNull(mock)) {
            InputStream is = new ByteArrayInputStream(mock.getResponse().getBytes());
            SaajSoapMessage message = saajMessageFactory.createWebServiceMessage();
            message.setSaajMessage(MessageFactory.newInstance().createMessage(null, is));
            messageContext.setResponse(message);
        }
    }

    private String removeExcludedParametersSOAP(String soapEnvelope) {
        Document doc = Jsoup.parse(soapEnvelope);
        for (String excludeNode : mockServiceContainer.getSoapExcludeParameters()) {
            ArrayList<Element> els = doc.getElementsByTag(excludeNode);
            for (Element el : els) {
                el.remove();
            }
            doc = Jsoup.parse(doc.body().children().toString());
        }

        return doc.toString();
    }
}

