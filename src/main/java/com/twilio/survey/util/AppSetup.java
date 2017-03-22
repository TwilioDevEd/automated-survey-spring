package com.twilio.survey.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

/**
 * Class capable of retrieving environment variables necessary for the app to
 * run
 */
public class AppSetup {
  private Map<String, String> env;

  public AppSetup() {
    this.env = System.getenv();
  }

  /**
   * Formats the database url to match JDB format
   *
   * @param url
   *            receives the url string from the environment variable
   * @return Returns a map with the DATABASE_URL as a string
   * @throws URISyntaxException
   */
  public Map<String, String> getParamsFromDbUrl(String url) throws URISyntaxException {
    Map<String, String> params = new HashMap<>();
    URI dbUri = new URI(url);
    String userInfo = dbUri.getUserInfo();
    final List<BasicNameValuePair> queryStringParams = new ArrayList<>();
    if (userInfo.isEmpty()) {
      String[] credentials = userInfo.split(":");
      queryStringParams.add(new BasicNameValuePair("username", credentials[0]));

      String password = credentials[1];
      if (password.isEmpty()) {
        password = "";
      }

      queryStringParams.add(new BasicNameValuePair("password", password));
    }

    if (getSslEnabledDb()) {
      queryStringParams.add(new BasicNameValuePair("ssl", "true"));
      queryStringParams.add(new BasicNameValuePair("sslfactory", "org.postgresql.ssl.NonValidatingFactory"));
    }

    String queryString = URLEncodedUtils.format(queryStringParams, StandardCharsets.UTF_8);
    String dbUrl = String.format("jdbc:postgresql://%s:%s%s?%s", dbUri.getHost(), dbUri.getPort(), dbUri.getPath(),
        queryString);

    params.put("url", dbUrl);

    return params;
  }

  public String getDatabaseURL() {
    return env.get("DATABASE_URL");
  }

  /**
   * Fetches the environment variable TWILIO_DISABLE_DB_SSL. This needs to be
   * set to true if the database doesn't have SSL enabled
   */
  public boolean getSslEnabledDb() {
    return !Boolean.parseBoolean(env.get("TWILIO_DISABLE_DB_SSL"));
  }
}
