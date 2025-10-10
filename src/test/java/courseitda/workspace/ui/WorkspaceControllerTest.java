package courseitda.workspace.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import courseitda.auth.ui.dto.request.LoginRequest;
import courseitda.auth.ui.dto.response.LoginResponse;
import courseitda.member.domain.MemberFixture;
import courseitda.member.ui.dto.request.SignUpRequest;
import courseitda.workspace.domain.WorkspaceFixture;
import courseitda.workspace.ui.dto.request.WorkspaceCreateRequest;
import courseitda.workspace.ui.dto.request.WorkspaceUpdateRequest;
import courseitda.workspace.ui.dto.response.WorkspaceCreateResponse;
import courseitda.workspace.ui.dto.response.WorkspaceUpdateResponse;
import io.restassured.RestAssured;
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

@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WorkspaceControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("워크스페이스 생성에 성공한다")
    void createWorkspace_success() {
        // given
        final String accessToken = signUpAndLogin();
        final String title = WorkspaceFixture.anyTitle();
        final WorkspaceCreateRequest request = new WorkspaceCreateRequest(title);

        // when
        final WorkspaceCreateResponse response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(request)
                .when()
                .post("/api/workspaces")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(WorkspaceCreateResponse.class);

        // then
        assertThat(response.id()).isNotNull();
        assertThat(response.title()).isEqualTo(title);
        assertThat(response.memberId()).isNotNull();
    }

    @Test
    @DisplayName("워크스페이스 수정에 성공한다")
    void updateWorkspace_success() {
        // given
        final String accessToken = signUpAndLogin();
        final String originalTitle = WorkspaceFixture.anyTitle();
        final WorkspaceCreateRequest createRequest = new WorkspaceCreateRequest(originalTitle);

        final WorkspaceCreateResponse createResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(createRequest)
                .when()
                .post("/api/workspaces")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(WorkspaceCreateResponse.class);

        final String newTitle = WorkspaceFixture.anyTitle();
        final WorkspaceUpdateRequest updateRequest = new WorkspaceUpdateRequest(newTitle);

        // when
        final WorkspaceUpdateResponse response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(updateRequest)
                .when()
                .patch("/api/workspaces/" + createResponse.id())
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(WorkspaceUpdateResponse.class);

        // then
        assertThat(response.id()).isEqualTo(createResponse.id());
        assertThat(response.title()).isEqualTo(newTitle);
    }

    @Test
    @DisplayName("워크스페이스 삭제에 성공한다")
    void deleteWorkspace_success() {
        // given
        final String accessToken = signUpAndLogin();
        final String title = WorkspaceFixture.anyTitle();
        final WorkspaceCreateRequest createRequest = new WorkspaceCreateRequest(title);

        final WorkspaceCreateResponse createResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(createRequest)
                .when()
                .post("/api/workspaces")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(WorkspaceCreateResponse.class);

        // when & then
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .when()
                .delete("/api/workspaces/" + createResponse.id())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    private String signUpAndLogin() {
        final String email = MemberFixture.anyEmail();
        final String password = MemberFixture.anyPassword();
        final String nickname = MemberFixture.anyNickname();
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

        return loginResponse.tokenType() + " " + loginResponse.accessToken();
    }
}

