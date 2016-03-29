package com.twilio.survey.controllers;

import com.mashape.unirest.http.Unirest;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic Unirest and general test helpers
 */
public class BaseControllerTest {
    @Value("${local.server.port}")
    int port;

    @Before
    public void baseBefore() {
        //Clean up cookies and use a cookie manager
        BasicCookieStore cookieStore = new BasicCookieStore();
        Unirest.setHttpClient(HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build());
    }

    protected String getAsSMS(String url) throws Exception {
        return getWithParameters(url, "MessageSid", "SMS225345234234");
    }

    protected String getAsCall(String url) throws Exception {
        return getWithParameters(url, "CallSid", "Call25345234234");
    }

    protected String get(String url) throws Exception {
        return getWithParameters(url, null, null);
    }

    protected String getWithParameters(String url, String key, String value) throws Exception {
        Map<String, Object> params = new HashMap<>();
        if (key != null && value != null) {
            params.put(key, value);
        }
        return getWithParameters(url, params);
    }

    protected String getWithParameters(String url, Map<String, Object> params) throws Exception {
        url = "http://localhost:" + port + url;
        return Unirest.get(url).queryString(params).asString().getBody();
    }

    protected String postCallWithParameters(String url, Map<String, Object> params) throws Exception {
        params.put("CallSid", "Call25345234234");
        url = "http://localhost:" + port + url;
        return Unirest.post(url).queryString(params).asString().getBody();
    }

    protected String postSMSWithParameters(String url, Map<String, Object> params) throws Exception {
        params.put("MessageSid", "SMS225345234234");
        url = "http://localhost:" + port + url;
        return Unirest.post(url).queryString(params).asString().getBody();
    }
}
