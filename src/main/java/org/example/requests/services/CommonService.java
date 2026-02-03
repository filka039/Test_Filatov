package org.example.requests.services;

import io.restassured.response.Response;
import lombok.Getter;
import org.example.constants.Endpoints;
import org.example.requests.httpCore.HttpCore;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CommonService extends HttpCore {
    private String lastRequestUrl;
    private Map<String, String> lastRequestHeaders;
    private String lastRequestBody;


    public Response postRequest(Map<String, String> headers, String body) {
        this.lastRequestUrl = Endpoints.BASE_URL + Endpoints.COMMON_ENDPOINT;
        this.lastRequestHeaders = new HashMap<>(headers);
        this.lastRequestBody = body;
        return post(Endpoints.COMMON_ENDPOINT, headers, body);
    }

    public Response postRequest(String body) {
        return post(Endpoints.COMMON_ENDPOINT, body);
    }

    public Response postRequest(Map<String, String> headers) {
        return post(Endpoints.COMMON_ENDPOINT, headers);
    }


}
