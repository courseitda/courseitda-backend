package courseitda.community.ui;

import courseitda.community.application.RecommendedCategoryService;
import courseitda.community.ui.dto.response.RecommendedCategoriesReadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommended-categories")
public class RecommendedCategoryController {

    private final RecommendedCategoryService recommendedCategoryService;

    @GetMapping
    public ResponseEntity<RecommendedCategoriesReadResponse> readAllRecommendedCategories() {
        final var response = recommendedCategoryService.findAllRecommendedCategories();

        return ResponseEntity.ok(response);
    }
}
