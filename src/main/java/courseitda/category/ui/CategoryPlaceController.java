package courseitda.category.ui;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import courseitda.category.application.CategoryPlaceService;
import courseitda.category.ui.dto.request.CategoryPlaceCreateRequest;
import courseitda.category.ui.dto.response.CategoryPlaceCreateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryPlaceController {

    private final CategoryPlaceService categoryPlaceService;

    // 카테고리 장소 생성
    @PostMapping("/categories/{categoryId}/category-places")
    public ResponseEntity<CategoryPlaceCreateResponse> createCategoryPlace(
            // TODO: 헤더 인증 필요
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryPlaceCreateRequest request
    ) {

        // ✅ 201 Created	카테고리 장소 생성 성공

        CategoryPlaceCreateResponse response = categoryPlaceService.createCategoryPlace(categoryId, request);
        return ResponseEntity.created(URI.create("/api/categories/" + categoryId + "/category-places" + response.id()))
                .body(response);
    }
}
