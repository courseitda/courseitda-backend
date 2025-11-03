package courseitda.workspace.ui;

import courseitda.auth.domain.AuthRole;
import courseitda.auth.domain.MemberAuthInfo;
import courseitda.auth.domain.RequiresRole;
import courseitda.workspace.application.CategoryPlaceService;
import courseitda.workspace.ui.dto.request.CategoryPlaceCreateRequest;
import courseitda.workspace.ui.dto.response.CategoryPlaceCreateResponse;
import courseitda.workspace.ui.dto.response.CategoryPlacesFindResponse;
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
@RequestMapping("/api/categories/{categoryId}/places")
public class CategoryPlaceController {

    private final CategoryPlaceService categoryPlaceService;

    // 카테고리 장소 생성
    @PostMapping
    public ResponseEntity<CategoryPlaceCreateResponse> createCategoryPlace(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final Long categoryId,
            @Valid @RequestBody final CategoryPlaceCreateRequest request
    ) {
        final var response = categoryPlaceService.createCategoryPlace(request, memberAuthInfo, categoryId);

        return ResponseEntity.created(URI.create("/api/categories/" + categoryId + "/category-places/" + response.id()))
                .body(response);
    }

    // 카테고리 장소 삭제
    @DeleteMapping("/{categoryPlaceId}")
    public ResponseEntity<Void> deleteCategoryPlace(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final Long categoryId,
            @PathVariable final Long categoryPlaceId
    ) {
        categoryPlaceService.deleteCategoryPlace(memberAuthInfo, categoryId, categoryPlaceId);

        return ResponseEntity.noContent().build();
    }

    // 카테고리 장소 목록 조회
    @GetMapping
    public ResponseEntity<CategoryPlacesFindResponse> readCategoryPlaces(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final Long categoryId
    ) {
        final var response = categoryPlaceService.findCategoryPlaces(memberAuthInfo, categoryId);

        return ResponseEntity.ok(response);
    }
}
