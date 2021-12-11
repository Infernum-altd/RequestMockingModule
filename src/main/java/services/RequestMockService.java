package services;

import models.RequestMock;
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

@Service
public class RequestMockService {

    private final RequestMockProperties requestMockProperties;

    private final MockServiceContainer mockServiceContainer;

    private final SaajSoapMessageFactory saajMessageFactory;

    @Autowired
    public RequestMockService(RequestMockProperties requestMockProperties,
                              MockServiceContainer mockServiceContainer,
                              SaajSoapMessageFactory saajMessageFactory) {
        this.requestMockProperties = requestMockProperties;
        this.mockServiceContainer = mockServiceContainer;
        this.saajMessageFactory = saajMessageFactory;
    }

    public ClientHttpResponse executeRestMockIfExists(HttpRequest httpRequest, byte[] defaultResponse,
                                                      URI uri, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        RequestMock mock = mockServiceContainer.getRestMock(
                uri.getHost() + uri.getPath() +
                        mockServiceContainer.sortRequestParameters(uri.getQuery()));
        return processRestMock(httpRequest, defaultResponse, clientHttpRequestExecution, mock);
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

        RequestMock mock = mockServiceContainer.getSoapMock(mockServiceContainer.removeExcludedParametersSOAP(soapRequest));
        if (Objects.nonNull(mock)) {
            InputStream is = new ByteArrayInputStream(mock.getResponse().getBytes());
            SaajSoapMessage message = saajMessageFactory.createWebServiceMessage();
            message.setSaajMessage(MessageFactory.newInstance().createMessage(null, is));
            messageContext.setResponse(message);
        }
    }
}

