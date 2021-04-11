import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.GetAccessTokenRequest;
import requests.PlayerRegisterRequest;

import static model.GrantType.CLIENT_CREDENTIALS;
import static model.GrantType.PASSWORD;
import static org.hamcrest.Matchers.*;

public class GetTokenTests extends  PlayersBase{

    @Test
    @DisplayName("Client Credentials Grant")
    public void getTokenTest() {
        GetAccessTokenRequest request = GetAccessTokenRequest.builder()
                .grant_type(CLIENT_CREDENTIALS.getValue())
                .scope("guest:default")
                .build();

        client.getToken(request)
                .statusCode(200)
                .body("access_token", not(is(emptyOrNullString())))
                .body("token_type", not(is(emptyOrNullString())))
                .body("expires_in", not(is(emptyOrNullString())));
        //   .body("refresh_token", not(is(emptyOrNullString()))); // не возвращается, ошибка?
    }

    @Test
    @DisplayName("Resource Owner Password Credentials Grant")
    public void authPlayerTest() {
        ExtractableResponse<Response> tokenResponse = getClientToken().extract();
        String tokenType = tokenResponse.path("token_type");
        String accessToken = tokenResponse.path("access_token");

        PlayerRegisterRequest registerRequest = buildRegisterPlayersRequest();
        client.registerPlayer(registerRequest, tokenType, accessToken)
                .statusCode(201);

        GetAccessTokenRequest playerTokenRequest = GetAccessTokenRequest.builder()
                .grant_type(PASSWORD.getValue())
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword_change())
                .build();

        client.getToken(playerTokenRequest)
                .statusCode(200)
                .body("access_token", not(is(emptyOrNullString())))
                .body("token_type", not(is(emptyOrNullString())))
                .body("expires_in", not(is(emptyOrNullString())))
                .body("refresh_token", not(is(emptyOrNullString())));
    }
}
