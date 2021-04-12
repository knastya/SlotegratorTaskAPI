import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import model.Currency;
import org.junit.jupiter.api.Test;
import requests.GetAccessTokenRequest;
import requests.PlayerRegisterRequest;

import static model.GrantType.CLIENT_CREDENTIALS;
import static model.GrantType.PASSWORD;
import static org.hamcrest.Matchers.*;

public class EndToEndTest extends PlayersBase {
    /*
     Не совсем поняла задание, нужен был один тест или нужно было протестить каждый метод,
     поэтому сделала и то, и то)
     */

    /*
    Action 1. Get user token
    Result 1. Token is received
    Action 2. Register player
    Result 2. Player is registered and data is correct
    Action 3. Get player token
    Result 3. Token is received
    Action 4. Get own profile player
    Result 4. Player profile is received and data is correct
    Action 5. Get other profile player
    Result 5. Status code is 404.
     */
    @Test
    public void endToEndTest() {
        // ----------------------Get user token-------------------
        GetAccessTokenRequest request = GetAccessTokenRequest.builder()
                .grant_type(CLIENT_CREDENTIALS.getValue())
                .scope("guest:default")
                .build();

        ExtractableResponse<Response> tokenResponse = client.getToken(request)
                .statusCode(200)
                .body("access_token", not(is(emptyOrNullString())))
                .body("token_type", not(is(emptyOrNullString())))
                .body("expires_in", not(is(emptyOrNullString())))
                //   .body("refresh_token", not(is(emptyOrNullString()))) // не возвращается, ошибка?
                .extract();

        String accessToken = tokenResponse.path("access_token");
        String tokenType = tokenResponse.path("token_type");

        // --------------------Register player-----------------------
        String password = getPassword();

        PlayerRegisterRequest registerRequest = PlayerRegisterRequest.builder()
                .username(genRandomUsername())
                .password_change(password)
                .password_repeat(password)
                .email(genRandomEmail())
                .currency_code(Currency.RUB)
                .name("Ivan")
                .surname("Ivanov")
                .build();

        Integer id = client.registerPlayer(registerRequest, tokenType, accessToken)
                .statusCode(201)
                .body("id", not(is(emptyOrNullString())))
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
                .body("is_verified", is(false))
                .extract()
                .path("id");

        // -------------------------Get player token----------------------------

        GetAccessTokenRequest playerTokenRequest = GetAccessTokenRequest.builder()
                .grant_type(PASSWORD.getValue())
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword_change())
                .build();

        ExtractableResponse<Response> playerTokenResponse = client.getToken(playerTokenRequest)
                .statusCode(200)
                .body("access_token", not(is(emptyOrNullString())))
                .body("token_type", not(is(emptyOrNullString())))
                .body("expires_in", not(is(emptyOrNullString())))
                .body("refresh_token", not(is(emptyOrNullString())))
                .extract();

        String playerTokenType = playerTokenResponse.path("token_type");
        String playerAccessToken = playerTokenResponse.path("access_token");

        // -------------------------Get own profile player----------------------------

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

        // -------------------------Get other profile player----------------------------

        client.getPlayerProfile("1", playerTokenType, playerAccessToken)
                .statusCode(404);
    }

}
