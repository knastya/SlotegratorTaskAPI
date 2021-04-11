import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import requests.PlayerRegisterRequest;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.nullValue;

public class GetProfilePlayerTests extends PlayersBase {

    //Get own players profile
    @Test
    public void getSinglePlayerTest() {
        ExtractableResponse<Response> tokenResponse = getClientToken().extract();
        String tokenType = tokenResponse.path("token_type");
        String accessToken = tokenResponse.path("access_token");

        PlayerRegisterRequest registerRequest = buildRegisterPlayersRequest();
        Integer id = client.registerPlayer(registerRequest, tokenType, accessToken)
                .statusCode(201)
                .extract()
                .path("id");

        ExtractableResponse<Response> playerTokenResponse =
                getPlayerToken(registerRequest.getUsername(), registerRequest.getPassword_change())
                        .extract();
        String playerTokenType = playerTokenResponse.path("token_type");
        String playerAccessToken = playerTokenResponse.path("access_token");

        client.getPlayerProfile(id.toString(), playerTokenType, playerAccessToken)
                .statusCode(200)
                .body("id", is(id))
                .body("country_id", nullValue())
                .body("timezone_id", nullValue())
                .body("username", is(registerRequest.getUsername()))
                .body("email", is(registerRequest.getEmail()))
                .body("name", is(registerRequest.getName()))
                .body("surname", is(registerRequest.getSurname()))
                .body("gender", nullValue())
                .body("phone_number", nullValue())
                .body("birthdate", nullValue())
                .body("bonuses_allowed", is(true))
                .body("is_verified", is(false));
    }

    //Get other players profile
    @Test
    public void getAnotherPlayerTest() {
        ExtractableResponse<Response> tokenResponse = getClientToken().extract();
        String tokenType = tokenResponse.path("token_type");
        String accessToken = tokenResponse.path("access_token");

        PlayerRegisterRequest registerRequest = buildRegisterPlayersRequest();
        client.registerPlayer(registerRequest, tokenType, accessToken)
                .statusCode(201);

        ExtractableResponse<Response> playerTokenResponse =
                getPlayerToken(registerRequest.getUsername(), registerRequest.getPassword_change())
                        .extract();
        String accessToken2 = playerTokenResponse.path("access_token");
        String tokenType2 = playerTokenResponse.path("token_type");

        client.getPlayerProfile("1", tokenType2, accessToken2)
                .statusCode(404);
    }

}
