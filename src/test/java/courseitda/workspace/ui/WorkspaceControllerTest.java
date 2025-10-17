package courseitda.workspace.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import courseitda.auth.ui.dto.request.LoginRequest;
import courseitda.auth.ui.dto.response.LoginResponse;
import courseitda.common.exception.ErrorCode;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class WorkspaceControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    @DisplayName("워크스페이스 생성 성공 시나리오")
    class CreateWorkspaceSuccessScenarios {

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
            assertThat(response.identifier()).isNotNull();
            assertThat(response.title()).isEqualTo(title);
        }
    }

    @Nested
    @DisplayName("워크스페이스 생성 실패 시나리오")
    class CreateWorkspaceFailureScenarios {

        @Test
        @DisplayName("중복된 워크스페이스 제목으로 생성 시 실패한다")
        void createWorkspace_fail_duplicateTitle() {
            // given
            final String accessToken = signUpAndLogin();
            final String title = WorkspaceFixture.anyTitle();
            final WorkspaceCreateRequest request = new WorkspaceCreateRequest(title);

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .post("/api/workspaces")
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .post("/api/workspaces")
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.DUPLICATE_WORKSPACE_TITLE.getCode()));
        }
    }

    @Nested
    @DisplayName("워크스페이스 수정 성공 시나리오")
    class UpdateWorkspaceSuccessScenarios {

        @Test
        @DisplayName("워크스페이스 수정에 성공한다")
        void updateWorkspace_success() {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);

            final String newTitle = WorkspaceFixture.anyTitle();
            final WorkspaceUpdateRequest updateRequest = new WorkspaceUpdateRequest(newTitle);

            // when
            final WorkspaceUpdateResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(updateRequest)
                    .when()
                    .patch("/api/workspaces/" + workspaceIdentifier)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(WorkspaceUpdateResponse.class);

            // then
            assertThat(response.identifier()).isEqualTo(workspaceIdentifier);
            assertThat(response.title()).isEqualTo(newTitle);
        }
    }

    @Nested
    @DisplayName("워크스페이스 수정 실패 시나리오")
    class UpdateWorkspaceFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 워크스페이스 수정 시 실패한다")
        void updateWorkspace_fail_workspaceNotFound() {
            // given
            final String accessToken = signUpAndLogin();
            final String nonExistentIdentifier = "non-existent-identifier";
            final String newTitle = WorkspaceFixture.anyTitle();
            final WorkspaceUpdateRequest updateRequest = new WorkspaceUpdateRequest(newTitle);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(updateRequest)
                    .when()
                    .patch("/api/workspaces/" + nonExistentIdentifier)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.WORKSPACE_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("다른 사용자의 워크스페이스 수정 시 실패한다")
        void updateWorkspace_fail_workspaceModifyForbidden() {
            // given
            final String owner = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(owner);

            final String otherUser = signUpAndLogin();
            final String newTitle = WorkspaceFixture.anyTitle();
            final WorkspaceUpdateRequest updateRequest = new WorkspaceUpdateRequest(newTitle);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, otherUser)
                    .body(updateRequest)
                    .when()
                    .patch("/api/workspaces/" + workspaceIdentifier)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.WORKSPACE_MODIFY_FORBIDDEN.getCode()));
        }
    }

    @Nested
    @DisplayName("워크스페이스 삭제 성공 시나리오")
    class DeleteWorkspaceSuccessScenarios {

        @Test
        @DisplayName("워크스페이스 삭제에 성공한다")
        void deleteWorkspace_success() {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .delete("/api/workspaces/" + workspaceIdentifier)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("워크스페이스 삭제 실패 시나리오")
    class DeleteWorkspaceFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 워크스페이스 삭제 시 실패한다")
        void deleteWorkspace_fail_workspaceNotFound() {
            // given
            final String accessToken = signUpAndLogin();
            final String nonExistentIdentifier = "non-existent-identifier";

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .delete("/api/workspaces/" + nonExistentIdentifier)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.WORKSPACE_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("다른 사용자의 워크스페이스 삭제 시 실패한다")
        void deleteWorkspace_fail_workspaceModifyForbidden() {
            // given
            final String owner = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(owner);

            final String otherUser = signUpAndLogin();

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, otherUser)
                    .when()
                    .delete("/api/workspaces/" + workspaceIdentifier)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.WORKSPACE_MODIFY_FORBIDDEN.getCode()));
        }
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

        return loginResponse.tokenType() + " " + loginResponse.accessToken();
    }

    private String createWorkspace(final String accessToken) {
        final String title = WorkspaceFixture.anyTitle();
        final WorkspaceCreateRequest request = new WorkspaceCreateRequest(title);

        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(request)
                .when()
                .post("/api/workspaces")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(WorkspaceCreateResponse.class)
                .identifier();
    }
}
