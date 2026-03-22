package courseitda.member.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import courseitda.auth.ui.dto.request.LoginRequest;
import courseitda.auth.ui.dto.response.LoginResponse;
import courseitda.member.domain.MemberFixture;
import courseitda.member.ui.dto.request.SignUpRequest;
import courseitda.member.ui.dto.response.MemberDropdownResponse;
import courseitda.member.ui.dto.response.MemberNavigatorResponse;
import courseitda.member.ui.dto.response.MemberProfileResponse;
import courseitda.member.ui.dto.response.MySavedCategoriesResponse;
import courseitda.member.ui.dto.response.MyWorkspacesResponse;
import courseitda.mystorage.domain.SavedCategoryFixture;
import courseitda.mystorage.ui.dto.request.SavedCategoryCreateRequest;
import courseitda.mystorage.ui.dto.response.SavedCategoryCreateResponse;
import courseitda.workspace.domain.WorkspaceFixture;
import courseitda.workspace.ui.dto.request.WorkspaceCreateRequest;
import courseitda.workspace.ui.dto.response.WorkspaceCreateResponse;
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
class MeControllerTest {

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

    private SavedCategoryCreateResponse createSavedCategory(final String accessToken) {
        final SavedCategoryCreateRequest request = new SavedCategoryCreateRequest(SavedCategoryFixture.anyName());

        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(request)
                .when()
                .post("/api/saved-categories")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(SavedCategoryCreateResponse.class);
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

    @Nested
    @DisplayName("내 보관 카테고리 목록 조회 성공 시나리오")
    class ReadMySavedCategoriesSuccessScenarios {

        @Test
        @DisplayName("내 보관 카테고리 목록 조회에 성공한다")
        void readMySavedCategories_success() {
            // given
            final String accessToken = signUpAndLogin();
            createSavedCategory(accessToken);
            createSavedCategory(accessToken);

            // when
            final MySavedCategoriesResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/me/saved-categories")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(MySavedCategoriesResponse.class);

            // then
            assertThat(response.savedCategoryResponses()).hasSize(2);
        }

        @Test
        @DisplayName("보관 카테고리가 없는 경우 빈 목록이 반환된다")
        void readMySavedCategories_success_emptyList() {
            // given
            final String accessToken = signUpAndLogin();

            // when
            final MySavedCategoriesResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/me/saved-categories")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(MySavedCategoriesResponse.class);

            // then
            assertThat(response.savedCategoryResponses()).isEmpty();
        }
    }

    @Nested
    @DisplayName("내 네비게이터 정보 조회 성공 시나리오")
    class ReadMemberNavigatorSuccessScenarios {

        @Test
        @DisplayName("내 네비게이터 정보 조회에 성공한다")
        void readMemberNavigator_success() {
            // given
            final String accessToken = signUpAndLogin();

            // when
            final MemberNavigatorResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/me/navigator")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(MemberNavigatorResponse.class);

            // then
            assertThat(response.nickname()).isNotNull();
        }
    }

    @Nested
    @DisplayName("내 드롭다운 정보 조회 성공 시나리오")
    class ReadMemberDropdownSuccessScenarios {

        @Test
        @DisplayName("내 드롭다운 정보 조회에 성공한다")
        void readMemberDropdown_success() {
            // given
            final String accessToken = signUpAndLogin();

            // when
            final MemberDropdownResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/me/dropdown")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(MemberDropdownResponse.class);

            // then
            assertThat(response.nickname()).isNotNull();
            assertThat(response.email()).isNotNull();
        }
    }

    @Nested
    @DisplayName("내 프로필 정보 조회 성공 시나리오")
    class ReadMemberProfileSuccessScenarios {

        @Test
        @DisplayName("내 프로필 정보 조회에 성공한다")
        void readMemberProfile_success() {
            // given
            final String accessToken = signUpAndLogin();

            // when
            final MemberProfileResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/me/profile")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(MemberProfileResponse.class);

            // then
            assertThat(response.nickname()).isNotNull();
            assertThat(response.email()).isNotNull();
        }
    }

    @Nested
    @DisplayName("내 워크스페이스 목록 조회 성공 시나리오")
    class ReadMyWorkspacesSuccessScenarios {

        @Test
        @DisplayName("내 워크스페이스 목록 조회에 성공한다")
        void readMyWorkspaces_success() {
            // given
            final String accessToken = signUpAndLogin();
            createWorkspace(accessToken);
            createWorkspace(accessToken);

            // when
            final MyWorkspacesResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/me/workspaces")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(MyWorkspacesResponse.class);

            // then
            assertThat(response.workspaces()).hasSize(2);
        }

        @Test
        @DisplayName("워크스페이스가 없는 경우 빈 목록이 반환된다")
        void readMyWorkspaces_success_emptyList() {
            // given
            final String accessToken = signUpAndLogin();

            // when
            final MyWorkspacesResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/me/workspaces")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(MyWorkspacesResponse.class);

            // then
            assertThat(response.workspaces()).isEmpty();
        }
    }
}
