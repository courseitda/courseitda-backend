package courseitda.workspace.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import courseitda.auth.ui.dto.request.LoginRequest;
import courseitda.auth.ui.dto.response.LoginResponse;
import courseitda.common.exception.ErrorCode;
import courseitda.member.domain.MemberFixture;
import courseitda.member.ui.dto.request.SignUpRequest;
import courseitda.workspace.domain.CategoryFixture;
import courseitda.workspace.domain.WorkspaceFixture;
import courseitda.workspace.ui.dto.request.CategoryCreateRequest;
import courseitda.workspace.ui.dto.request.CategoryReorderRequest;
import courseitda.workspace.ui.dto.request.CategoryReorderRequest.CategorySequenceRequest;
import courseitda.workspace.ui.dto.request.WorkspaceCreateRequest;
import courseitda.workspace.ui.dto.request.WorkspaceUpdateRequest;
import courseitda.workspace.ui.dto.response.CategoriesFindResponse;
import courseitda.workspace.ui.dto.response.CategoryCreateResponse;
import courseitda.workspace.ui.dto.response.CategorySequenceUpdateResponse;
import courseitda.workspace.ui.dto.response.CheckTitleDuplicateResponse;
import courseitda.workspace.ui.dto.response.WorkspaceCreateResponse;
import courseitda.workspace.ui.dto.response.WorkspaceFindResponse;
import courseitda.workspace.ui.dto.response.WorkspaceUpdateResponse;
import io.restassured.RestAssured;
import java.util.List;
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

    private CategoryCreateResponse createCategory(final String accessToken, final String workspaceIdentifier) {
        final String name = CategoryFixture.anyName();
        final String color = CategoryFixture.anyColor();
        final CategoryCreateRequest request = new CategoryCreateRequest(name, color);

        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(request)
                .when()
                .post("/api/workspaces/" + workspaceIdentifier + "/categories")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(CategoryCreateResponse.class);
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
    @DisplayName("워크스페이스 조회 성공 시나리오")
    class ReadWorkspaceSuccessScenarios {

        @Test
        @DisplayName("워크스페이스 조회에 성공한다")
        void readWorkspace_success() {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);

            // when
            final WorkspaceFindResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/workspaces/" + workspaceIdentifier)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(WorkspaceFindResponse.class);

            // then
            assertThat(response.identifier()).isEqualTo(workspaceIdentifier);
            assertThat(response.title()).isNotNull();
        }
    }

    @Nested
    @DisplayName("워크스페이스 조회 실패 시나리오")
    class ReadWorkspaceFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 워크스페이스 조회 시 실패한다")
        void readWorkspace_fail_workspaceNotFound() {
            // given
            final String accessToken = signUpAndLogin();
            final String nonExistentIdentifier = "non-existent-identifier";

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/workspaces/" + nonExistentIdentifier)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.WORKSPACE_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("다른 사용자의 워크스페이스 조회 시 실패한다")
        void readWorkspace_fail_workspaceModifyForbidden() {
            // given
            final String owner = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(owner);

            final String otherUser = signUpAndLogin();

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, otherUser)
                    .when()
                    .get("/api/workspaces/" + workspaceIdentifier)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.WORKSPACE_MODIFY_FORBIDDEN.getCode()));
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

    @Nested
    @DisplayName("카테고리 생성 성공 시나리오")
    class CreateCategorySuccessScenarios {

        @Test
        @DisplayName("카테고리 생성에 성공한다")
        void createCategory_success() {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);

            final String name = CategoryFixture.anyName();
            final String color = CategoryFixture.anyColor();
            final CategoryCreateRequest request = new CategoryCreateRequest(name, color);

            // when
            final CategoryCreateResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .post("/api/workspaces/" + workspaceIdentifier + "/categories")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract()
                    .as(CategoryCreateResponse.class);

            // then
            assertThat(response.id()).isNotNull();
            assertThat(response.name()).isEqualTo(name);
            assertThat(response.color()).isEqualTo(color);
            assertThat(response.sequence()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("카테고리 생성 실패 시나리오")
    class CreateCategoryFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 워크스페이스에 카테고리 생성 시 실패한다")
        void createCategory_fail_workspaceNotFound() {
            // given
            final String accessToken = signUpAndLogin();
            final String nonExistentIdentifier = "non-existent-identifier";

            final String name = CategoryFixture.anyName();
            final String color = CategoryFixture.anyColor();
            final CategoryCreateRequest request = new CategoryCreateRequest(name, color);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .post("/api/workspaces/" + nonExistentIdentifier + "/categories")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.WORKSPACE_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("다른 사용자의 워크스페이스에 카테고리 생성 시 실패한다")
        void createCategory_fail_workspaceModifyForbidden() {
            // given
            final String owner = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(owner);

            final String otherUser = signUpAndLogin();
            final String name = CategoryFixture.anyName();
            final String color = CategoryFixture.anyColor();
            final CategoryCreateRequest request = new CategoryCreateRequest(name, color);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, otherUser)
                    .body(request)
                    .when()
                    .post("/api/workspaces/" + workspaceIdentifier + "/categories")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.WORKSPACE_MODIFY_FORBIDDEN.getCode()));
        }
    }

    @Nested
    @DisplayName("카테고리 순서 변경 성공 시나리오")
    class UpdateCategorySequenceSuccessScenarios {

        @Test
        @DisplayName("카테고리 순서 변경에 성공한다")
        void updateCategorySequence_success() {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);

            final CategoryCreateResponse category1 = createCategory(accessToken, workspaceIdentifier);
            final CategoryCreateResponse category2 = createCategory(accessToken, workspaceIdentifier);

            final List<CategorySequenceRequest> sequenceRequests = List.of(
                    new CategorySequenceRequest(category1.id(), 2),
                    new CategorySequenceRequest(category2.id(), 1)
            );
            final CategoryReorderRequest request = new CategoryReorderRequest(sequenceRequests);

            // when
            final CategorySequenceUpdateResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .post("/api/workspaces/" + workspaceIdentifier + "/categories/sequence")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(CategorySequenceUpdateResponse.class);

            // then
            assertThat(response.categorySequenceResponses()).hasSize(2);
            assertThat(response.categorySequenceResponses()).extracting("id")
                    .containsExactlyInAnyOrder(category1.id(), category2.id());
        }
    }

    @Nested
    @DisplayName("카테고리 순서 변경 실패 시나리오")
    class UpdateCategorySequenceFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 워크스페이스의 카테고리 순서 변경 시 실패한다")
        void updateCategorySequence_fail_workspaceNotFound() {
            // given
            final String accessToken = signUpAndLogin();
            final String nonExistentIdentifier = "non-existent-identifier";

            final List<CategorySequenceRequest> sequenceRequests = List.of(
                    new CategorySequenceRequest(1L, 1)
            );
            final CategoryReorderRequest request = new CategoryReorderRequest(sequenceRequests);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .post("/api/workspaces/" + nonExistentIdentifier + "/categories/sequence")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.WORKSPACE_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("다른 사용자의 워크스페이스 카테고리 순서 변경 시 실패한다")
        void updateCategorySequence_fail_workspaceModifyForbidden() {
            // given
            final String owner = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(owner);
            final CategoryCreateResponse category1 = createCategory(owner, workspaceIdentifier);

            final String otherUser = signUpAndLogin();
            final List<CategorySequenceRequest> sequenceRequests = List.of(
                    new CategorySequenceRequest(category1.id(), 1)
            );
            final CategoryReorderRequest request = new CategoryReorderRequest(sequenceRequests);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, otherUser)
                    .body(request)
                    .when()
                    .post("/api/workspaces/" + workspaceIdentifier + "/categories/sequence")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.WORKSPACE_MODIFY_FORBIDDEN.getCode()));
        }
    }

    @Nested
    @DisplayName("카테고리 목록 조회 성공 시나리오")
    class ReadAllCategoriesSuccessScenarios {

        @Test
        @DisplayName("카테고리 목록 조회에 성공한다")
        void readAllCategories_success() {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);

            createCategory(accessToken, workspaceIdentifier);
            createCategory(accessToken, workspaceIdentifier);
            createCategory(accessToken, workspaceIdentifier);

            // when
            final CategoriesFindResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/workspaces/" + workspaceIdentifier + "/categories")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(CategoriesFindResponse.class);

            // then
            assertThat(response.categoryResponses()).hasSize(3);
        }

        @Test
        @DisplayName("카테고리가 없는 경우 빈 목록이 반환된다")
        void readAllCategories_success_emptyList() {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);

            // when
            final CategoriesFindResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/workspaces/" + workspaceIdentifier + "/categories")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(CategoriesFindResponse.class);

            // then
            assertThat(response.categoryResponses()).isEmpty();
        }
    }

    @Nested
    @DisplayName("카테고리 목록 조회 실패 시나리오")
    class ReadAllCategoriesFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 워크스페이스의 카테고리 목록 조회 시 실패한다")
        void readAllCategories_fail_workspaceNotFound() {
            // given
            final String accessToken = signUpAndLogin();
            final String nonExistentIdentifier = "non-existent-identifier";

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/workspaces/" + nonExistentIdentifier + "/categories")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.WORKSPACE_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("다른 사용자의 워크스페이스 카테고리 목록 조회 시 실패한다")
        void readAllCategories_fail_workspaceModifyForbidden() {
            // given
            final String owner = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(owner);

            final String otherUser = signUpAndLogin();

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, otherUser)
                    .when()
                    .get("/api/workspaces/" + workspaceIdentifier + "/categories")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.WORKSPACE_MODIFY_FORBIDDEN.getCode()));
        }
    }

    @Nested
    @DisplayName("워크스페이스 제목 중복 검증 성공 시나리오")
    class CheckTitleDuplicateSuccessScenarios {

        @Test
        @DisplayName("중복되지 않은 제목을 검증한다")
        void checkTitleDuplicate_success_notDuplicated() {
            // given
            final String accessToken = signUpAndLogin();
            final String title = WorkspaceFixture.anyTitle();

            // when
            final CheckTitleDuplicateResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .queryParam("value", title)
                    .when()
                    .get("/api/workspaces/validations/title")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(CheckTitleDuplicateResponse.class);

            // then
            assertThat(response.isDuplicated()).isFalse();
        }

        @Test
        @DisplayName("중복된 제목을 검증한다")
        void checkTitleDuplicate_success_duplicated() {
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

            // when
            final CheckTitleDuplicateResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .queryParam("value", title)
                    .when()
                    .get("/api/workspaces/validations/title")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(CheckTitleDuplicateResponse.class);

            // then
            assertThat(response.isDuplicated()).isTrue();
        }

        @Test
        @DisplayName("다른 사용자의 동일한 제목은 중복이 아니다")
        void checkTitleDuplicate_success_notDuplicatedForDifferentUser() {
            // given
            final String user1 = signUpAndLogin();
            final String title = WorkspaceFixture.anyTitle();
            final WorkspaceCreateRequest request = new WorkspaceCreateRequest(title);

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, user1)
                    .body(request)
                    .when()
                    .post("/api/workspaces")
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            final String user2 = signUpAndLogin();

            // when
            final CheckTitleDuplicateResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, user2)
                    .queryParam("value", title)
                    .when()
                    .get("/api/workspaces/validations/title")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(CheckTitleDuplicateResponse.class);

            // then
            assertThat(response.isDuplicated()).isFalse();
        }
    }

    @Nested
    @DisplayName("워크스페이스 제목 중복 검증 실패 시나리오")
    class CheckTitleDuplicateFailureScenarios {

        @Test
        @DisplayName("제목이 null인 경우 검증에 실패한다")
        void checkTitleDuplicate_fail_nullTitle() {
            // given
            final String accessToken = signUpAndLogin();

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/workspaces/validations/title")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.WORKSPACE_TITLE_EMPTY.getCode()));
        }

        @Test
        @DisplayName("제목이 빈 문자열인 경우 검증에 실패한다")
        void checkTitleDuplicate_fail_emptyTitle() {
            // given
            final String accessToken = signUpAndLogin();
            final String emptyTitle = "";

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .queryParam("value", emptyTitle)
                    .when()
                    .get("/api/workspaces/validations/title")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.WORKSPACE_TITLE_EMPTY.getCode()));
        }
    }
}
