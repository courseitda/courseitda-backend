package courseitda.community.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import courseitda.community.ui.dto.response.RecommendedCategoriesReadResponse;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RecommendedCategoryControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    @DisplayName("추천 카테고리 전체 조회 성공 시나리오")
    class ReadAllRecommendedCategoriesSuccessScenarios {

        @Test
        @DisplayName("추천 카테고리 전체 조회에 성공한다")
        void readAllRecommendedCategories_success() {
            // when
            final RecommendedCategoriesReadResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/api/recommended-categories")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(RecommendedCategoriesReadResponse.class);

            // then
            assertThat(response.recommendedCategoryResponses()).isNotNull();
        }

        @Test
        @DisplayName("추천 카테고리가 없는 경우 빈 목록이 반환된다")
        void readAllRecommendedCategories_success_emptyList() {
            // when
            final RecommendedCategoriesReadResponse response = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/api/recommended-categories")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(RecommendedCategoriesReadResponse.class);

            // then
            assertThat(response.recommendedCategoryResponses()).isEmpty();
        }
    }
}
