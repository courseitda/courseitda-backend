package courseitda.member.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import courseitda.auth.ui.dto.request.LoginRequest;
import courseitda.auth.ui.dto.response.LoginResponse;
import courseitda.member.domain.MemberFixture;
import courseitda.member.ui.dto.request.SignUpRequest;
import courseitda.member.ui.dto.response.MemberReadDropdownResponse;
import courseitda.member.ui.dto.response.MemberReadNavigatorResponse;
import courseitda.member.ui.dto.response.MemberReadProfileResponse;
import courseitda.workspace.ui.dto.request.WorkspaceCreateRequest;
import courseitda.workspace.ui.dto.response.WorkspacesResponse;
import courseitda.workspace.ui.dto.response.WorkspacesResponse.WorkspaceResponse;
import io.restassured.RestAssured;
import jakarta.transaction.Transactional;
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

@Transactional
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MeControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("회원 정보(네비게이터 뷰) 조회에 성공한다")
    void readMemberNavigator_success() {
        // given
        final String nickname = MemberFixture.anyNickname();
        final String email = MemberFixture.anyEmail();
        final String password = MemberFixture.anyPassword();
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

        final String accessToken = loginResponse.tokenType() + " " + loginResponse.accessToken();

        // when
        final MemberReadNavigatorResponse response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .when()
                .get("/api/me/navigator")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(MemberReadNavigatorResponse.class);

        // then
        assertThat(response.nickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("회원 정보(드롭다운 뷰) 조회에 성공한다")
    void readMemberDropdown_success() {
        // given
        final String nickname = MemberFixture.anyNickname();
        final String email = MemberFixture.anyEmail();
        final String password = MemberFixture.anyPassword();
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

        final String accessToken = loginResponse.tokenType() + " " + loginResponse.accessToken();

        // when
        final MemberReadDropdownResponse response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .when()
                .get("/api/me/dropdown")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(MemberReadDropdownResponse.class);

        // then
        assertThat(response.nickname()).isEqualTo(nickname);
        assertThat(response.email()).isEqualTo(email);
    }

    @Test
    @DisplayName("회원 정보(프로필) 조회에 성공한다")
    void readMemberProfile_success() {
        // given
        final String nickname = MemberFixture.anyNickname();
        final String email = MemberFixture.anyEmail();
        final String password = MemberFixture.anyPassword();
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

        final String accessToken = loginResponse.tokenType() + " " + loginResponse.accessToken();

        // when
        final MemberReadProfileResponse response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .when()
                .get("/api/me/profile")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(MemberReadProfileResponse.class);

        // then
        assertThat(response.nickName()).isEqualTo(nickname);
        assertThat(response.email()).isEqualTo(email);
    }

    @Test
    @DisplayName("내 워크스페이스 목록 조회에 성공한다")
    void readMyWorkspaces_success() {
        // given
        final String nickname = MemberFixture.anyNickname();
        final String email = MemberFixture.anyEmail();
        final String password = MemberFixture.anyPassword();
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

        final String accessToken = loginResponse.tokenType() + " " + loginResponse.accessToken();

        // 워크스페이스 3개 생성
        final List<WorkspaceCreateRequest> workspaceCreateRequests = List.of(
                new WorkspaceCreateRequest("워크스페이스1"),
                new WorkspaceCreateRequest("워크스페이스2"),
                new WorkspaceCreateRequest("워크스페이스3")
        );

        workspaceCreateRequests.forEach(workspaceCreateRequest -> {
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(workspaceCreateRequest)
                    .when()
                    .post("/api/workspaces")
                    .then()
                    .statusCode(HttpStatus.CREATED.value());
        });

        // when
        final WorkspacesResponse response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .when()
                .get("/api/me/workspaces")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(WorkspacesResponse.class);

        // then
        assertThat(response.workspaces()).hasSize(3);
        assertThat(
                response.workspaces()
                        .stream()
                        .map(WorkspaceResponse::title)
                        .toList()
        ).containsExactlyInAnyOrder("워크스페이스1", "워크스페이스2", "워크스페이스3");
    }
}
