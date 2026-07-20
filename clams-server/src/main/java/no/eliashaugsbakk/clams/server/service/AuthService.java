package no.eliashaugsbakk.clams.server.service;

import no.eliashaugsbakk.clams.server.config.AppConfig;

public class AuthService {
  private final String tokenHash;

  public AuthService(AppConfig appConfig) {
    this.tokenHash = appConfig.getAuthToken();
  }

  public boolean isValid(String incomingToken) {
    if (incomingToken == null || incomingToken.isBlank()) {
      return false;
    }

    return incomingToken.equals(tokenHash);
  }
}
