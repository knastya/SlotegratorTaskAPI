import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import model.Currency;
import org.junit.jupiter.api.Test;
import requests.PlayerRegisterRequest;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;

public class RegisterPlayerTests extends PlayersBase {
    /* TODO Можно ещё добавить тестов, но уже сил нет)
         - на длину полей
         - корректность имейла
         - тесты по паролю
     */

    //Register a new player with only mandatory fields
    @Test
    public void mandatoryFieldsTest() {
        ExtractableResponse<Response> tokenResponse = getClientToken().extract();
        String accessToken = tokenResponse.path("access_token");
        String tokenType = tokenResponse.path("token_type");

        String password = getPassword();

        PlayerRegisterRequest registerRequest = PlayerRegisterRequest.builder()
                .username(genRandomUsername())
                .password_change(password)
                .password_repeat(password)
                .email(genRandomEmail())
                .build();

        client.registerPlayer(registerRequest, tokenType, accessToken)
                .statusCode(201)
                .body("id", not(is(emptyOrNullString())))
                .body("country_id", nullValue())
                .body("timezone_id", nullValue())
                .body("username", is(registerRequest.getUsername()))
                .body("email", is(registerRequest.getEmail()))
                .body("name", nullValue())
                .body("surname", nullValue())
                .body("gender", nullValue())
                .body("phone_number", nullValue())
                .body("birthdate", nullValue())
                .body("bonuses_allowed", is(true))
                .body("is_verified", is(false));
    }

    //Register a new player with all fields
    @Test
    public void allPramsTest() {
        ExtractableResponse<Response> tokenResponse = getClientToken().extract();
        String accessToken = tokenResponse.path("access_token");
        String tokenType = tokenResponse.path("token_type");

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

        client.registerPlayer(registerRequest, tokenType, accessToken)
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
                .body("is_verified", is(false));
    }

    //Check another currency
    @Test
    public void currencyTest() {
        ExtractableResponse<Response> tokenResponse = getClientToken().extract();
        String accessToken = tokenResponse.path("access_token");
        String tokenType = tokenResponse.path("token_type");

        String password = getPassword();

        PlayerRegisterRequest registerRequest = PlayerRegisterRequest.builder()
                .username(genRandomUsername())
                .password_change(password)
                .password_repeat(password)
                .email(genRandomEmail())
                .currency_code(Currency.EUR)
                .build();

        client.registerPlayer(registerRequest, tokenType, accessToken)
                .statusCode(201)
                .body("id", not(is(emptyOrNullString())))
                .body("country_id", nullValue())
                .body("timezone_id", nullValue())
                .body("username", is(registerRequest.getUsername()))
                .body("email", is(registerRequest.getEmail()))
                .body("name", nullValue())
                .body("surname", nullValue())
                .body("gender", nullValue())
                .body("phone_number", nullValue())
                .body("birthdate", nullValue())
                .body("bonuses_allowed", is(true))
                .body("is_verified", is(false));
    }

    //Check existed username
    @Test
    public void existedUsernameTest() {
        ExtractableResponse<Response> tokenResponse = getClientToken().extract();
        String accessToken = tokenResponse.path("access_token");
        String tokenType = tokenResponse.path("token_type");

        String password = getPassword();

        PlayerRegisterRequest registerRequest = PlayerRegisterRequest.builder()
                .username(genRandomUsername())
                .password_change(password)
                .password_repeat(password)
                .email(genRandomEmail())
                .build();

        client.registerPlayer(registerRequest, tokenType, accessToken)
                .statusCode(201);

        registerRequest.setEmail(genRandomEmail());
        client.registerPlayer(registerRequest, tokenType, accessToken)
                .statusCode(422)
                .body("[0].field", is("username"))
                .body("[0].message",
                        is(String.format("Username \"%s\" has already been taken.", registerRequest.getUsername())));
    }

    //Check existed email
    @Test
    public void existedEmailTest() {
        ExtractableResponse<Response> tokenResponse = getClientToken().extract();
        String accessToken = tokenResponse.path("access_token");
        String tokenType = tokenResponse.path("token_type");

        String password = getPassword();

        PlayerRegisterRequest registerRequest = PlayerRegisterRequest.builder()
                .username(genRandomUsername())
                .password_change(password)
                .password_repeat(password)
                .email(genRandomEmail())
                .build();

        client.registerPlayer(registerRequest, tokenType, accessToken)
                .statusCode(201);

        registerRequest.setUsername(genRandomUsername());
        client.registerPlayer(registerRequest, tokenType, accessToken)
                .statusCode(422)
                .body("[0].field", is("email"))
                .body("[0].message",
                        is(String.format("Email \"%s\" has already been taken.", registerRequest.getEmail())));
    }


    /* TODO
        не разобралась с логикой пароля, получала и 500ки и 422
        в реальности обратилась бы к разработчику/требованиям
        @Test
        public void passwordTest() {}
     */
}
