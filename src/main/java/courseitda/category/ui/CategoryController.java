package courseitda.category.ui;

import courseitda.auth.domain.AuthRole;
import courseitda.auth.domain.RequiresRole;
import courseitda.category.application.CategoryService;
import courseitda.category.ui.dto.request.CategoryCreateRequest;
import courseitda.category.ui.dto.request.CategoryReorderRequest;
import courseitda.category.ui.dto.request.CategoryUpdateRequest;
import courseitda.category.ui.dto.response.CategoriesResponse;
import courseitda.category.ui.dto.response.CategoryCreateResponse;
import courseitda.category.ui.dto.response.CategoryReorderResponse;
import courseitda.category.ui.dto.response.CategoryResponse;
import courseitda.category.ui.dto.response.CategoryUpdateResponse;
import courseitda.member.domain.Member;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequiresRole(authRoles = {AuthRole.MEMBER})
@RequestMapping("/api/workspaces/{workspaceId}/categories")
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 생성
    @PostMapping
    public ResponseEntity<CategoryCreateResponse> createCategory(
            Member member,
            @PathVariable Long workspaceId,
            @Valid @RequestBody CategoryCreateRequest request
    ) {
        CategoryCreateResponse response = categoryService.createCategory(member, workspaceId, request);
        return ResponseEntity.created(URI.create("/api/workspaces/" + workspaceId + "/categories/" + response.id()))
                .body(response);
    }

    // 카테고리 순서 변경
    @PostMapping("/reorder")
    public ResponseEntity<CategoryReorderResponse> updateCategorySequence(
            Member member,
            @PathVariable Long workspaceId,
            @Valid @RequestBody CategoryReorderRequest request
    ) {
        CategoryReorderResponse response = categoryService.updateCategorySequence(member, workspaceId, request);
        return ResponseEntity.ok(response);
    }

    // 카테고리 (이름/색상) 수정
    @PatchMapping("/{categoryId}")
    public ResponseEntity<CategoryUpdateResponse> updateCategory(
            Member member,
            @PathVariable Long workspaceId,
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryUpdateRequest request
    ) {
        CategoryUpdateResponse response = categoryService.updateCategory(member, workspaceId, categoryId, request);
        return ResponseEntity.ok(response);
    }

    // 카테고리 삭제
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            Member member,
            @PathVariable Long workspaceId,
            @PathVariable Long categoryId
    ) {
        categoryService.deleteCategory(member, workspaceId, categoryId);
        return ResponseEntity.noContent().build();
    }

    // 카테고리 단건 조회
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> readCategory(
            Member member,
            @PathVariable Long workspaceId,
            @PathVariable Long categoryId
    ) {
        CategoryResponse response = categoryService.findCategory(member, workspaceId, categoryId);
        return ResponseEntity.ok(response);
    }

    // 카테고리 목록 전체 조회 - 워크스페이스 상세 페이지
    @GetMapping
    public ResponseEntity<CategoriesResponse> readAllCategories(
            Member member,
            @PathVariable Long workspaceId
    ) {
        CategoriesResponse response = categoryService.findAllCategories(member, workspaceId);
        return ResponseEntity.ok(response);
    }
}
