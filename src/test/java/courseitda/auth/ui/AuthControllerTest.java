package courseitda.auth.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import courseitda.auth.ui.dto.request.LoginRequest;
import courseitda.auth.ui.dto.response.LoginResponse;
import courseitda.common.exception.ErrorCode;
import courseitda.member.domain.MemberFixture;
import courseitda.member.ui.dto.request.SignUpRequest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    private void signUp(final String nickname, final String email, final String password) {
        final SignUpRequest signUpRequest = new SignUpRequest(nickname, email, password);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(signUpRequest)
                .when()
                .post("/api/members")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Nested
    @DisplayName("로그인 성공 시나리오")
    class LoginSuccessScenarios {

        @Test
        @DisplayName("로그인에 성공한다")
        void login_success() {
            // given
            final String email = MemberFixture.anyEmail();
            final String password = MemberFixture.anyPassword();
            final String nickname = MemberFixture.anyNickname();

            signUp(nickname, email, password);

            final LoginRequest loginRequest = new LoginRequest(email, password);

            // when
            final LoginResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(loginRequest)
                    .when()
                    .post("/api/auth/login")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(LoginResponse.class);

            // then
            assertThat(response.tokenType()).isEqualTo("Bearer");
            assertThat(response.accessToken()).isNotNull();
            assertThat(response.accessToken()).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("로그인 실패 시나리오")
    class LoginFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 이메일로 로그인 시 실패한다")
        void login_fail_memberNotFoundByEmail() {
            // given
            final String nonExistentEmail = MemberFixture.anyEmail();
            final String password = MemberFixture.anyPassword();
            final LoginRequest loginRequest = new LoginRequest(nonExistentEmail, password);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(loginRequest)
                    .when()
                    .post("/api/auth/login")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.MEMBER_NOT_FOUND_BY_EMAIL.getCode()));
        }

        @Test
        @DisplayName("잘못된 비밀번호로 로그인 시 실패한다")
        void login_fail_incorrectPassword() {
            // given
            final String email = MemberFixture.anyEmail();
            final String password = MemberFixture.anyPassword();
            final String nickname = MemberFixture.anyNickname();

            signUp(nickname, email, password);

            final String wrongPassword = "wrongPassword123!";
            final LoginRequest loginRequest = new LoginRequest(email, wrongPassword);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(loginRequest)
                    .when()
                    .post("/api/auth/login")
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.INCORRECT_PASSWORD.getCode()));
        }
    }

}
