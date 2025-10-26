package courseitda.workspace.ui;

import courseitda.auth.domain.AuthRole;
import courseitda.auth.domain.MemberAuthInfo;
import courseitda.auth.domain.RequiresRole;
import courseitda.workspace.application.CategoryService;
import courseitda.workspace.application.dto.request.DeleteCategoryCommand;
import courseitda.workspace.application.dto.request.DeleteRepresentativeCategoryPlaceCommand;
import courseitda.workspace.application.dto.request.FindCategoryCommand;
import courseitda.workspace.ui.dto.request.CategoryUpdateRequest;
import courseitda.workspace.ui.dto.request.RepresentativeCategoryPlaceUpdateRequest;
import courseitda.workspace.ui.dto.response.CategoryReadResponse;
import courseitda.workspace.ui.dto.response.CategoryUpdateResponse;
import courseitda.workspace.ui.dto.response.RepresentativeCategoryPlaceUpdateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequiresRole(authRoles = {AuthRole.MEMBER})
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 대표 장소 수정
    @PutMapping("/{categoryId}/representative-place")
    public ResponseEntity<RepresentativeCategoryPlaceUpdateResponse> updateRepresentativeCategoryPlace(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final Long categoryId,
            @Valid @RequestBody final RepresentativeCategoryPlaceUpdateRequest request
    ) {
        final var result = categoryService.updateRepresentativeCategoryPlace(
                request.toCommandWith(memberAuthInfo, categoryId)
        );

        return ResponseEntity.ok(RepresentativeCategoryPlaceUpdateResponse.from(result));
    }

    // 카테고리 (이름/색상) 수정
    @PatchMapping("/{categoryId}")
    public ResponseEntity<CategoryUpdateResponse> updateCategory(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final Long categoryId,
            @Valid @RequestBody final CategoryUpdateRequest request
    ) {
        final var response = categoryService.updateCategory(
                request.toCommandWith(memberAuthInfo, categoryId)
        );

        return ResponseEntity.ok(CategoryUpdateResponse.from(response));
    }

    // 카테고리 대표 장소 해제
    @DeleteMapping("/{categoryId}/representative-place")
    public ResponseEntity<Void> deleteRepresentativeCategoryPlace(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final Long categoryId
    ) {
        categoryService.deleteRepresentativeCategoryPlace(
                new DeleteRepresentativeCategoryPlaceCommand(memberAuthInfo, categoryId)
        );

        return ResponseEntity.noContent().build();
    }

    // 카테고리 삭제
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final Long categoryId
    ) {
        categoryService.deleteCategory(
                new DeleteCategoryCommand(memberAuthInfo, categoryId)
        );

        return ResponseEntity.noContent().build();
    }

    // 카테고리 단건 조회
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryReadResponse> readCategory(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final Long categoryId
    ) {
        final var response = categoryService.findCategory(
                new FindCategoryCommand(memberAuthInfo, categoryId)
        );

        return ResponseEntity.ok(CategoryReadResponse.from(response));
    }
}
