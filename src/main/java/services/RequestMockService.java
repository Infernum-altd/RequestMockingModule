package services;

import models.RequestMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.ws.context.MessageContext;
import utils.RequestMockProperties;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RequestMockService {

    private final RequestMockProperties requestMockProperties;

    private final MockServiceContainer mockServiceContainer;

    /*Boolean.getBoolean(requestMockProperties.getProperty("request.mocking.library.is.db.mode")*/
    @Autowired
    public RequestMockService(RequestMockProperties requestMockProperties, MockServiceContainer mockServiceContainer) {
        this.requestMockProperties = requestMockProperties;
        this.mockServiceContainer = mockServiceContainer;
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

        }
        return executeDefaultBehaviourOfRest(httpRequest, defaultResponse, clientHttpRequestExecution);
    }

    private ClientHttpResponse executeDefaultBehaviourOfRest(HttpRequest httpRequest, byte[] defaultResponse, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        return clientHttpRequestExecution.execute(httpRequest, defaultResponse);
    }

    public void executeSoapMockIfExists(MessageContext messageContext) throws IOException {
        OutputStream outputStream = new ByteArrayOutputStream();
        messageContext.getRequest().writeTo(outputStream);
        String soapRequest = outputStream.toString(); //TODO change responce if mock exists for request
    }
}

   /* public static void main(String[] args){ //TODO EXCLUDE SOAP NODE, SORT THEM ?
        String XmlContent="<Address> <Location>Beach</Location><Dangerous>
                <Flag>N</Flag> </Dangerous> </Address>";

        String tagToReplace="Address";
        String newValue="";

        Document doc = Jsoup.parse(XmlContent);
        ArrayList<Element> els =doc.getElementsByTag(tagToReplace);
        for(int i=0;i<els.size();i++){
            Element el = els.get(i);
            el.remove();
        }
        XmlContent=doc.body().children().toString();
    }*/
