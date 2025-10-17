package courseitda.member.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import courseitda.common.exception.ErrorCode;
import courseitda.member.domain.MemberFixture;
import courseitda.member.ui.dto.request.SignUpRequest;
import courseitda.member.ui.dto.response.SignUpResponse;
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
class MemberControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    @DisplayName("회원가입 성공 시나리오")
    class SignUpSuccessScenarios {

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
                    .post("/api/members")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract()
                    .as(SignUpResponse.class);

            // then
            assertThat(response.id()).isNotNull();
            assertThat(response.nickname()).isEqualTo(nickname);
            assertThat(response.email()).isEqualTo(email);
        }
    }

    @Nested
    @DisplayName("회원가입 실패 시나리오")
    class SignUpFailureScenarios {

        @Test
        @DisplayName("중복된 이메일로 회원가입 시 실패한다")
        void signup_fail_duplicateEmail() {
            // given
            final String nickname = MemberFixture.anyNickname();
            final String email = MemberFixture.anyEmail();
            final String password = MemberFixture.anyPassword();
            final SignUpRequest request = new SignUpRequest(nickname, email, password);

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .post("/api/members")
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            final String anotherNickname = MemberFixture.anyNickname();
            final SignUpRequest duplicateEmailRequest = new SignUpRequest(anotherNickname, email, password);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(duplicateEmailRequest)
                    .when()
                    .post("/api/members")
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.DUPLICATE_EMAIL.getCode()));
        }

        @Test
        @DisplayName("중복된 닉네임으로 회원가입 시 실패한다")
        void signup_fail_duplicateNickname() {
            // given
            final String nickname = MemberFixture.anyNickname();
            final String email = MemberFixture.anyEmail();
            final String password = MemberFixture.anyPassword();
            final SignUpRequest request = new SignUpRequest(nickname, email, password);

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .post("/api/members")
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            final String anotherEmail = MemberFixture.anyEmail();
            final SignUpRequest duplicateNicknameRequest = new SignUpRequest(nickname, anotherEmail, password);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(duplicateNicknameRequest)
                    .when()
                    .post("/api/members")
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.DUPLICATE_NICKNAME.getCode()));
        }
    }
}
