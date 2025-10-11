package courseitda.category.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import courseitda.auth.ui.dto.request.LoginRequest;
import courseitda.auth.ui.dto.response.LoginResponse;
import courseitda.category.domain.CategoryFixture;
import courseitda.category.ui.dto.request.CategoryCreateRequest;
import courseitda.category.ui.dto.request.CategoryPlaceCreateRequest;
import courseitda.category.ui.dto.request.RepresentativeCategoryPlaceUpdateRequest;
import courseitda.category.ui.dto.response.CategoryCreateResponse;
import courseitda.category.ui.dto.response.CategoryPlaceCreateResponse;
import courseitda.category.ui.dto.response.RepresentativeCategoryPlaceUpdateResponse;
import courseitda.member.domain.MemberFixture;
import courseitda.member.ui.dto.request.SignUpRequest;
import courseitda.place.domain.PlaceFixture;
import courseitda.workspace.domain.WorkspaceFixture;
import courseitda.workspace.ui.dto.request.WorkspaceCreateRequest;
import courseitda.workspace.ui.dto.response.WorkspaceCreateResponse;
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
class RepresentativeCategoryPlaceControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("대표 카테고리 장소 업데이트에 성공한다")
    void updateRepresentativeCategoryPlace_success() {
        // given
        final String accessToken = signUpAndLogin();
        final Long workspaceId = createWorkspace(accessToken);
        final Long categoryId = createCategory(accessToken, workspaceId).id();
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

    @Test
    @DisplayName("대표 카테고리 장소 삭제에 성공한다")
    void deleteRepresentativeCategoryPlace_success() {
        // given
        final String accessToken = signUpAndLogin();
        final Long workspaceId = createWorkspace(accessToken);
        final Long categoryId = createCategory(accessToken, workspaceId).id();
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

    private Long createWorkspace(final String accessToken) {
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
                .id();
    }

    private CategoryCreateResponse createCategory(final String accessToken, final Long workspaceId) {
        final String name = CategoryFixture.anyName();
        final String color = CategoryFixture.anyColor();
        final CategoryCreateRequest request = new CategoryCreateRequest(name, color);

        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(request)
                .when()
                .post("/api/workspaces/" + workspaceId + "/categories")
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
                .post("/api/categories/" + categoryId + "/category-places")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(CategoryPlaceCreateResponse.class);
    }
}
