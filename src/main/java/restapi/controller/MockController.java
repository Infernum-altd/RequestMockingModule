package restapi.controller;

import exceptions.UnknownMockTypeException;
import models.RequestMock;
import models.RequestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import services.MockServiceContainer;
import utils.RequestMockProperties;

@RestController("/mock")
@CrossOrigin
public class MockController {

    private final RequestMockProperties requestMockProperties;

    private final MockServiceContainer mockServiceContainer;

    private final static String IS_MOCKING_ENABLES = "is.mocking.enables";

    @Autowired
    public MockController(RequestMockProperties requestMockProperties, MockServiceContainer mockServiceContainer) {
        this.requestMockProperties = requestMockProperties;
        this.mockServiceContainer = mockServiceContainer;
    }

    @GetMapping("/switch/{switchValue}")
    public boolean mockSwitch(@PathVariable String switchValue) {
        if (switchValue.equalsIgnoreCase("true") || switchValue.equalsIgnoreCase("false")) {
            requestMockProperties.setProperty(IS_MOCKING_ENABLES, switchValue);
            return Boolean.parseBoolean(requestMockProperties.getProperty(IS_MOCKING_ENABLES));
        }
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Received not boolean path variable");
    }

    @PostMapping("/add")
    public HttpStatus addMock(@RequestBody RequestMock requestMock) {
        if (RequestType.REST.equals(requestMock.getRequestType())) {
            mockServiceContainer.addRestMock(requestMock);
            return HttpStatus.OK;
        } else if (RequestType.SOAP.equals(requestMock.getRequestType())) {
            mockServiceContainer.addSoapMock(requestMock);
            return HttpStatus.OK;
        }

        throw new UnknownMockTypeException("Unknown type of mock : " + requestMock.getRequestType() + " Mock can have type REST or SOAP");
    }

    @PostMapping("/remove")
    public HttpStatus removeMock(@RequestBody RequestMock requestMock) {
        if (RequestType.REST.equals(requestMock.getRequestType())) {
            mockServiceContainer.removeRestMock(requestMock);
            return HttpStatus.OK;
        } else if (RequestType.SOAP.equals(requestMock.getRequestType())) {
            mockServiceContainer.removeSoapMock(requestMock);
            return HttpStatus.OK;
        }

        throw new UnknownMockTypeException("Unknown type of mock : " + requestMock.getRequestType() + " Mock can have type REST or SOAP");
    }
}
