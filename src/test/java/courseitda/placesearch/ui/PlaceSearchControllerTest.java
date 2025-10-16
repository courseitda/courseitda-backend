package courseitda.placesearch.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import courseitda.auth.ui.dto.request.LoginRequest;
import courseitda.auth.ui.dto.response.LoginResponse;
import courseitda.member.domain.MemberFixture;
import courseitda.member.ui.dto.request.SignUpRequest;
import courseitda.placesearch.domain.PlaceSearcher;
import courseitda.placesearch.domain.SearchedPlace;
import courseitda.placesearch.ui.dto.response.SearchedPlacesResponse;
import io.restassured.RestAssured;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Transactional
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PlaceSearchControllerTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private PlaceSearcher mockPlaceSearcher;

    private String accessToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        final String nickname = MemberFixture.anyNickname();
        final String email = MemberFixture.anyEmail();
        final String password = MemberFixture.anyPassword();
        final SignUpRequest signUpRequest = new SignUpRequest(nickname, email, password);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(signUpRequest)
                .when()
                .post("/api/members")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        final LoginRequest loginRequest = new LoginRequest(email, password);
        final LoginResponse loginResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(LoginResponse.class);

        accessToken = loginResponse.tokenType() + " " + loginResponse.accessToken();
    }

    @Test
    @DisplayName("장소 검색에 성공한다")
    void searchPlaces_success() {
        // given
        final List<SearchedPlace> mockSearchedPlaces = List.of(
                new SearchedPlace("스타벅스 강남점", "서울 강남구 역삼동 123-45", "서울 강남구 테헤란로 123", 37.498095, 127.027610),
                new SearchedPlace("스타벅스 역삼점", "서울 강남구 역삼동 678-90", "서울 강남구 테헤란로 456", 37.500123, 127.030456),
                new SearchedPlace("스타벅스 선릉점", "서울 강남구 역삼동 234-56", "서울 강남구 테헤란로 789", 37.504567, 127.049123)
        );
        given(mockPlaceSearcher.searchPlaces(anyString(), anyInt()))
                .willReturn(mockSearchedPlaces);

        // when
        final SearchedPlacesResponse response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .queryParam("keyword", "스타벅스")
                .when()
                .get("/api/places/search")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(SearchedPlacesResponse.class);

        // then
        assertThat(response.searchedPlaces()).hasSize(3);
        assertThat(response.searchedPlaces().get(0).name()).isEqualTo("스타벅스 강남점");
        assertThat(response.searchedPlaces().get(0).addressName()).isEqualTo("서울 강남구 역삼동 123-45");
        assertThat(response.searchedPlaces().get(0).roadAddressName()).isEqualTo("서울 강남구 테헤란로 123");
        assertThat(response.searchedPlaces().get(0).latitude()).isEqualTo(37.498095);
        assertThat(response.searchedPlaces().get(0).longitude()).isEqualTo(127.027610);
    }
}
