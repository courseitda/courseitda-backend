package courseitda.workspace.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import courseitda.auth.ui.dto.request.LoginRequest;
import courseitda.auth.ui.dto.response.LoginResponse;
import courseitda.common.exception.ErrorCode;
import courseitda.member.domain.MemberFixture;
import courseitda.member.ui.dto.request.SignUpRequest;
import courseitda.place.domain.PlaceFixture;
import courseitda.workspace.domain.CategoryFixture;
import courseitda.workspace.domain.WorkspaceFixture;
import courseitda.workspace.ui.dto.request.CategoryCreateRequest;
import courseitda.workspace.ui.dto.request.CategoryPlaceCreateRequest;
import courseitda.workspace.ui.dto.request.CategoryUpdateRequest;
import courseitda.workspace.ui.dto.request.RepresentativeCategoryPlaceUpdateRequest;
import courseitda.workspace.ui.dto.request.WorkspaceCreateRequest;
import courseitda.workspace.ui.dto.response.CategoryCreateResponse;
import courseitda.workspace.ui.dto.response.CategoryPlaceCreateResponse;
import courseitda.workspace.ui.dto.response.CategoryReadResponse;
import courseitda.workspace.ui.dto.response.CategoryUpdateResponse;
import courseitda.workspace.ui.dto.response.RepresentativeCategoryPlaceUpdateResponse;
import courseitda.workspace.ui.dto.response.WorkspaceCreateResponse;
import courseitda.workspace.ui.dto.response.WorkspaceReadResponse;
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

    private CategoryPlaceCreateResponse createCategoryPlace(final String accessToken, final Long categoryId) {
        final String name = PlaceFixture.anyName();
        final String roadAddressName = PlaceFixture.anyRoadAddressName();
        final String addressName = PlaceFixture.anyAddressName();
        final double lat = PlaceFixture.anyLatitude();
        final double lng = PlaceFixture.anyLongitude();

        final CategoryPlaceCreateRequest request = new CategoryPlaceCreateRequest(
                name, roadAddressName, addressName, lat, lng
        );

        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(request)
                .when()
                .post("/api/categories/" + categoryId + "/places")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(CategoryPlaceCreateResponse.class);
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
            final CategoryReadResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/categories/" + category.id())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(CategoryReadResponse.class);

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

    @Nested
    @DisplayName("대표 카테고리 장소 업데이트 성공 시나리오")
    class UpdateRepresentativeCategoryPlaceSuccessScenarios {

        @Test
        @DisplayName("대표 카테고리 장소 업데이트에 성공한다")
        void updateRepresentativeCategoryPlace_success() {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);
            final Long categoryId = createCategory(accessToken, workspaceIdentifier).id();
            final Long categoryPlaceId = createCategoryPlace(accessToken, categoryId).id();

            final RepresentativeCategoryPlaceUpdateRequest request = new RepresentativeCategoryPlaceUpdateRequest(
                    categoryPlaceId
            );

            // when
            final RepresentativeCategoryPlaceUpdateResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .put("/api/categories/" + categoryId + "/representative-place")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(RepresentativeCategoryPlaceUpdateResponse.class);

            // then
            assertThat(response.id()).isEqualTo(categoryPlaceId);
        }
    }

    @Nested
    @DisplayName("대표 카테고리 장소 업데이트 실패 시나리오")
    class UpdateRepresentativeCategoryPlaceFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 카테고리의 대표 장소 업데이트 시 실패한다")
        void updateRepresentativeCategoryPlace_fail_categoryNotFound() {
            // given
            final String accessToken = signUpAndLogin();
            final Long nonExistentCategoryId = 999999L;
            final Long categoryPlaceId = 1L;

            final RepresentativeCategoryPlaceUpdateRequest request = new RepresentativeCategoryPlaceUpdateRequest(
                    categoryPlaceId
            );

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .put("/api/categories/" + nonExistentCategoryId + "/representative-place")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.CATEGORY_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("존재하지 않는 카테고리 장소를 대표로 지정 시 실패한다")
        void updateRepresentativeCategoryPlace_fail_categoryPlaceNotFound() {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);
            final Long categoryId = createCategory(accessToken, workspaceIdentifier).id();
            final Long nonExistentCategoryPlaceId = 999999L;

            final RepresentativeCategoryPlaceUpdateRequest request = new RepresentativeCategoryPlaceUpdateRequest(
                    nonExistentCategoryPlaceId
            );

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .put("/api/categories/" + categoryId + "/representative-place")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.CATEGORY_PLACE_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("다른 사용자의 카테고리 대표 장소 업데이트 시 실패한다")
        void updateRepresentativeCategoryPlace_fail_categoryModifyForbidden() {
            // given
            final String owner = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(owner);
            final Long categoryId = createCategory(owner, workspaceIdentifier).id();
            final Long categoryPlaceId = createCategoryPlace(owner, categoryId).id();

            final String otherUser = signUpAndLogin();
            final RepresentativeCategoryPlaceUpdateRequest request = new RepresentativeCategoryPlaceUpdateRequest(
                    categoryPlaceId
            );

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, otherUser)
                    .body(request)
                    .when()
                    .put("/api/categories/" + categoryId + "/representative-place")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.CATEGORY_MODIFY_FORBIDDEN.getCode()));
        }

        @Test
        @DisplayName("다른 카테고리의 장소를 대표로 지정 시 실패한다")
        void updateRepresentativeCategoryPlace_fail_invalidRepresentativePlaceAssignment() {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);
            final Long categoryId1 = createCategory(accessToken, workspaceIdentifier).id();
            final Long categoryId2 = createCategory(accessToken, workspaceIdentifier).id();
            final Long categoryPlaceId = createCategoryPlace(accessToken, categoryId1).id();

            final RepresentativeCategoryPlaceUpdateRequest request = new RepresentativeCategoryPlaceUpdateRequest(
                    categoryPlaceId
            );

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .put("/api/categories/" + categoryId2 + "/representative-place")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.INVALID_REPRESENTATIVE_PLACE_ASSIGNMENT
                            .getCode()));
        }
    }

    @Nested
    @DisplayName("대표 카테고리 장소 삭제 성공 시나리오")
    class DeleteRepresentativeCategoryPlaceSuccessScenarios {

        @Test
        @DisplayName("대표 카테고리 장소 삭제에 성공한다")
        void deleteRepresentativeCategoryPlace_success() {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);
            final Long categoryId = createCategory(accessToken, workspaceIdentifier).id();
            final Long categoryPlaceId = createCategoryPlace(accessToken, categoryId).id();

            final RepresentativeCategoryPlaceUpdateRequest updateRequest = new RepresentativeCategoryPlaceUpdateRequest(
                    categoryPlaceId
            );

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(updateRequest)
                    .when()
                    .put("/api/categories/" + categoryId + "/representative-place")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .delete("/api/categories/" + categoryId + "/representative-place")
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("대표 카테고리 장소 삭제 실패 시나리오")
    class DeleteRepresentativeCategoryPlaceFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 카테고리의 대표 장소 삭제 시 실패한다")
        void deleteRepresentativeCategoryPlace_fail_categoryNotFound() {
            // given
            final String accessToken = signUpAndLogin();
            final Long nonExistentCategoryId = 999999L;

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .delete("/api/categories/" + nonExistentCategoryId + "/representative-place")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.CATEGORY_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("다른 사용자의 카테고리 대표 장소 삭제 시 실패한다")
        void deleteRepresentativeCategoryPlace_fail_categoryModifyForbidden() {
            // given
            final String owner = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(owner);
            final Long categoryId = createCategory(owner, workspaceIdentifier).id();
            final Long categoryPlaceId = createCategoryPlace(owner, categoryId).id();

            final RepresentativeCategoryPlaceUpdateRequest updateRequest = new RepresentativeCategoryPlaceUpdateRequest(
                    categoryPlaceId
            );

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, owner)
                    .body(updateRequest)
                    .when()
                    .put("/api/categories/" + categoryId + "/representative-place")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            final String otherUser = signUpAndLogin();

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, otherUser)
                    .when()
                    .delete("/api/categories/" + categoryId + "/representative-place")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.CATEGORY_MODIFY_FORBIDDEN.getCode()));
        }
    }

    @Nested
    @DisplayName("카테고리 작업 시 워크스페이스 마지막 활동 시간 업데이트 시나리오")
    class CategoryWorkspaceLastActivityAtUpdateScenarios {

        @Test
        @DisplayName("카테고리 수정 시 워크스페이스 lastActivityAt이 업데이트된다")
        void updateCategory_updatesWorkspaceLastActivityAt() throws InterruptedException {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);
            final CategoryCreateResponse category = createCategory(accessToken, workspaceIdentifier);

            Thread.sleep(1000);

            final WorkspaceReadResponse beforeUpdate = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/workspaces/" + workspaceIdentifier)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(WorkspaceReadResponse.class);

            Thread.sleep(1000);

            final String newName = CategoryFixture.anyName();
            final String newColor = "#123456";
            final CategoryUpdateRequest request = new CategoryUpdateRequest(newName, newColor);

            // when
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .patch("/api/categories/" + category.id())
                    .then()
                    .statusCode(HttpStatus.OK.value());

            final WorkspaceReadResponse afterUpdate = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/workspaces/" + workspaceIdentifier)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(WorkspaceReadResponse.class);

            // then
            assertThat(afterUpdate.lastActivityAt()).isAfter(beforeUpdate.lastActivityAt());
        }

        @Test
        @DisplayName("카테고리 삭제 시 워크스페이스 lastActivityAt이 업데이트된다")
        void deleteCategory_updatesWorkspaceLastActivityAt() throws InterruptedException {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);
            final CategoryCreateResponse category = createCategory(accessToken, workspaceIdentifier);

            Thread.sleep(1000);

            final WorkspaceReadResponse beforeDelete = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/workspaces/" + workspaceIdentifier)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(WorkspaceReadResponse.class);

            Thread.sleep(1000);

            // when
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .delete("/api/categories/" + category.id())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            final WorkspaceReadResponse afterDelete = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/workspaces/" + workspaceIdentifier)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(WorkspaceReadResponse.class);

            // then
            assertThat(afterDelete.lastActivityAt()).isAfter(beforeDelete.lastActivityAt());
        }

        @Test
        @DisplayName("대표 카테고리 장소 업데이트 시 워크스페이스 lastActivityAt이 업데이트된다")
        void updateRepresentativeCategoryPlace_updatesWorkspaceLastActivityAt() throws InterruptedException {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);
            final Long categoryId = createCategory(accessToken, workspaceIdentifier).id();
            final Long categoryPlaceId = createCategoryPlace(accessToken, categoryId).id();

            Thread.sleep(1000);

            final WorkspaceReadResponse beforeUpdate = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/workspaces/" + workspaceIdentifier)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(WorkspaceReadResponse.class);

            Thread.sleep(1000);

            final RepresentativeCategoryPlaceUpdateRequest request = new RepresentativeCategoryPlaceUpdateRequest(
                    categoryPlaceId
            );

            // when
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .put("/api/categories/" + categoryId + "/representative-place")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            final WorkspaceReadResponse afterUpdate = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/workspaces/" + workspaceIdentifier)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(WorkspaceReadResponse.class);

            // then
            assertThat(afterUpdate.lastActivityAt()).isAfter(beforeUpdate.lastActivityAt());
        }

        @Test
        @DisplayName("대표 카테고리 장소 삭제 시 워크스페이스 lastActivityAt이 업데이트된다")
        void deleteRepresentativeCategoryPlace_updatesWorkspaceLastActivityAt() throws InterruptedException {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);
            final Long categoryId = createCategory(accessToken, workspaceIdentifier).id();
            final Long categoryPlaceId = createCategoryPlace(accessToken, categoryId).id();

            final RepresentativeCategoryPlaceUpdateRequest updateRequest = new RepresentativeCategoryPlaceUpdateRequest(
                    categoryPlaceId
            );

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(updateRequest)
                    .when()
                    .put("/api/categories/" + categoryId + "/representative-place")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            Thread.sleep(1000);

            final WorkspaceReadResponse beforeDelete = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/workspaces/" + workspaceIdentifier)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(WorkspaceReadResponse.class);

            Thread.sleep(1000);

            // when
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .delete("/api/categories/" + categoryId + "/representative-place")
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            final WorkspaceReadResponse afterDelete = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/workspaces/" + workspaceIdentifier)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(WorkspaceReadResponse.class);

            // then
            assertThat(afterDelete.lastActivityAt()).isAfter(beforeDelete.lastActivityAt());
        }
    }

}
