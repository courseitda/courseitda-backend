package courseitda.category.ui;

import courseitda.auth.domain.AuthRole;
import courseitda.auth.domain.RequiresRole;
import courseitda.category.application.CategoryPlaceService;
import courseitda.category.ui.dto.request.CategoryPlaceCreateRequest;
import courseitda.category.ui.dto.response.CategoryPlaceCreateResponse;
import courseitda.category.ui.dto.response.CategoryPlaceResponses;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequiresRole(authRoles = {AuthRole.MEMBER})
@RequestMapping("/api/categories/{categoryId}/category-places")
public class CategoryPlaceController {

    private final CategoryPlaceService categoryPlaceService;

    // 카테고리 장소 생성
    @PostMapping
    public ResponseEntity<CategoryPlaceCreateResponse> createCategoryPlace(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryPlaceCreateRequest request
    ) {

        // ✅ 201 Created	카테고리 장소 생성 성공
        CategoryPlaceCreateResponse response = categoryPlaceService.createCategoryPlace(categoryId, request);
        return ResponseEntity.created(URI.create("/api/categories/" + categoryId + "/category-places" + response.id()))
                .body(response);
    }

    // 카테고리 장소 삭제
    @DeleteMapping("/{categoryPlaceId}")
    public ResponseEntity<Void> deleteCategoryPlace(
            @PathVariable Long categoryId,
            @PathVariable Long categoryPlaceId
    ) {

        // ✅ 204 No Content	카테고리 장소 삭제 성공
        // ✅ 403 Forbidden 카테고리 안의 장소가 아닐때
        categoryPlaceService.deleteCategoryPlace(categoryId, categoryPlaceId);
        return ResponseEntity.noContent().build();
    }

    // 카테고리 장소 목록 조회
    @GetMapping
    public ResponseEntity<CategoryPlaceResponses> readCategoryPlaces(
            @PathVariable Long categoryId
    ) {

        // ✅ 200 OK	카테고리 장소 목록 조회 성공
        CategoryPlaceResponses response = categoryPlaceService.findCategoryPlaces(categoryId);
        return ResponseEntity.ok(response);
    }
}
