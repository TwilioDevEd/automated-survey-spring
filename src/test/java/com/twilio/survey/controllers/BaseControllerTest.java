package com.twilio.survey.controllers;

import com.mashape.unirest.http.Unirest;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

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

    protected Document getDocument(String content) throws JDOMException, IOException {
        return new SAXBuilder().build(new StringReader(content));
    }

    protected Element getElement(Document document, String path) {
        String[] pathComponents = path.split("/");

        switch (pathComponents.length) {
            case 1:
                return document.getRootElement().getChild(pathComponents[0]);
            case 2:
                return document.getRootElement()
                        .getChild(pathComponents[0])
                        .getChild(pathComponents[1]);
            case 3:
                return document.getRootElement()
                        .getChild(pathComponents[0])
                        .getChild(pathComponents[1])
                        .getChild(pathComponents[2]);
            default:
                return null;
        }
    }

    protected String getAttributeValue(Document document, String path, String attrName) {
        Element element = getElement(document, path);
        return element.getAttributeValue(attrName);
    }
}
