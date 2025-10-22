package courseitda.workspace.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

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
import courseitda.workspace.ui.dto.request.WorkspaceCreateRequest;
import courseitda.workspace.ui.dto.response.CategoryCreateResponse;
import courseitda.workspace.ui.dto.response.CategoryPlaceCreateResponse;
import courseitda.workspace.ui.dto.response.CategoryPlacesResponse;
import courseitda.workspace.ui.dto.response.WorkspaceCreateResponse;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CategoryPlaceControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    @DisplayName("카테고리 장소 생성 성공 시나리오")
    class CreateCategoryPlaceSuccessScenarios {

        @Test
        @DisplayName("카테고리 장소 생성에 성공한다")
        void createCategoryPlace_success() {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);
            final Long categoryId = createCategory(accessToken, workspaceIdentifier).id();

            final String name = PlaceFixture.anyName();
            final String roadAddressName = PlaceFixture.anyRoadAddressName();
            final String addressName = PlaceFixture.anyAddressName();
            final double lat = PlaceFixture.anyLatitude();
            final double lng = PlaceFixture.anyLongitude();

            final CategoryPlaceCreateRequest request = new CategoryPlaceCreateRequest(
                    name, roadAddressName, addressName, lat, lng
            );

            // when
            final CategoryPlaceCreateResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .post("/api/categories/" + categoryId + "/places")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract()
                    .as(CategoryPlaceCreateResponse.class);

            // then
            assertThat(response.id()).isNotNull();
            assertThat(response.placeId()).isNotNull();
        }
    }

    @Nested
    @DisplayName("카테고리 장소 생성 실패 시나리오")
    class CreateCategoryPlaceFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 카테고리에 장소 생성 시 실패한다")
        void createCategoryPlace_fail_categoryNotFound() {
            // given
            final String accessToken = signUpAndLogin();
            final Long nonExistentCategoryId = 999999L;

            final CategoryPlaceCreateRequest request = new CategoryPlaceCreateRequest(
                    PlaceFixture.anyName(), PlaceFixture.anyRoadAddressName(),
                    PlaceFixture.anyAddressName(), PlaceFixture.anyLatitude(), PlaceFixture.anyLongitude()
            );

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .post("/api/categories/" + nonExistentCategoryId + "/places")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.CATEGORY_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("다른 사용자의 카테고리에 장소 생성 시 실패한다")
        void createCategoryPlace_fail_categoryModifyForbidden() {
            // given
            final String owner = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(owner);
            final Long categoryId = createCategory(owner, workspaceIdentifier).id();

            final String otherUser = signUpAndLogin();
            final CategoryPlaceCreateRequest request = new CategoryPlaceCreateRequest(
                    PlaceFixture.anyName(), PlaceFixture.anyRoadAddressName(),
                    PlaceFixture.anyAddressName(), PlaceFixture.anyLatitude(), PlaceFixture.anyLongitude()
            );

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, otherUser)
                    .body(request)
                    .when()
                    .post("/api/categories/" + categoryId + "/places")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.CATEGORY_MODIFY_FORBIDDEN.getCode()));
        }
    }

    @Nested
    @DisplayName("카테고리 장소 목록 조회 성공 시나리오")
    class ReadCategoryPlacesSuccessScenarios {

        @Test
        @DisplayName("카테고리 장소 목록 조회에 성공한다")
        void readCategoryPlaces_success() {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);
            final Long categoryId = createCategory(accessToken, workspaceIdentifier).id();

            createCategoryPlace(accessToken, categoryId);
            createCategoryPlace(accessToken, categoryId);

            // when
            final CategoryPlacesResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/categories/" + categoryId + "/places")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(CategoryPlacesResponse.class);

            // then
            assertThat(response.categoryPlaceResponses()).hasSize(2);
        }

        @Test
        @DisplayName("카테고리 장소가 없는 경우 빈 목록이 반환된다")
        void readCategoryPlaces_success_emptyList() {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);
            final Long categoryId = createCategory(accessToken, workspaceIdentifier).id();

            // when
            final CategoryPlacesResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/categories/" + categoryId + "/places")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(CategoryPlacesResponse.class);

            // then
            assertThat(response.categoryPlaceResponses()).isEmpty();
        }
    }

    @Nested
    @DisplayName("카테고리 장소 목록 조회 실패 시나리오")
    class ReadCategoryPlacesFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 카테고리의 장소 목록 조회 시 실패한다")
        void readCategoryPlaces_fail_categoryNotFound() {
            // given
            final String accessToken = signUpAndLogin();
            final Long nonExistentCategoryId = 999999L;

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/categories/" + nonExistentCategoryId + "/places")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.CATEGORY_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("다른 사용자의 카테고리 장소 목록 조회 시 실패한다")
        void readCategoryPlaces_fail_categoryModifyForbidden() {
            // given
            final String owner = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(owner);
            final Long categoryId = createCategory(owner, workspaceIdentifier).id();

            final String otherUser = signUpAndLogin();

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, otherUser)
                    .when()
                    .get("/api/categories/" + categoryId + "/places")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.CATEGORY_MODIFY_FORBIDDEN.getCode()));
        }
    }

    @Nested
    @DisplayName("카테고리 장소 삭제 성공 시나리오")
    class DeleteCategoryPlaceSuccessScenarios {

        @Test
        @DisplayName("카테고리 장소 삭제에 성공한다")
        void deleteCategoryPlace_success() {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);
            final Long categoryId = createCategory(accessToken, workspaceIdentifier).id();
            final Long categoryPlaceId = createCategoryPlace(accessToken, categoryId).id();

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .delete("/api/categories/" + categoryId + "/places/" + categoryPlaceId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("카테고리 장소 삭제 실패 시나리오")
    class DeleteCategoryPlaceFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 카테고리 장소 삭제 시 실패한다")
        void deleteCategoryPlace_fail_categoryPlaceNotFound() {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);
            final Long categoryId = createCategory(accessToken, workspaceIdentifier).id();
            final Long nonExistentCategoryPlaceId = 999999L;

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .delete("/api/categories/" + categoryId + "/places/" + nonExistentCategoryPlaceId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.CATEGORY_PLACE_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("다른 사용자의 카테고리 장소 삭제 시 실패한다")
        void deleteCategoryPlace_fail_categoryModifyForbidden() {
            // given
            final String owner = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(owner);
            final Long categoryId = createCategory(owner, workspaceIdentifier).id();
            final Long categoryPlaceId = createCategoryPlace(owner, categoryId).id();

            final String otherUser = signUpAndLogin();

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, otherUser)
                    .when()
                    .delete("/api/categories/" + categoryId + "/places/" + categoryPlaceId)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.CATEGORY_MODIFY_FORBIDDEN.getCode()));
        }

        @Test
        @DisplayName("다른 카테고리의 장소 삭제 시 실패한다")
        void deleteCategoryPlace_fail_placeNotBelongToCategory() {
            // given
            final String accessToken = signUpAndLogin();
            final String workspaceIdentifier = createWorkspace(accessToken);
            final Long categoryId1 = createCategory(accessToken, workspaceIdentifier).id();
            final Long categoryId2 = createCategory(accessToken, workspaceIdentifier).id();
            final Long categoryPlaceId = createCategoryPlace(accessToken, categoryId1).id();

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .delete("/api/categories/" + categoryId2 + "/places/" + categoryPlaceId)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.PLACE_NOT_BELONG_TO_CATEGORY.getCode()));
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
}
