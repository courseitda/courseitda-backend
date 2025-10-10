package courseitda.category.ui;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import courseitda.category.application.CategoryPlaceService;
import courseitda.category.ui.dto.request.CategoryPlaceCreateRequest;
import courseitda.category.ui.dto.response.CategoryPlaceCreateResponse;
import courseitda.category.ui.dto.response.CategoryPlaceResponses;
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

    // 카테고리 장소 삭제
    @DeleteMapping("/category-places/{categoryPlaceId}")
    public ResponseEntity<Void> deleteCategoryPlace(
            @PathVariable Long categoryPlaceId
    ) {
        // ✅ 204 No Content	카테고리 장소 삭제 성공
        categoryPlaceService.deleteCategoryPlace(categoryPlaceId);
        return ResponseEntity.noContent().build();
    }

    // 카테고리 장소 목록 조회
    @GetMapping("/categories/{categoryId}/category-places")
    public ResponseEntity<CategoryPlaceResponses> readCategoryPlaces(
            // TODO: 헤더 인증 필요
            @PathVariable Long categoryId
    ) {

        // ✅ 200 OK	카테고리 장소 목록 조회 성공
        CategoryPlaceResponses response = categoryPlaceService.findCategoryPlaces(categoryId);
        return ResponseEntity.ok(response);
    }
}
