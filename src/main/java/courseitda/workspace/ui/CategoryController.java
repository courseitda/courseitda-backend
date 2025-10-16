package courseitda.workspace.ui;

import courseitda.auth.domain.AuthRole;
import courseitda.auth.domain.MemberAuthInfo;
import courseitda.auth.domain.RequiresRole;
import courseitda.workspace.application.CategoryService;
import courseitda.workspace.ui.dto.request.CategoryCreateRequest;
import courseitda.workspace.ui.dto.request.CategoryReorderRequest;
import courseitda.workspace.ui.dto.request.CategoryUpdateRequest;
import courseitda.workspace.ui.dto.response.CategoriesResponse;
import courseitda.workspace.ui.dto.response.CategoryCreateResponse;
import courseitda.workspace.ui.dto.response.CategoryReorderResponse;
import courseitda.workspace.ui.dto.response.CategoryResponse;
import courseitda.workspace.ui.dto.response.CategoryUpdateResponse;
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
@RequestMapping("/api/workspaces/{workspaceIdentifier}/categories")
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 생성
    @PostMapping
    public ResponseEntity<CategoryCreateResponse> createCategory(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final String workspaceIdentifier,
            @Valid @RequestBody final CategoryCreateRequest request
    ) {
        final CategoryCreateResponse response = categoryService.createCategory(memberAuthInfo, workspaceIdentifier,
                request);
        return ResponseEntity.created(
                URI.create("/api/workspaces/"
                        + workspaceIdentifier + "/categories/"
                        + response.id()
                )
        ).body(response);
    }

    // 카테고리 순서 변경
    @PostMapping("/reorder")
    public ResponseEntity<CategoryReorderResponse> updateCategorySequence(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final String workspaceIdentifier,
            @Valid @RequestBody final CategoryReorderRequest request
    ) {
        final CategoryReorderResponse response = categoryService.updateCategorySequence(
                memberAuthInfo,
                workspaceIdentifier,
                request
        );
        return ResponseEntity.ok(response);
    }

    // 카테고리 (이름/색상) 수정
    @PatchMapping("/{categoryId}")
    public ResponseEntity<CategoryUpdateResponse> updateCategory(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final String workspaceIdentifier,
            @PathVariable final Long categoryId,
            @Valid @RequestBody final CategoryUpdateRequest request
    ) {
        final CategoryUpdateResponse response = categoryService.updateCategory(
                memberAuthInfo,
                workspaceIdentifier,
                categoryId,
                request
        );

        return ResponseEntity.ok(response);
    }

    // 카테고리 삭제
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final String workspaceIdentifier,
            @PathVariable final Long categoryId
    ) {
        categoryService.deleteCategory(memberAuthInfo, workspaceIdentifier, categoryId);

        return ResponseEntity.noContent().build();
    }

    // 카테고리 단건 조회
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> readCategory(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final String workspaceIdentifier,
            @PathVariable final Long categoryId
    ) {
        final CategoryResponse response = categoryService.findCategory(memberAuthInfo, workspaceIdentifier, categoryId);

        return ResponseEntity.ok(response);
    }

    // 카테고리 목록 전체 조회 - 워크스페이스 상세 페이지
    @GetMapping
    public ResponseEntity<CategoriesResponse> readAllCategories(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final String workspaceIdentifier
    ) {
        final CategoriesResponse response = categoryService.findAllCategories(memberAuthInfo, workspaceIdentifier);

        return ResponseEntity.ok(response);
    }
}
