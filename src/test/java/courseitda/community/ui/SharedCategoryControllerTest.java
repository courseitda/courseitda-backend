package courseitda.community.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import courseitda.auth.ui.dto.request.LoginRequest;
import courseitda.auth.ui.dto.response.LoginResponse;
import courseitda.common.exception.ErrorCode;
import courseitda.community.domain.SharedCategoryFixture;
import courseitda.community.ui.dto.request.SharedCategoryCreateRequest;
import courseitda.community.ui.dto.response.SharedCategoriesReadResponse;
import courseitda.community.ui.dto.response.SharedCategoryCreateResponse;
import courseitda.community.ui.dto.response.SharedCategoryReadResponse;
import courseitda.community.ui.dto.response.SharedCategorySearchResponse;
import courseitda.member.domain.MemberFixture;
import courseitda.member.ui.dto.request.SignUpRequest;
import courseitda.member.ui.dto.response.MySharedCategoriesResponse;
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
class SharedCategoryControllerTest {

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
        return createSavedCategory(accessToken, SavedCategoryFixture.anyName());
    }

    private SavedCategoryCreateResponse createSavedCategory(final String accessToken, final String name) {
        final SavedCategoryCreateResponse created = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(new SavedCategoryCreateRequest(name))
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
        final SharedCategoryCreateRequest request = new SharedCategoryCreateRequest(savedCategoryId);

        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(request)
                .when()
                .post("/api/shared-categories")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(SharedCategoryCreateResponse.class);
    }

    @Nested
    @DisplayName("공유 카테고리 생성 성공 시나리오")
    class CreateSharedCategorySuccessScenarios {

        @Test
        @DisplayName("공유 카테고리 생성에 성공한다")
        void createSharedCategory_success() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateResponse savedCategory = createSavedCategory(accessToken);
            final SharedCategoryCreateRequest request = new SharedCategoryCreateRequest(savedCategory.id());

            // when
            final SharedCategoryCreateResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .post("/api/shared-categories")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract()
                    .as(SharedCategoryCreateResponse.class);

            // then
            assertThat(response.id()).isNotNull();
            assertThat(response.name()).isEqualTo(savedCategory.name());
        }
    }

    @Nested
    @DisplayName("공유 카테고리 생성 실패 시나리오")
    class CreateSharedCategoryFailureScenarios {

        @Test
        @DisplayName("savedCategoryId가 null인 경우 생성에 실패한다")
        void createSharedCategory_fail_nullSavedCategoryId() {
            // given
            final String accessToken = signUpAndLogin();
            final SharedCategoryCreateRequest request = new SharedCategoryCreateRequest(null);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .post("/api/shared-categories")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.REQUEST_VALIDATION_FAILED.getCode()));
        }

        @Test
        @DisplayName("존재하지 않는 보관 카테고리로 생성 시 실패한다")
        void createSharedCategory_fail_savedCategoryNotFound() {
            // given
            final String accessToken = signUpAndLogin();
            final SharedCategoryCreateRequest request = new SharedCategoryCreateRequest(999999L);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .post("/api/shared-categories")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.SAVED_CATEGORY_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("다른 사용자의 보관 카테고리로 생성 시 실패한다")
        void createSharedCategory_fail_forbidden() {
            // given
            final String owner = signUpAndLogin();
            final SavedCategoryCreateResponse savedCategory = createSavedCategory(owner);

            final String otherUser = signUpAndLogin();
            final SharedCategoryCreateRequest request = new SharedCategoryCreateRequest(savedCategory.id());

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, otherUser)
                    .body(request)
                    .when()
                    .post("/api/shared-categories")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.SAVED_CATEGORY_MODIFY_FORBIDDEN.getCode()));
        }
    }

    @Nested
    @DisplayName("공유 카테고리 전체 조회 성공 시나리오")
    class ReadAllSharedCategoriesSuccessScenarios {

        @Test
        @DisplayName("공유 카테고리 전체 조회에 성공한다")
        void readAllSharedCategories_success() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateResponse savedCategory = createSavedCategory(accessToken);
            createSharedCategory(accessToken, savedCategory.id());

            // when
            final SharedCategoriesReadResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/api/shared-categories")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(SharedCategoriesReadResponse.class);

            // then
            assertThat(response.sharedCategoryResponses()).isNotEmpty();
            assertThat(response.hasNext()).isNotNull();
        }
    }

    @Nested
    @DisplayName("공유 카테고리 전체 조회 실패 시나리오")
    class ReadAllSharedCategoriesFailureScenarios {

        @Test
        @DisplayName("size가 0인 경우 조회에 실패한다")
        void readAllSharedCategories_fail_sizeZero() {
            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .queryParam("size", 0)
                    .when()
                    .get("/api/shared-categories")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.INVALID_SHARED_CATEGORY_SIZE.getCode()));
        }

        @Test
        @DisplayName("size가 101인 경우 조회에 실패한다")
        void readAllSharedCategories_fail_sizeTooLarge() {
            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .queryParam("size", 101)
                    .when()
                    .get("/api/shared-categories")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.INVALID_SHARED_CATEGORY_SIZE.getCode()));
        }
    }

    @Nested
    @DisplayName("공유 카테고리 검색 성공 시나리오")
    class SearchSharedCategoriesSuccessScenarios {

        @Test
        @DisplayName("공유 카테고리 검색에 성공한다")
        void searchSharedCategories_success() {
            // given
            final String accessToken = signUpAndLogin();
            final String uniqueName = SharedCategoryFixture.anyName();
            final SavedCategoryCreateResponse savedCategory = createSavedCategory(accessToken, uniqueName);
            createSharedCategory(accessToken, savedCategory.id());

            // when
            final SharedCategorySearchResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .queryParam("keyword", uniqueName)
                    .when()
                    .get("/api/shared-categories/search")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(SharedCategorySearchResponse.class);

            // then
            assertThat(response.sharedCategoryResponses()).hasSize(1);
            assertThat(response.sharedCategoryResponses().get(0).name()).isEqualTo(uniqueName);
        }

        @Test
        @DisplayName("검색 결과가 없는 경우 빈 목록이 반환된다")
        void searchSharedCategories_success_emptyResult() {
            // when
            final SharedCategorySearchResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .queryParam("keyword", "존재하지않는키워드xyz12345")
                    .when()
                    .get("/api/shared-categories/search")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(SharedCategorySearchResponse.class);

            // then
            assertThat(response.sharedCategoryResponses()).isEmpty();
            assertThat(response.hasNext()).isFalse();
        }
    }

    @Nested
    @DisplayName("공유 카테고리 검색 실패 시나리오")
    class SearchSharedCategoriesFailureScenarios {

        @Test
        @DisplayName("검색어가 빈 값인 경우 검색에 실패한다")
        void searchSharedCategories_fail_blankKeyword() {
            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .queryParam("keyword", "   ")
                    .when()
                    .get("/api/shared-categories/search")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code",
                            org.hamcrest.Matchers.equalTo(ErrorCode.BLANK_SHARED_CATEGORY_SEARCH_KEYWORD.getCode()));
        }

        @Test
        @DisplayName("size가 0인 경우 검색에 실패한다")
        void searchSharedCategories_fail_invalidSize() {
            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .queryParam("keyword", "test")
                    .queryParam("size", 0)
                    .when()
                    .get("/api/shared-categories/search")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.INVALID_SHARED_CATEGORY_SIZE.getCode()));
        }
    }

    @Nested
    @DisplayName("공유 카테고리 단건 조회 성공 시나리오")
    class ReadSharedCategorySuccessScenarios {

        @Test
        @DisplayName("공유 카테고리 단건 조회에 성공한다")
        void readSharedCategory_success() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateResponse savedCategory = createSavedCategory(accessToken);
            final SharedCategoryCreateResponse created = createSharedCategory(accessToken, savedCategory.id());

            // when
            final SharedCategoryReadResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/api/shared-categories/" + created.id())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(SharedCategoryReadResponse.class);

            // then
            assertThat(response.id()).isEqualTo(created.id());
            assertThat(response.name()).isEqualTo(created.name());
            assertThat(response.sharedCategoryPlaceResponses()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("공유 카테고리 단건 조회 실패 시나리오")
    class ReadSharedCategoryFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 공유 카테고리 조회 시 실패한다")
        void readSharedCategory_fail_notFound() {
            // given
            final Long nonExistentId = 999999L;

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/api/shared-categories/" + nonExistentId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.SHARED_CATEGORY_NOT_FOUND.getCode()));
        }
    }

    @Nested
    @DisplayName("공유 카테고리 삭제 성공 시나리오")
    class DeleteSharedCategorySuccessScenarios {

        @Test
        @DisplayName("공유 카테고리 삭제에 성공한다")
        void deleteSharedCategory_success() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateResponse savedCategory = createSavedCategory(accessToken);
            final SharedCategoryCreateResponse created = createSharedCategory(accessToken, savedCategory.id());

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .delete("/api/shared-categories/" + created.id())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("공유 카테고리 삭제 실패 시나리오")
    class DeleteSharedCategoryFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 공유 카테고리 삭제 시 실패한다")
        void deleteSharedCategory_fail_notFound() {
            // given
            final String accessToken = signUpAndLogin();
            final Long nonExistentId = 999999L;

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .delete("/api/shared-categories/" + nonExistentId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.SHARED_CATEGORY_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("다른 사용자의 공유 카테고리 삭제 시 실패한다")
        void deleteSharedCategory_fail_forbidden() {
            // given
            final String owner = signUpAndLogin();
            final SavedCategoryCreateResponse savedCategory = createSavedCategory(owner);
            final SharedCategoryCreateResponse created = createSharedCategory(owner, savedCategory.id());

            final String otherUser = signUpAndLogin();

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, otherUser)
                    .when()
                    .delete("/api/shared-categories/" + created.id())
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.SHARED_CATEGORY_MODIFY_FORBIDDEN.getCode()));
        }
    }

    @Nested
    @DisplayName("내 공유 카테고리 목록 조회 성공 시나리오")
    class ReadMySharedCategoriesSuccessScenarios {

        @Test
        @DisplayName("내 공유 카테고리 목록 조회에 성공한다")
        void readMySharedCategories_success() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateResponse savedCategory1 = createSavedCategory(accessToken);
            final SavedCategoryCreateResponse savedCategory2 = createSavedCategory(accessToken);
            createSharedCategory(accessToken, savedCategory1.id());
            createSharedCategory(accessToken, savedCategory2.id());

            // when
            final MySharedCategoriesResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/me/shared-categories")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(MySharedCategoriesResponse.class);

            // then
            assertThat(response.sharedCategoryResponses()).hasSize(2);
        }

        @Test
        @DisplayName("공유한 카테고리가 없는 경우 빈 목록이 반환된다")
        void readMySharedCategories_success_emptyList() {
            // given
            final String accessToken = signUpAndLogin();

            // when
            final MySharedCategoriesResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/me/shared-categories")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(MySharedCategoriesResponse.class);

            // then
            assertThat(response.sharedCategoryResponses()).isEmpty();
        }
    }
}
