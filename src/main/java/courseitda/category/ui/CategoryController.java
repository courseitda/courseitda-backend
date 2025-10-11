package courseitda.category.ui;

import courseitda.auth.domain.AuthRole;
import courseitda.auth.domain.RequiresRole;
import courseitda.category.application.CategoryService;
import courseitda.category.ui.dto.request.CategoryCreateRequest;
import courseitda.category.ui.dto.request.CategoryReorderRequest;
import courseitda.category.ui.dto.response.CategoryCreateResponse;
import courseitda.category.ui.dto.response.CategoryReorderResponse;
import courseitda.member.domain.Member;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/reorder")
    public ResponseEntity<CategoryReorderResponse> updateCategorySequence(
            Member member,
            @PathVariable Long workspaceId,
            @Valid @RequestBody CategoryReorderRequest request
    ) {
        CategoryReorderResponse response = categoryService.updateCategorySequence(member, workspaceId, request);
        return ResponseEntity.ok(response);
    }
}
