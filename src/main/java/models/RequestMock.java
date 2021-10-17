package models;

public class RequestMock {
    private String identifierOfRequest;
    private String response;
    private RequestType requestType;

    public RequestMock(String identifierOfRequest, String response, RequestType requestType) {
        this.identifierOfRequest = identifierOfRequest;
        this.response = response;
        this.requestType = requestType;
    }

    public String getIdentifierOfRequest() {
        return identifierOfRequest;
    }

    public void setIdentifierOfRequest(String identifierOfRequest) {
        this.identifierOfRequest = identifierOfRequest;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}
