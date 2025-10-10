package courseitda.member.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import courseitda.auth.ui.dto.request.LoginRequest;
import courseitda.auth.ui.dto.response.LoginResponse;
import courseitda.member.domain.MemberFixture;
import courseitda.member.ui.dto.request.SignUpRequest;
import courseitda.member.ui.dto.response.MemberReadDropdownResponse;
import courseitda.member.ui.dto.response.MemberReadNavigatorResponse;
import courseitda.member.ui.dto.response.MemberReadProfileResponse;
import courseitda.member.ui.dto.response.SignUpResponse;
import io.restassured.RestAssured;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@Transactional
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("회원가입에 성공한다")
    void signup_success() {
        // given
        final String nickname = MemberFixture.anyNickname();
        final String email = MemberFixture.anyEmail();
        final String password = MemberFixture.anyPassword();
        final SignUpRequest request = new SignUpRequest(nickname, email, password);

        // when
        final SignUpResponse response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/members")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(SignUpResponse.class);

        // then
        assertThat(response.id()).isNotNull();
        assertThat(response.nickname()).isEqualTo(nickname);
        assertThat(response.email()).isEqualTo(email);
    }

    @Test
    @DisplayName("회원 정보(네비게이터 뷰) 조회에 성공한다")
    void readMemberNavigator_success() {
        // given
        final String nickname = MemberFixture.anyNickname();
        final String email = MemberFixture.anyEmail();
        final String password = MemberFixture.anyPassword();
        final SignUpRequest signUpRequest = new SignUpRequest(nickname, email, password);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(signUpRequest)
                .when()
                .post("/members")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        final LoginRequest loginRequest = new LoginRequest(email, password);
        final LoginResponse loginResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(LoginResponse.class);

        final String accessToken = loginResponse.tokenType() + " " + loginResponse.accessToken();

        // when
        final MemberReadNavigatorResponse response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .when()
                .get("/members/me/navigator")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(MemberReadNavigatorResponse.class);

        // then
        assertThat(response.nickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("회원 정보(드롭다운 뷰) 조회에 성공한다")
    void readMemberDropdown_success() {
        // given
        final String nickname = MemberFixture.anyNickname();
        final String email = MemberFixture.anyEmail();
        final String password = MemberFixture.anyPassword();
        final SignUpRequest signUpRequest = new SignUpRequest(nickname, email, password);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(signUpRequest)
                .when()
                .post("/members")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        final LoginRequest loginRequest = new LoginRequest(email, password);
        final LoginResponse loginResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(LoginResponse.class);

        final String accessToken = loginResponse.tokenType() + " " + loginResponse.accessToken();

        // when
        final MemberReadDropdownResponse response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .when()
                .get("/members/me/dropdown")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(MemberReadDropdownResponse.class);

        // then
        assertThat(response.nickname()).isEqualTo(nickname);
        assertThat(response.email()).isEqualTo(email);
    }

    @Test
    @DisplayName("회원 정보(프로필) 조회에 성공한다")
    void readMemberProfile_success() {
        // given
        final String nickname = MemberFixture.anyNickname();
        final String email = MemberFixture.anyEmail();
        final String password = MemberFixture.anyPassword();
        final SignUpRequest signUpRequest = new SignUpRequest(nickname, email, password);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(signUpRequest)
                .when()
                .post("/members")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        final LoginRequest loginRequest = new LoginRequest(email, password);
        final LoginResponse loginResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(LoginResponse.class);

        final String accessToken = loginResponse.tokenType() + " " + loginResponse.accessToken();

        // when
        final MemberReadProfileResponse response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .when()
                .get("/members/me/profile")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(MemberReadProfileResponse.class);

        // then
        assertThat(response.nickName()).isEqualTo(nickname);
        assertThat(response.email()).isEqualTo(email);
    }
}
