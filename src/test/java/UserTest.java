import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import model.Currency;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import requests.GetAccessTokenRequest;
import requests.PlayerRegisterRequest;

import java.util.Random;

import static model.GrantType.*;
import static org.hamcrest.Matchers.*;

public class UserTest {
    private static StotegratorClient client;

    @BeforeAll
    static void init() {
        client = new StotegratorClient();
    }

    /*
    - добавить логирование для асёртов (в тестах?)
    - добавить описание тестам
    - разделить клиент на два с базовым классом
    - дописать тесты
    - выделить повторяющиеся куски
    - разбить тестирование методов по разным классам?
    - добавить Readme file
    -разобратся с гит игнор
     */

    @Test
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
        //   .body("refresh_token", not(is(emptyOrNullString())));
    }

    @Test
    public void registerPlayerTest() {
        GetAccessTokenRequest request = GetAccessTokenRequest.builder()
                .grant_type(CLIENT_CREDENTIALS.getValue())
                .scope("guest:default")
                .build();

        ExtractableResponse<Response> tokenResponse = client.getToken(request)
                .statusCode(200)
                .extract();
        String accessToken = tokenResponse.path("access_token");
        String tokenType = tokenResponse.path("token_type");

        PlayerRegisterRequest registerRequest = PlayerRegisterRequest.builder()
                .username("Username" + new Random().nextInt())
                .password_change("m9obmRvZTEyMw==")
                .password_repeat("m9obmRvZTEyMw==")
                .currency_code(Currency.RUB)
                .email(String.format("naskj%s@example.com", new Random().nextInt()))
                .name("Ivan")
                .surname("Ivanov")
                .build();

        client.registerPlayer(registerRequest, tokenType, accessToken)
                .statusCode(201)
                .body("id", not(is(emptyOrNullString())))
                .body("username", is(registerRequest.getUsername()))
                .body("name", is(registerRequest.getName()))
                .body("surname", is(registerRequest.getSurname()))
                .body("email", is(registerRequest.getEmail()));
    }

    @Test
    public void authPlayerTest() {
        GetAccessTokenRequest request = GetAccessTokenRequest.builder()
                .grant_type(CLIENT_CREDENTIALS.getValue())
                .scope("guest:default")
                .build();

        ExtractableResponse<Response> tokenResponse = client.getToken(request)
                .statusCode(200)
                .extract();
        String accessToken = tokenResponse.path("access_token");
        String tokenType = tokenResponse.path("token_type");

        PlayerRegisterRequest registerRequest = PlayerRegisterRequest.builder()
                .username("Username" + new Random().nextInt())
                .password_change("m9obmRvZTEyMw==")
                .password_repeat("m9obmRvZTEyMw==")
                //.currency_code(Currency.EUR)
                .email(String.format("naskj%s@example.com", new Random().nextInt()))
                .name("Ivan")
                .surname("Ivanov")
                .build();

        client.registerPlayer(registerRequest, tokenType, accessToken)
                .statusCode(201);


        GetAccessTokenRequest requestPlayer = GetAccessTokenRequest.builder()
                .grant_type(PASSWORD.getValue())
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword_change())
                .build();

        client.getToken(requestPlayer)
                .statusCode(200)
                .body("access_token", not(is(emptyOrNullString())))
                .body("token_type", not(is(emptyOrNullString())))
                .body("expires_in", not(is(emptyOrNullString())))
                .body("refresh_token", not(is(emptyOrNullString())));
    }

    @Test
    public void getSinglePlayerTest() {
        GetAccessTokenRequest request = GetAccessTokenRequest.builder()
                .grant_type(CLIENT_CREDENTIALS.getValue())
                .scope("guest:default")
                .build();

        ExtractableResponse<Response> tokenResponse = client.getToken(request)
                .statusCode(200)
                .extract();
        String accessToken = tokenResponse.path("access_token");
        String tokenType = tokenResponse.path("token_type");

        PlayerRegisterRequest registerRequest = PlayerRegisterRequest.builder()
                .username("Username" + new Random().nextInt())
                .password_change("m9obmRvZTEyMw==")
                .password_repeat("m9obmRvZTEyMw==")
                //.currency_code(Currency.EUR)
                .email(String.format("naskj%s@example.com", new Random().nextInt()))
                .name("Ivan")
                .surname("Ivanov")
                .build();

        Integer id = client.registerPlayer(registerRequest, tokenType, accessToken)
                .statusCode(201)
                .extract()
                .path("id");


        GetAccessTokenRequest requestPlayer = GetAccessTokenRequest.builder()
                .grant_type(PASSWORD.getValue())
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword_change())
                .build();

        ExtractableResponse<Response> e2 = client.getToken(requestPlayer)
                .statusCode(200)
                .extract();
        String accessToken2 = e2.path("access_token");
        String tokenType2 = e2.path("token_type");

        client.getPlayerProfile(id.toString(), tokenType2, accessToken2)
                .statusCode(200)
                .body("id", is(id))
                .body("username", is(registerRequest.getUsername()))
                .body("name", is(registerRequest.getName()))
                .body("surname", is(registerRequest.getSurname()))
                .body("email", is(registerRequest.getEmail()));
    }

    @Test
    public void getAnotherPlayerTest() {
        GetAccessTokenRequest request = GetAccessTokenRequest.builder()
                .grant_type(CLIENT_CREDENTIALS.getValue())
                .scope("guest:default")
                .build();

        ExtractableResponse<Response> tokenResponse = client.getToken(request)
                .statusCode(200)
                .extract();
        String accessToken = tokenResponse.path("access_token");
        String tokenType = tokenResponse.path("token_type");

        PlayerRegisterRequest registerRequest = PlayerRegisterRequest.builder()
                .username("Username" + new Random().nextInt())
                .password_change("m9obmRvZTEyMw==")
                .password_repeat("m9obmRvZTEyMw==")
                //.currency_code(Currency.EUR)
                .email(String.format("naskj%s@example.com", new Random().nextInt()))
                .name("Ivan")
                .surname("Ivanov")
                .build();

        client.registerPlayer(registerRequest, tokenType, accessToken)
                .statusCode(201);


        GetAccessTokenRequest requestPlayer = GetAccessTokenRequest.builder()
                .grant_type(PASSWORD.getValue())
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword_change())
                .build();

        ExtractableResponse<Response> e2 = client.getToken(requestPlayer)
                .statusCode(200)
                .extract();
        String accessToken2 = e2.path("access_token");
        String tokenType2 = e2.path("token_type");

        client.getPlayerProfile("1", tokenType2, accessToken2)
                .statusCode(404);
    }


}
