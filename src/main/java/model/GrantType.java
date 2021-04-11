package model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GrantType {
    CLIENT_CREDENTIALS("client_credentials"),
    PASSWORD("password");

    private String value;
}
