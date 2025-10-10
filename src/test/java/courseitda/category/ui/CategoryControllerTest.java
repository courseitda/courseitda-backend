package courseitda.category.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import courseitda.auth.ui.dto.request.LoginRequest;
import courseitda.auth.ui.dto.response.LoginResponse;
import courseitda.category.domain.CategoryFixture;
import courseitda.category.ui.dto.request.CategoryCreateRequest;
import courseitda.category.ui.dto.request.CategoryReorderRequest;
import courseitda.category.ui.dto.request.CategorySequenceRequest;
import courseitda.category.ui.dto.response.CategoryCreateResponse;
import courseitda.category.ui.dto.response.CategoryReorderResponse;
import courseitda.member.domain.MemberFixture;
import courseitda.member.ui.dto.request.SignUpRequest;
import courseitda.workspace.domain.WorkspaceFixture;
import courseitda.workspace.ui.dto.request.WorkspaceCreateRequest;
import courseitda.workspace.ui.dto.response.WorkspaceCreateResponse;
import io.restassured.RestAssured;
import java.util.List;
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
class CategoryControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("카테고리 생성에 성공한다")
    void createCategory_success() {
        // given
        final String accessToken = signUpAndLogin();
        final Long workspaceId = createWorkspace(accessToken);

        final String name = CategoryFixture.anyName();
        final String color = CategoryFixture.anyColor();
        final CategoryCreateRequest request = new CategoryCreateRequest(name, color);

        // when
        final CategoryCreateResponse response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(request)
                .when()
                .post("/api/workspaces/" + workspaceId + "/categories")
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

    @Test
    @DisplayName("카테고리 순서 변경에 성공한다")
    void updateCategorySequence_success() {
        // given
        final String accessToken = signUpAndLogin();
        final Long workspaceId = createWorkspace(accessToken);

        final CategoryCreateResponse category1 = createCategory(accessToken, workspaceId);
        final CategoryCreateResponse category2 = createCategory(accessToken, workspaceId);

        final List<CategorySequenceRequest> sequenceRequests = List.of(
                new CategorySequenceRequest(category1.id(), 2),
                new CategorySequenceRequest(category2.id(), 1)
        );
        final CategoryReorderRequest request = new CategoryReorderRequest(sequenceRequests);

        // when
        final CategoryReorderResponse response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(request)
                .when()
                .post("/api/workspaces/" + workspaceId + "/categories/reorder")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CategoryReorderResponse.class);

        // then
        assertThat(response.categorySequenceResponses()).hasSize(2);
        assertThat(response.categorySequenceResponses()).extracting("id")
                .containsExactlyInAnyOrder(category1.id(), category2.id());
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
}

