package courseitda.community.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import courseitda.auth.ui.dto.request.LoginRequest;
import courseitda.auth.ui.dto.response.LoginResponse;
import courseitda.common.exception.ErrorCode;
import courseitda.community.ui.dto.request.SharedCategoryCreateRequest;
import courseitda.community.ui.dto.response.SharedCategoryCreateResponse;
import courseitda.community.ui.dto.response.SharedCategoryLikeCreateResponse;
import courseitda.member.domain.MemberFixture;
import courseitda.member.ui.dto.request.SignUpRequest;
import courseitda.mystorage.domain.SavedCategoryFixture;
import courseitda.mystorage.ui.dto.request.SavedCategoryCreateRequest;
import courseitda.mystorage.ui.dto.request.SavedCategoryPlaceCreateRequest;
import courseitda.mystorage.ui.dto.request.SavedCategoryPlaceCreateRequest.SavedCategoryPlaceRequest;
import courseitda.mystorage.ui.dto.response.SavedCategoryCreateResponse;
import courseitda.place.domain.PlaceFixture;
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
class SharedCategoryLikeControllerTest {

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

    private SavedCategoryPlaceRequest anyPlaceRequest() {
        return new SavedCategoryPlaceRequest(
                PlaceFixture.anyName(),
                PlaceFixture.anyPlaceUrl(),
                PlaceFixture.anyRoadAddressName(),
                PlaceFixture.anyAddressName(),
                PlaceFixture.anyLatitude(),
                PlaceFixture.anyLongitude()
        );
    }

    private SavedCategoryCreateResponse createSavedCategory(final String accessToken) {
        final SavedCategoryCreateResponse created = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(new SavedCategoryCreateRequest(SavedCategoryFixture.anyName()))
                .when()
                .post("/api/saved-categories")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(SavedCategoryCreateResponse.class);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(new SavedCategoryPlaceCreateRequest(List.of(anyPlaceRequest())))
                .when()
                .post("/api/saved-categories/" + created.id() + "/places")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        return created;
    }

    private SharedCategoryCreateResponse createSharedCategory(final String accessToken, final Long savedCategoryId) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(new SharedCategoryCreateRequest(savedCategoryId))
                .when()
                .post("/api/shared-categories")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(SharedCategoryCreateResponse.class);
    }

    private SharedCategoryLikeCreateResponse createSharedCategoryLike(
            final String accessToken,
            final Long sharedCategoryId
    ) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .when()
                .post("/api/shared-categories/" + sharedCategoryId + "/likes")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(SharedCategoryLikeCreateResponse.class);
    }

    @Nested
    @DisplayName("공유 카테고리 찜 생성 성공 시나리오")
    class CreateSharedCategoryLikeSuccessScenarios {

        @Test
        @DisplayName("공유 카테고리 찜 생성에 성공한다")
        void createSharedCategoryLike_success() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateResponse savedCategory = createSavedCategory(accessToken);
            final SharedCategoryCreateResponse sharedCategory = createSharedCategory(accessToken, savedCategory.id());

            final String liker = signUpAndLogin();

            // when
            final SharedCategoryLikeCreateResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, liker)
                    .when()
                    .post("/api/shared-categories/" + sharedCategory.id() + "/likes")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract()
                    .as(SharedCategoryLikeCreateResponse.class);

            // then
            assertThat(response.id()).isNotNull();
        }
    }

    @Nested
    @DisplayName("공유 카테고리 찜 생성 실패 시나리오")
    class CreateSharedCategoryLikeFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 공유 카테고리에 찜 생성 시 실패한다")
        void createSharedCategoryLike_fail_sharedCategoryNotFound() {
            // given
            final String accessToken = signUpAndLogin();
            final Long nonExistentId = 999999L;

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .post("/api/shared-categories/" + nonExistentId + "/likes")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.SHARED_CATEGORY_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("이미 찜한 공유 카테고리에 중복 찜 생성 시 실패한다")
        void createSharedCategoryLike_fail_alreadyExists() {
            // given
            final String owner = signUpAndLogin();
            final SavedCategoryCreateResponse savedCategory = createSavedCategory(owner);
            final SharedCategoryCreateResponse sharedCategory = createSharedCategory(owner, savedCategory.id());

            final String liker = signUpAndLogin();
            createSharedCategoryLike(liker, sharedCategory.id());

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, liker)
                    .when()
                    .post("/api/shared-categories/" + sharedCategory.id() + "/likes")
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .body("code",
                            org.hamcrest.Matchers.equalTo(ErrorCode.SHARED_CATEGORY_LIKE_ALREADY_EXISTS.getCode()));
        }
    }

    @Nested
    @DisplayName("공유 카테고리 찜 삭제 성공 시나리오")
    class DeleteSharedCategoryLikeSuccessScenarios {

        @Test
        @DisplayName("공유 카테고리 찜 삭제에 성공한다")
        void deleteSharedCategoryLike_success() {
            // given
            final String owner = signUpAndLogin();
            final SavedCategoryCreateResponse savedCategory = createSavedCategory(owner);
            final SharedCategoryCreateResponse sharedCategory = createSharedCategory(owner, savedCategory.id());

            final String liker = signUpAndLogin();
            createSharedCategoryLike(liker, sharedCategory.id());

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, liker)
                    .when()
                    .delete("/api/shared-categories/" + sharedCategory.id() + "/likes")
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("공유 카테고리 찜 삭제 실패 시나리오")
    class DeleteSharedCategoryLikeFailureScenarios {

        @Test
        @DisplayName("찜하지 않은 공유 카테고리 찜 삭제 시 실패한다")
        void deleteSharedCategoryLike_fail_notFound() {
            // given
            final String owner = signUpAndLogin();
            final SavedCategoryCreateResponse savedCategory = createSavedCategory(owner);
            final SharedCategoryCreateResponse sharedCategory = createSharedCategory(owner, savedCategory.id());

            final String notLiker = signUpAndLogin();

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, notLiker)
                    .when()
                    .delete("/api/shared-categories/" + sharedCategory.id() + "/likes")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code",
                            org.hamcrest.Matchers.equalTo(ErrorCode.SHARED_CATEGORY_LIKE_NOT_FOUND.getCode()));
        }
    }

}
