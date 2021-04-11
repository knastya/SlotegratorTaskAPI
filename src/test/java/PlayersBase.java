import io.restassured.response.ValidatableResponse;
import model.Currency;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeAll;
import requests.GetAccessTokenRequest;
import requests.PlayerRegisterRequest;

import java.util.Random;

import static model.GrantType.CLIENT_CREDENTIALS;
import static model.GrantType.PASSWORD;

public class PlayersBase {
    protected static StotegratorClient client;

    @BeforeAll
    static void init() {
        client = new StotegratorClient();
    }

    /* TODO
        - в целом, добавила бы ещё логирования для проверок в body
        - добавить описания в Readme file
     */

    public ValidatableResponse getClientToken() {
        GetAccessTokenRequest request = GetAccessTokenRequest.builder()
                .grant_type(CLIENT_CREDENTIALS.getValue())
                .scope("guest:default")
                .build();

        return client.getToken(request)
                .statusCode(200);
    }

    public ValidatableResponse getPlayerToken(String userName, String password) {
        GetAccessTokenRequest requestPlayer = GetAccessTokenRequest.builder()
                .grant_type(PASSWORD.getValue())
                .username(userName)
                .password(password)
                .build();

        return client.getToken(requestPlayer)
                .statusCode(200);
    }

    public PlayerRegisterRequest buildRegisterPlayersRequest() {
        String password = getPassword();
        return PlayerRegisterRequest.builder()
                .username(genRandomUsername())
                .password_change(password)
                .password_repeat(password)
                .currency_code(Currency.RUB)
                .email(genRandomEmail())
                .build();
    }

    //TODO it doesn't work
    public String genPassword() {
        byte[] bytes = new byte[10];
        new Random().nextBytes(bytes);
        return Base64.encodeBase64String(bytes);
    }

    public String getPassword() {
        return "amFuZWRvZTEyMw==";
    }

    public String genRandomUsername() {
        return "Username" + new Random().nextInt();
    }

    public String genRandomEmail() {
        return String.format("test%s@example.com", new Random().nextInt());
    }

}
