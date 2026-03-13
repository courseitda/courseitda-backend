package courseitda.mystorage.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import courseitda.auth.ui.dto.request.LoginRequest;
import courseitda.auth.ui.dto.response.LoginResponse;
import courseitda.common.exception.ErrorCode;
import courseitda.member.domain.MemberFixture;
import courseitda.member.ui.dto.request.SignUpRequest;
import courseitda.mystorage.domain.SavedCategoryFixture;
import courseitda.mystorage.ui.dto.request.SavedCategoryCreateRequest;
import courseitda.mystorage.ui.dto.request.SavedCategoryPlaceCreateRequest;
import courseitda.mystorage.ui.dto.request.SavedCategoryPlaceCreateRequest.SavedCategoryPlaceRequest;
import courseitda.mystorage.ui.dto.request.SavedCategoryPlaceUpdateRequest;
import courseitda.mystorage.ui.dto.response.SavedCategoryCreateResponse;
import courseitda.mystorage.ui.dto.response.SavedCategoryPlaceCreateResponse;
import courseitda.mystorage.ui.dto.response.SavedCategoryPlaceUpdateResponse;
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
class SavedCategoryPlaceControllerTest {

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

    private SavedCategoryPlaceRequest anyCreatePlaceRequest() {
        return new SavedCategoryPlaceRequest(
                PlaceFixture.anyName(),
                PlaceFixture.anyPlaceUrl(),
                PlaceFixture.anyRoadAddressName(),
                PlaceFixture.anyAddressName(),
                PlaceFixture.anyLatitude(),
                PlaceFixture.anyLongitude()
        );
    }

    private SavedCategoryPlaceCreateResponse createPlaces(
            final String accessToken,
            final Long savedCategoryId,
            final List<SavedCategoryPlaceRequest> placeRequests
    ) {
        final SavedCategoryPlaceCreateRequest request = new SavedCategoryPlaceCreateRequest(placeRequests);

        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(request)
                .when()
                .post("/api/saved-categories/" + savedCategoryId + "/places")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(SavedCategoryPlaceCreateResponse.class);
    }

    @Nested
    @DisplayName("보관 카테고리 장소 추가 성공 시나리오")
    class CreateSavedCategoryPlacesSuccessScenarios {

        @Test
        @DisplayName("보관 카테고리 장소 추가에 성공한다")
        void createSavedCategoryPlaces_success() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateResponse created = createSavedCategory(accessToken);
            final SavedCategoryPlaceCreateRequest request = new SavedCategoryPlaceCreateRequest(
                    List.of(anyCreatePlaceRequest(), anyCreatePlaceRequest())
            );

            // when
            final SavedCategoryPlaceCreateResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .post("/api/saved-categories/" + created.id() + "/places")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract()
                    .as(SavedCategoryPlaceCreateResponse.class);

            // then
            assertThat(response.savedCategoryPlaceResponses()).hasSize(2);
        }

        @Test
        @DisplayName("장소가 있는 보관 카테고리 삭제에 성공한다")
        void deleteSavedCategoryWithPlaces_success() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateResponse created = createSavedCategory(accessToken);
            createPlaces(accessToken, created.id(),
                    List.of(anyCreatePlaceRequest(), anyCreatePlaceRequest(), anyCreatePlaceRequest()));

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .delete("/api/saved-categories/" + created.id())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("보관 카테고리 장소 추가 실패 시나리오")
    class CreateSavedCategoryPlacesFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 보관 카테고리에 장소 추가 시 실패한다")
        void createSavedCategoryPlaces_fail_notFound() {
            // given
            final String accessToken = signUpAndLogin();
            final Long nonExistentId = 999999L;
            final SavedCategoryPlaceCreateRequest request = new SavedCategoryPlaceCreateRequest(
                    List.of(anyCreatePlaceRequest())
            );

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .post("/api/saved-categories/" + nonExistentId + "/places")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.SAVED_CATEGORY_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("다른 사용자의 보관 카테고리에 장소 추가 시 실패한다")
        void createSavedCategoryPlaces_fail_forbidden() {
            // given
            final String owner = signUpAndLogin();
            final SavedCategoryCreateResponse created = createSavedCategory(owner);

            final String otherUser = signUpAndLogin();
            final SavedCategoryPlaceCreateRequest request = new SavedCategoryPlaceCreateRequest(
                    List.of(anyCreatePlaceRequest())
            );

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, otherUser)
                    .body(request)
                    .when()
                    .post("/api/saved-categories/" + created.id() + "/places")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.SAVED_CATEGORY_MODIFY_FORBIDDEN.getCode()));
        }

        @Test
        @DisplayName("장소 목록이 비어있는 경우 추가에 실패한다")
        void createSavedCategoryPlaces_fail_emptyPlaces() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateResponse created = createSavedCategory(accessToken);
            final SavedCategoryPlaceCreateRequest request = new SavedCategoryPlaceCreateRequest(List.of());

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .post("/api/saved-categories/" + created.id() + "/places")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.REQUEST_VALIDATION_FAILED.getCode()));
        }
    }

    @Nested
    @DisplayName("보관 카테고리 장소 동기화 성공 시나리오")
    class SyncSavedCategoryPlacesSuccessScenarios {

        @Test
        @DisplayName("기존 장소를 유지하면서 새 장소를 추가하는 동기화에 성공한다")
        void syncSavedCategoryPlaces_addPlace_success() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateResponse created = createSavedCategory(accessToken);

            final SavedCategoryPlaceCreateResponse createdPlaces = createPlaces(
                    accessToken, created.id(), List.of(anyCreatePlaceRequest())
            );
            final Long existingPlaceId = createdPlaces.savedCategoryPlaceResponses().get(0).id();

            final var keepExistingPlace = new SavedCategoryPlaceUpdateRequest.SavedCategoryPlaceRequest(
                    existingPlaceId,
                    PlaceFixture.anyName(),
                    PlaceFixture.anyPlaceUrl(),
                    PlaceFixture.anyRoadAddressName(),
                    PlaceFixture.anyAddressName(),
                    PlaceFixture.anyLatitude(),
                    PlaceFixture.anyLongitude()
            );
            final var newPlace = new SavedCategoryPlaceUpdateRequest.SavedCategoryPlaceRequest(
                    null,
                    PlaceFixture.anyName(),
                    PlaceFixture.anyPlaceUrl(),
                    PlaceFixture.anyRoadAddressName(),
                    PlaceFixture.anyAddressName(),
                    PlaceFixture.anyLatitude(),
                    PlaceFixture.anyLongitude()
            );
            final SavedCategoryPlaceUpdateRequest request = new SavedCategoryPlaceUpdateRequest(
                    List.of(keepExistingPlace, newPlace)
            );

            // when
            final SavedCategoryPlaceUpdateResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .patch("/api/saved-categories/" + created.id() + "/places")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(SavedCategoryPlaceUpdateResponse.class);

            // then
            assertThat(response.savedCategoryPlaceResponses()).hasSize(2);
        }

        @Test
        @DisplayName("기존 장소를 제거하는 동기화에 성공한다")
        void syncSavedCategoryPlaces_removePlace_success() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateResponse created = createSavedCategory(accessToken);
            createPlaces(accessToken, created.id(), List.of(anyCreatePlaceRequest(), anyCreatePlaceRequest()));

            // 기존 장소를 포함하지 않은 요청으로 전체 교체
            final var newPlace = new SavedCategoryPlaceUpdateRequest.SavedCategoryPlaceRequest(
                    null,
                    PlaceFixture.anyName(),
                    PlaceFixture.anyPlaceUrl(),
                    PlaceFixture.anyRoadAddressName(),
                    PlaceFixture.anyAddressName(),
                    PlaceFixture.anyLatitude(),
                    PlaceFixture.anyLongitude()
            );
            final SavedCategoryPlaceUpdateRequest request = new SavedCategoryPlaceUpdateRequest(List.of(newPlace));

            // when
            final SavedCategoryPlaceUpdateResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .patch("/api/saved-categories/" + created.id() + "/places")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(SavedCategoryPlaceUpdateResponse.class);

            // then
            assertThat(response.savedCategoryPlaceResponses()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("보관 카테고리 장소 동기화 실패 시나리오")
    class SyncSavedCategoryPlacesFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 보관 카테고리의 장소 동기화 시 실패한다")
        void syncSavedCategoryPlaces_fail_notFound() {
            // given
            final String accessToken = signUpAndLogin();
            final Long nonExistentId = 999999L;
            final var placeRequest = new SavedCategoryPlaceUpdateRequest.SavedCategoryPlaceRequest(
                    null,
                    PlaceFixture.anyName(),
                    PlaceFixture.anyPlaceUrl(),
                    PlaceFixture.anyRoadAddressName(),
                    PlaceFixture.anyAddressName(),
                    PlaceFixture.anyLatitude(),
                    PlaceFixture.anyLongitude()
            );
            final SavedCategoryPlaceUpdateRequest request = new SavedCategoryPlaceUpdateRequest(List.of(placeRequest));

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .patch("/api/saved-categories/" + nonExistentId + "/places")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.SAVED_CATEGORY_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("다른 사용자의 보관 카테고리 장소 동기화 시 실패한다")
        void syncSavedCategoryPlaces_fail_forbidden() {
            // given
            final String owner = signUpAndLogin();
            final SavedCategoryCreateResponse created = createSavedCategory(owner);

            final String otherUser = signUpAndLogin();
            final var placeRequest = new SavedCategoryPlaceUpdateRequest.SavedCategoryPlaceRequest(
                    null,
                    PlaceFixture.anyName(),
                    PlaceFixture.anyPlaceUrl(),
                    PlaceFixture.anyRoadAddressName(),
                    PlaceFixture.anyAddressName(),
                    PlaceFixture.anyLatitude(),
                    PlaceFixture.anyLongitude()
            );
            final SavedCategoryPlaceUpdateRequest request = new SavedCategoryPlaceUpdateRequest(List.of(placeRequest));

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, otherUser)
                    .body(request)
                    .when()
                    .patch("/api/saved-categories/" + created.id() + "/places")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.SAVED_CATEGORY_MODIFY_FORBIDDEN.getCode()));
        }

        @Test
        @DisplayName("장소 목록이 비어있는 경우 동기화에 실패한다")
        void syncSavedCategoryPlaces_fail_emptyPlaces() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateResponse created = createSavedCategory(accessToken);
            final SavedCategoryPlaceUpdateRequest request = new SavedCategoryPlaceUpdateRequest(List.of());

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .patch("/api/saved-categories/" + created.id() + "/places")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.REQUEST_VALIDATION_FAILED.getCode()));
        }
    }
}
