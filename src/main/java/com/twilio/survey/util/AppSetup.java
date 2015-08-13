package com.twilio.survey.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class AppSetup {
  private Map<String, String> env;

  public AppSetup() {
    this.env = System.getenv();
  }

  public Map<String, String> getParamsFromDBURL(String url) {
    Map<String, String> params = new HashMap<>();
    URI dbUri = null;

    try {
      dbUri = new URI(url);
    } catch (URISyntaxException e) {
      System.out.println("Unable to parse DB URL");
    }
    String[] userInfo = dbUri.getUserInfo().split(":");
    String username = dbUri.getUserInfo().split(":")[0];
    String password = null;
    if (userInfo.length > 1) {
      password = dbUri.getUserInfo().split(":")[1];
    } else {
      password = "";
    }

    String dBUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath() +
        "?user=" + username + "&password=" + password;
    if (getSslEnabledDb()) {
      dBUrl += "&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

    }
    params.put("url", dBUrl);

    return params;
  }

  public int getPortNumber() {
    String port = env.get("PORT");

    if (port != null) {
      return Integer.parseInt(port);
    } else {
      return 8080;
    }
  }

  public String getDatabaseURL() {
    return env.get("DATABASE_URL");
  }

  public boolean getSslEnabledDb() {
    String ssl = env.get("TWILIO_DISABLE_DB_SSL");
    if (ssl != null && ssl.compareToIgnoreCase("true") == 0) {
      return false;
    }
    else {
      return true;
    }
  }
}
