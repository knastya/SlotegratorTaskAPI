import io.restassured.RestAssured;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.util.Arrays;
import java.util.List;


public class StotegratorClient {
    private static final String BASE_URL = "http://test-api.d6.dev.devcaz.com";

    private static final String GET_TOKEN = "/v2/oauth2/token";
    private static final String REGISTER_PLAYER = "/v2/players";
    private static final String GET_PLAYER = "/v2/players/{id}";

    private static final String USERNAME = "front_2d6b0a8391742f5d789d7d915755e09e";
    private static final String PASSWORD = "123";

    public ValidatableResponse getToken(Object jsonObject) {
        System.out.println("Send request to get token\n");
        return post(GET_TOKEN, jsonObject, getSpecWithAuthorization());
    }

    public ValidatableResponse registerPlayer(Object jsonObject, String type, String token) {
        System.out.println("Send request to register player\n");
        return post(REGISTER_PLAYER, jsonObject, getSpecWithAuthorization(type, token));
    }

    public ValidatableResponse getPlayerProfile(String id, String type, String token){
        System.out.println("Send request to get player profile\n");
        return get(GET_PLAYER, id, getSpecWithAuthorization(type, token));
    }

    private RequestSpecBuilder getInitialSpecBuilder() {
        List<Filter> filters = Arrays.asList(new RequestLoggingFilter(), new ResponseLoggingFilter());
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .addFilters(filters);
    }

    private RequestSpecification getSpecWithAuthorization(String type, String token){
        return getInitialSpecBuilder()
                .addHeader("Authorization", String.format("%s %s", type, token))
                .build();
    }

    private RequestSpecification getSpecWithAuthorization(){
        return getInitialSpecBuilder()
                .setAuth(getAuthScheme())
                .build();
    }

    private PreemptiveBasicAuthScheme getAuthScheme() {
        PreemptiveBasicAuthScheme authenticationScheme = new PreemptiveBasicAuthScheme();
        authenticationScheme.setUserName(USERNAME);
        authenticationScheme.setPassword(PASSWORD);
        return authenticationScheme;
    }

    private ValidatableResponse post(String url, Object jsonObject, RequestSpecification requestSpecification) {
        return RestAssured.given()
                .spec(requestSpecification)
                .contentType(ContentType.JSON)
                .body(jsonObject)
                .post(url)
                .then();
    }

    private ValidatableResponse get(String url, String id, RequestSpecification requestSpecification){
        return RestAssured.given()
                .spec(requestSpecification)
                .contentType(ContentType.JSON)
                .get(url, id)
                .then();
    }
}
