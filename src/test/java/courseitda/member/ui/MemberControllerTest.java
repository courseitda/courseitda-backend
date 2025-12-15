package courseitda.member.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import courseitda.common.exception.ErrorCode;
import courseitda.member.domain.MemberFixture;
import courseitda.member.ui.dto.request.SignUpRequest;
import courseitda.member.ui.dto.response.CheckEmailDuplicateResponse;
import courseitda.member.ui.dto.response.CheckNicknameDuplicateResponse;
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
        @DisplayName("닉네임이 2자 미만인 경우 회원가입에 실패한다")
        void signup_fail_nicknameTooShort() {
            // given
            final String shortNickname = "a";
            final String email = MemberFixture.anyEmail();
            final String password = MemberFixture.anyPassword();
            final SignUpRequest request = new SignUpRequest(shortNickname, email, password);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .post("/api/members")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.INVALID_NICKNAME_LENGTH.getCode()));
        }

        @Test
        @DisplayName("닉네임이 20자 초과인 경우 회원가입에 실패한다")
        void signup_fail_nicknameTooLong() {
            // given
            final String longNickname = "a".repeat(21);
            final String email = MemberFixture.anyEmail();
            final String password = MemberFixture.anyPassword();
            final SignUpRequest request = new SignUpRequest(longNickname, email, password);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .post("/api/members")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.INVALID_NICKNAME_LENGTH.getCode()));
        }

        @Test
        @DisplayName("이메일 형식이 잘못된 경우 회원가입에 실패한다")
        void signup_fail_invalidEmailFormat() {
            // given
            final String nickname = MemberFixture.anyNickname();
            final String invalidEmail = "invalid-email";
            final String password = MemberFixture.anyPassword();
            final SignUpRequest request = new SignUpRequest(nickname, invalidEmail, password);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .post("/api/members")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.INVALID_EMAIL_FORMAT.getCode()));
        }

        @Test
        @DisplayName("비밀번호가 6자 미만인 경우 회원가입에 실패한다")
        void signup_fail_passwordTooShort() {
            // given
            final String nickname = MemberFixture.anyNickname();
            final String email = MemberFixture.anyEmail();
            final String shortPassword = "pass1";
            final SignUpRequest request = new SignUpRequest(nickname, email, shortPassword);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .post("/api/members")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.INVALID_PASSWORD_LENGTH.getCode()));
        }

        @Test
        @DisplayName("비밀번호가 20자 초과인 경우 회원가입에 실패한다")
        void signup_fail_passwordTooLong() {
            // given
            final String nickname = MemberFixture.anyNickname();
            final String email = MemberFixture.anyEmail();
            final String longPassword = "a".repeat(21);
            final SignUpRequest request = new SignUpRequest(nickname, email, longPassword);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .post("/api/members")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.INVALID_PASSWORD_LENGTH.getCode()));
        }

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

    @Nested
    @DisplayName("닉네임 중복 검증 성공 시나리오")
    class CheckNicknameDuplicateSuccessScenarios {

        @Test
        @DisplayName("중복되지 않은 닉네임을 검증한다")
        void checkNicknameDuplicate_success_notDuplicated() {
            // given
            final String nickname = MemberFixture.anyNickname();

            // when
            final CheckNicknameDuplicateResponse response = given()
                    .queryParam("value", nickname)
                    .when()
                    .get("/api/members/validations/nickname")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(CheckNicknameDuplicateResponse.class);

            // then
            assertThat(response.isDuplicated()).isFalse();
        }

        @Test
        @DisplayName("중복된 닉네임을 검증한다")
        void checkNicknameDuplicate_success_duplicated() {
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

            // when
            final CheckNicknameDuplicateResponse response = given()
                    .queryParam("value", nickname)
                    .when()
                    .get("/api/members/validations/nickname")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(CheckNicknameDuplicateResponse.class);

            // then
            assertThat(response.isDuplicated()).isTrue();
        }
    }

    @Nested
    @DisplayName("닉네임 중복 검증 실패 시나리오")
    class CheckNicknameDuplicateFailureScenarios {

        @Test
        @DisplayName("닉네임이 null인 경우 검증에 실패한다")
        void checkNicknameDuplicate_fail_nullNickname() {
            // when & then
            given()
                    .when()
                    .get("/api/members/validations/nickname")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.MEMBER_NICKNAME_EMPTY.getCode()));
        }

        @Test
        @DisplayName("닉네임이 빈 문자열인 경우 검증에 실패한다")
        void checkNicknameDuplicate_fail_emptyNickname() {
            // given
            final String emptyNickname = "";

            // when & then
            given()
                    .queryParam("value", emptyNickname)
                    .when()
                    .get("/api/members/validations/nickname")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.MEMBER_NICKNAME_EMPTY.getCode()));
        }

        @Test
        @DisplayName("닉네임이 2자 미만인 경우 검증에 실패한다")
        void checkNicknameDuplicate_fail_nicknameTooShort() {
            // given
            final String shortNickname = "a";

            // when & then
            given()
                    .queryParam("value", shortNickname)
                    .when()
                    .get("/api/members/validations/nickname")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.INVALID_NICKNAME_LENGTH.getCode()));
        }

        @Test
        @DisplayName("닉네임이 20자 초과인 경우 검증에 실패한다")
        void checkNicknameDuplicate_fail_nicknameTooLong() {
            // given
            final String longNickname = "a".repeat(21);

            // when & then
            given()
                    .queryParam("value", longNickname)
                    .when()
                    .get("/api/members/validations/nickname")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.INVALID_NICKNAME_LENGTH.getCode()));
        }
    }

    @Nested
    @DisplayName("이메일 중복 검증 성공 시나리오")
    class CheckEmailDuplicateSuccessScenarios {

        @Test
        @DisplayName("중복되지 않은 이메일을 검증한다")
        void checkEmailDuplicate_success_notDuplicated() {
            // given
            final String email = MemberFixture.anyEmail();

            // when
            final CheckEmailDuplicateResponse response = given()
                    .queryParam("value", email)
                    .when()
                    .get("/api/members/validations/email")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(CheckEmailDuplicateResponse.class);

            // then
            assertThat(response.isDuplicated()).isFalse();
        }

        @Test
        @DisplayName("중복된 이메일을 검증한다")
        void checkEmailDuplicate_success_duplicated() {
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

            // when
            final CheckEmailDuplicateResponse response = given()
                    .queryParam("value", email)
                    .when()
                    .get("/api/members/validations/email")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(CheckEmailDuplicateResponse.class);

            // then
            assertThat(response.isDuplicated()).isTrue();
        }
    }

    @Nested
    @DisplayName("이메일 중복 검증 실패 시나리오")
    class CheckEmailDuplicateFailureScenarios {

        @Test
        @DisplayName("이메일이 null인 경우 검증에 실패한다")
        void checkEmailDuplicate_fail_nullEmail() {
            // when & then
            given()
                    .when()
                    .get("/api/members/validations/email")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.MEMBER_EMAIL_EMPTY.getCode()));
        }

        @Test
        @DisplayName("이메일이 빈 문자열인 경우 검증에 실패한다")
        void checkEmailDuplicate_fail_emptyEmail() {
            // given
            final String emptyEmail = "";

            // when & then
            given()
                    .queryParam("value", emptyEmail)
                    .when()
                    .get("/api/members/validations/email")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.MEMBER_EMAIL_EMPTY.getCode()));
        }

        @Test
        @DisplayName("이메일 형식이 잘못된 경우 검증에 실패한다")
        void checkEmailDuplicate_fail_invalidEmailFormat() {
            // given
            final String invalidEmail = "invalid-email";

            // when & then
            given()
                    .queryParam("value", invalidEmail)
                    .when()
                    .get("/api/members/validations/email")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.INVALID_EMAIL_FORMAT.getCode()));
        }
    }
}
