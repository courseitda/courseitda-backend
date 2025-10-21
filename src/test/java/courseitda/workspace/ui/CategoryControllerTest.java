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
import courseitda.workspace.ui.dto.request.CategoryUpdateRequest;
import courseitda.workspace.ui.dto.request.WorkspaceCreateRequest;
import courseitda.workspace.ui.dto.response.CategoriesResponse;
import courseitda.workspace.ui.dto.response.CategoryCreateResponse;
import courseitda.workspace.ui.dto.response.CategoryReorderResponse;
import courseitda.workspace.ui.dto.response.CategoryResponse;
import courseitda.workspace.ui.dto.response.CategoryUpdateResponse;
import courseitda.workspace.ui.dto.response.WorkspaceCreateResponse;
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
class CategoryControllerTest {

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
    @DisplayName("카테고리 수정 성공 시나리오")
    class UpdateCategorySuccessScenarios {

        @Test
        @DisplayName("카테고리 수정에 성공한다")
        void updateCategory_success() {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);
            final CategoryCreateResponse category = createCategory(accessToken, workspaceIdentifier);

            final String newName = CategoryFixture.anyName();
            final String newColor = "#123456";
            final CategoryUpdateRequest request = new CategoryUpdateRequest(newName, newColor);

            // when
            final CategoryUpdateResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .patch("/api/categories/" + category.id())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(CategoryUpdateResponse.class);

            // then
            assertThat(response.id()).isEqualTo(category.id());
            assertThat(response.name()).isEqualTo(newName);
            assertThat(response.color()).isEqualTo(newColor);
        }
    }

    @Nested
    @DisplayName("카테고리 수정 실패 시나리오")
    class UpdateCategoryFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 카테고리 수정 시 실패한다")
        void updateCategory_fail_categoryNotFound() {
            // given
            final String accessToken = signUpAndLogin();
            final Long nonExistentCategoryId = 999999L;

            final String newName = CategoryFixture.anyName();
            final String newColor = "#123456";
            final CategoryUpdateRequest request = new CategoryUpdateRequest(newName, newColor);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .patch("/api/categories/" + nonExistentCategoryId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.CATEGORY_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("다른 사용자의 카테고리 수정 시 실패한다")
        void updateCategory_fail_categoryModifyForbidden() {
            // given
            final String owner = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(owner);
            final CategoryCreateResponse category = createCategory(owner, workspaceIdentifier);

            final String otherUser = signUpAndLogin();
            final String newName = CategoryFixture.anyName();
            final String newColor = "#123456";
            final CategoryUpdateRequest request = new CategoryUpdateRequest(newName, newColor);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, otherUser)
                    .body(request)
                    .when()
                    .patch("/api/categories/" + category.id())
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.CATEGORY_MODIFY_FORBIDDEN.getCode()));
        }

    }

    @Nested
    @DisplayName("카테고리 삭제 성공 시나리오")
    class DeleteCategorySuccessScenarios {

        @Test
        @DisplayName("카테고리 삭제에 성공한다")
        void deleteCategory_success() {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);
            final CategoryCreateResponse category = createCategory(accessToken, workspaceIdentifier);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .delete("/api/categories/" + category.id())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("카테고리 삭제 실패 시나리오")
    class DeleteCategoryFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 카테고리 삭제 시 실패한다")
        void deleteCategory_fail_categoryNotFound() {
            // given
            final String accessToken = signUpAndLogin();
            final Long nonExistentCategoryId = 999999L;

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .delete("/api/categories/" + nonExistentCategoryId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.CATEGORY_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("다른 사용자의 카테고리 삭제 시 실패한다")
        void deleteCategory_fail_categoryModifyForbidden() {
            // given
            final String owner = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(owner);
            final CategoryCreateResponse category = createCategory(owner, workspaceIdentifier);

            final String otherUser = signUpAndLogin();

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, otherUser)
                    .when()
                    .delete("/api/categories/" + category.id())
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.CATEGORY_MODIFY_FORBIDDEN.getCode()));
        }

    }

    @Nested
    @DisplayName("카테고리 단건 조회 성공 시나리오")
    class ReadCategorySuccessScenarios {

        @Test
        @DisplayName("카테고리 단건 조회에 성공한다")
        void readCategory_success() {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);
            final CategoryCreateResponse category = createCategory(accessToken, workspaceIdentifier);

            // when
            final CategoryResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/categories/" + category.id())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(CategoryResponse.class);

            // then
            assertThat(response.id()).isEqualTo(category.id());
            assertThat(response.name()).isEqualTo(category.name());
            assertThat(response.color()).isEqualTo(category.color());
            assertThat(response.sequence()).isEqualTo(category.sequence());
        }
    }

    @Nested
    @DisplayName("카테고리 단건 조회 실패 시나리오")
    class ReadCategoryFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 카테고리 단건 조회 시 실패한다")
        void readCategory_fail_categoryNotFound() {
            // given
            final String accessToken = signUpAndLogin();
            final Long nonExistentCategoryId = 999999L;

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/categories/" + nonExistentCategoryId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.CATEGORY_NOT_FOUND.getCode()));
        }

    }

}
