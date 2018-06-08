package com.dipp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessToken {

    @JsonProperty(value = "access_token")
    private String accessToken;

    @JsonProperty(value = "token_type")
    private String tokenType;

    @JsonProperty(value = "refresh_token")
    private String refreshToken;

    @JsonProperty(value = "scope")
    private String scope;

    @JsonProperty(value = "id_token")
    private String idToken;

    public AccessToken() {
    }

    public AccessToken(String accessToken, String tokenType, String refreshToken, String scope, String idToken) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.refreshToken = refreshToken;
        this.scope = scope;
        this.idToken = idToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    @Override
    public String toString() {
        return "{" +
                "\n\t\"access_token\":\"" + accessToken + "\"" +
                ",\n\t\"token_type\":\"" + tokenType + "\"" +
                ",\n\t\"refresh_token\":\"" + refreshToken + "\"" +
                ",\n\t\"scope\":\"" + scope + "\"" +
                ",\n\t\"id_token\":\"" + idToken + "\"\n" +
                '}';
    }
}
