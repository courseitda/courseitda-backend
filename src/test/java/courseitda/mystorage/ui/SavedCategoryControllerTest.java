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
import courseitda.mystorage.ui.dto.request.SavedCategoryCreateRequest.SavedCategoryPlaceRequest;
import courseitda.mystorage.ui.dto.request.SavedCategoryUpdateRequest;
import courseitda.mystorage.ui.dto.response.SavedCategoryCreateResponse;
import courseitda.mystorage.ui.dto.response.SavedCategoryReadResponse;
import courseitda.mystorage.ui.dto.response.SavedCategoryUpdateResponse;
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
class SavedCategoryControllerTest {

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
        final SavedCategoryCreateRequest request = new SavedCategoryCreateRequest(
                SavedCategoryFixture.anyName(),
                List.of(anyPlaceRequest())
        );

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

    @Nested
    @DisplayName("보관 카테고리 생성 성공 시나리오")
    class CreateSavedCategorySuccessScenarios {

        @Test
        @DisplayName("보관 카테고리 생성에 성공한다")
        void createSavedCategory_success() {
            // given
            final String accessToken = signUpAndLogin();
            final String name = SavedCategoryFixture.anyName();
            final SavedCategoryCreateRequest request = new SavedCategoryCreateRequest(
                    name,
                    List.of(anyPlaceRequest())
            );

            // when
            final SavedCategoryCreateResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .post("/api/saved-categories")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract()
                    .as(SavedCategoryCreateResponse.class);

            // then
            assertThat(response.id()).isNotNull();
            assertThat(response.name()).isEqualTo(name);
        }
    }

    @Nested
    @DisplayName("보관 카테고리 생성 실패 시나리오")
    class CreateSavedCategoryFailureScenarios {

        @Test
        @DisplayName("보관 카테고리 이름이 10자 초과인 경우 생성에 실패한다")
        void createSavedCategory_fail_nameTooLong() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateRequest request = new SavedCategoryCreateRequest(
                    "a".repeat(11),
                    List.of(anyPlaceRequest())
            );

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .post("/api/saved-categories")
                    .then()
                    .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                    .body("code",
                            org.hamcrest.Matchers.equalTo(ErrorCode.SAVED_CATEGORY_NAME_LENGTH_EXCEEDED.getCode()));
        }

        @Test
        @DisplayName("보관 카테고리 이름이 빈 값인 경우 생성에 실패한다")
        void createSavedCategory_fail_emptyName() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateRequest request = new SavedCategoryCreateRequest(
                    "",
                    List.of(anyPlaceRequest())
            );

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .post("/api/saved-categories")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.REQUEST_VALIDATION_FAILED.getCode()));
        }

        @Test
        @DisplayName("장소 목록이 비어있는 경우 생성에 실패한다")
        void createSavedCategory_fail_emptyPlaces() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateRequest request = new SavedCategoryCreateRequest(
                    SavedCategoryFixture.anyName(),
                    List.of()
            );

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .post("/api/saved-categories")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.REQUEST_VALIDATION_FAILED.getCode()));
        }
    }

    @Nested
    @DisplayName("보관 카테고리 단건 조회 성공 시나리오")
    class ReadSavedCategorySuccessScenarios {

        @Test
        @DisplayName("보관 카테고리 단건 조회에 성공한다")
        void readSavedCategory_success() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateResponse created = createSavedCategory(accessToken);

            // when
            final SavedCategoryReadResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/saved-categories/" + created.id())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(SavedCategoryReadResponse.class);

            // then
            assertThat(response.id()).isEqualTo(created.id());
            assertThat(response.name()).isEqualTo(created.name());
            assertThat(response.savedCategoryPlaceResponses()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("보관 카테고리 단건 조회 실패 시나리오")
    class ReadSavedCategoryFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 보관 카테고리 조회 시 실패한다")
        void readSavedCategory_fail_notFound() {
            // given
            final String accessToken = signUpAndLogin();
            final Long nonExistentId = 999999L;

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/saved-categories/" + nonExistentId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.SAVED_CATEGORY_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("다른 사용자의 보관 카테고리 조회 시 실패한다")
        void readSavedCategory_fail_forbidden() {
            // given
            final String owner = signUpAndLogin();
            final SavedCategoryCreateResponse created = createSavedCategory(owner);

            final String otherUser = signUpAndLogin();

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, otherUser)
                    .when()
                    .get("/api/saved-categories/" + created.id())
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.SAVED_CATEGORY_MODIFY_FORBIDDEN.getCode()));
        }
    }

    @Nested
    @DisplayName("보관 카테고리 수정 성공 시나리오")
    class UpdateSavedCategorySuccessScenarios {

        @Test
        @DisplayName("보관 카테고리 이름 수정에 성공한다")
        void updateSavedCategory_success_rename() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateResponse created = createSavedCategory(accessToken);

            final SavedCategoryReadResponse before = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/saved-categories/" + created.id())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(SavedCategoryReadResponse.class);

            final Long existingPlaceId = before.savedCategoryPlaceResponses().get(0).id();
            final String newName = SavedCategoryFixture.anyName();
            final var keepExistingPlace = new SavedCategoryUpdateRequest.SavedCategoryPlaceRequest(
                    existingPlaceId,
                    PlaceFixture.anyName(),
                    PlaceFixture.anyPlaceUrl(),
                    PlaceFixture.anyRoadAddressName(),
                    PlaceFixture.anyAddressName(),
                    PlaceFixture.anyLatitude(),
                    PlaceFixture.anyLongitude()
            );
            final SavedCategoryUpdateRequest request = new SavedCategoryUpdateRequest(newName,
                    List.of(keepExistingPlace));

            // when
            final SavedCategoryUpdateResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .patch("/api/saved-categories/" + created.id())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(SavedCategoryUpdateResponse.class);

            // then
            assertThat(response.id()).isEqualTo(created.id());
            assertThat(response.name()).isEqualTo(newName);
        }

        @Test
        @DisplayName("보관 카테고리 장소 추가 수정에 성공한다")
        void updateSavedCategory_success_addPlace() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateResponse created = createSavedCategory(accessToken);

            final SavedCategoryReadResponse before = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .get("/api/saved-categories/" + created.id())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(SavedCategoryReadResponse.class);

            final Long existingPlaceId = before.savedCategoryPlaceResponses().get(0).id();
            final var keepExistingPlace = new SavedCategoryUpdateRequest.SavedCategoryPlaceRequest(
                    existingPlaceId,
                    PlaceFixture.anyName(),
                    PlaceFixture.anyPlaceUrl(),
                    PlaceFixture.anyRoadAddressName(),
                    PlaceFixture.anyAddressName(),
                    PlaceFixture.anyLatitude(),
                    PlaceFixture.anyLongitude()
            );
            final var newPlace = new SavedCategoryUpdateRequest.SavedCategoryPlaceRequest(
                    null,
                    PlaceFixture.anyName(),
                    PlaceFixture.anyPlaceUrl(),
                    PlaceFixture.anyRoadAddressName(),
                    PlaceFixture.anyAddressName(),
                    PlaceFixture.anyLatitude(),
                    PlaceFixture.anyLongitude()
            );
            final SavedCategoryUpdateRequest request = new SavedCategoryUpdateRequest(
                    SavedCategoryFixture.anyName(),
                    List.of(keepExistingPlace, newPlace)
            );

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .patch("/api/saved-categories/" + created.id())
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        @DisplayName("보관 카테고리 장소 제거 수정에 성공한다")
        void updateSavedCategory_success_removePlace() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateResponse created = createSavedCategory(accessToken);

            final var newPlace = new SavedCategoryUpdateRequest.SavedCategoryPlaceRequest(
                    null,
                    PlaceFixture.anyName(),
                    PlaceFixture.anyPlaceUrl(),
                    PlaceFixture.anyRoadAddressName(),
                    PlaceFixture.anyAddressName(),
                    PlaceFixture.anyLatitude(),
                    PlaceFixture.anyLongitude()
            );
            // 기존 장소를 포함하지 않아 삭제, 새 장소 추가
            final SavedCategoryUpdateRequest request = new SavedCategoryUpdateRequest(
                    SavedCategoryFixture.anyName(),
                    List.of(newPlace)
            );

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .patch("/api/saved-categories/" + created.id())
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }
    }

    @Nested
    @DisplayName("보관 카테고리 수정 실패 시나리오")
    class UpdateSavedCategoryFailureScenarios {

        @Test
        @DisplayName("보관 카테고리 이름이 10자 초과인 경우 수정에 실패한다")
        void updateSavedCategory_fail_nameTooLong() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateResponse created = createSavedCategory(accessToken);

            final var placeRequest = new SavedCategoryUpdateRequest.SavedCategoryPlaceRequest(
                    null,
                    PlaceFixture.anyName(),
                    PlaceFixture.anyPlaceUrl(),
                    PlaceFixture.anyRoadAddressName(),
                    PlaceFixture.anyAddressName(),
                    PlaceFixture.anyLatitude(),
                    PlaceFixture.anyLongitude()
            );
            final SavedCategoryUpdateRequest request = new SavedCategoryUpdateRequest(
                    "a".repeat(11),
                    List.of(placeRequest)
            );

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .patch("/api/saved-categories/" + created.id())
                    .then()
                    .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                    .body("code",
                            org.hamcrest.Matchers.equalTo(ErrorCode.SAVED_CATEGORY_NAME_LENGTH_EXCEEDED.getCode()));
        }

        @Test
        @DisplayName("존재하지 않는 보관 카테고리 수정 시 실패한다")
        void updateSavedCategory_fail_notFound() {
            // given
            final String accessToken = signUpAndLogin();
            final Long nonExistentId = 999999L;

            final var placeRequest = new SavedCategoryUpdateRequest.SavedCategoryPlaceRequest(
                    null,
                    PlaceFixture.anyName(),
                    PlaceFixture.anyPlaceUrl(),
                    PlaceFixture.anyRoadAddressName(),
                    PlaceFixture.anyAddressName(),
                    PlaceFixture.anyLatitude(),
                    PlaceFixture.anyLongitude()
            );
            final SavedCategoryUpdateRequest request = new SavedCategoryUpdateRequest(
                    SavedCategoryFixture.anyName(),
                    List.of(placeRequest)
            );

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .patch("/api/saved-categories/" + nonExistentId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.SAVED_CATEGORY_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("다른 사용자의 보관 카테고리 수정 시 실패한다")
        void updateSavedCategory_fail_forbidden() {
            // given
            final String owner = signUpAndLogin();
            final SavedCategoryCreateResponse created = createSavedCategory(owner);

            final String otherUser = signUpAndLogin();
            final var placeRequest = new SavedCategoryUpdateRequest.SavedCategoryPlaceRequest(
                    null,
                    PlaceFixture.anyName(),
                    PlaceFixture.anyPlaceUrl(),
                    PlaceFixture.anyRoadAddressName(),
                    PlaceFixture.anyAddressName(),
                    PlaceFixture.anyLatitude(),
                    PlaceFixture.anyLongitude()
            );
            final SavedCategoryUpdateRequest request = new SavedCategoryUpdateRequest(
                    SavedCategoryFixture.anyName(),
                    List.of(placeRequest)
            );

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, otherUser)
                    .body(request)
                    .when()
                    .patch("/api/saved-categories/" + created.id())
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.SAVED_CATEGORY_MODIFY_FORBIDDEN.getCode()));
        }
    }

    @Nested
    @DisplayName("보관 카테고리 삭제 성공 시나리오")
    class DeleteSavedCategorySuccessScenarios {

        @Test
        @DisplayName("보관 카테고리 삭제에 성공한다")
        void deleteSavedCategory_success() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateResponse created = createSavedCategory(accessToken);

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .delete("/api/saved-categories/" + created.id())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @DisplayName("장소가 여러 개 포함된 보관 카테고리 삭제에 성공한다")
        void deleteSavedCategory_withPlaces_success() {
            // given
            final String accessToken = signUpAndLogin();
            final SavedCategoryCreateRequest request = new SavedCategoryCreateRequest(
                    SavedCategoryFixture.anyName(),
                    List.of(anyPlaceRequest(), anyPlaceRequest(), anyPlaceRequest())
            );

            final SavedCategoryCreateResponse created = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .body(request)
                    .when()
                    .post("/api/saved-categories")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract()
                    .as(SavedCategoryCreateResponse.class);

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
    @DisplayName("보관 카테고리 삭제 실패 시나리오")
    class DeleteSavedCategoryFailureScenarios {

        @Test
        @DisplayName("존재하지 않는 보관 카테고리 삭제 시 실패한다")
        void deleteSavedCategory_fail_notFound() {
            // given
            final String accessToken = signUpAndLogin();
            final Long nonExistentId = 999999L;

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .when()
                    .delete("/api/saved-categories/" + nonExistentId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.SAVED_CATEGORY_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("다른 사용자의 보관 카테고리 삭제 시 실패한다")
        void deleteSavedCategory_fail_forbidden() {
            // given
            final String owner = signUpAndLogin();
            final SavedCategoryCreateResponse created = createSavedCategory(owner);

            final String otherUser = signUpAndLogin();

            // when & then
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, otherUser)
                    .when()
                    .delete("/api/saved-categories/" + created.id())
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body("code", org.hamcrest.Matchers.equalTo(ErrorCode.SAVED_CATEGORY_MODIFY_FORBIDDEN.getCode()));
        }
    }
}
