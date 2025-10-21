package courseitda.workspace.ui;

import courseitda.auth.domain.AuthRole;
import courseitda.auth.domain.MemberAuthInfo;
import courseitda.auth.domain.RequiresRole;
import courseitda.workspace.application.CategoryPlaceService;
import courseitda.workspace.application.dto.request.DeleteCategoryPlaceCommand;
import courseitda.workspace.application.dto.request.FindCategoryPlacesCommand;
import courseitda.workspace.ui.dto.request.CategoryPlaceCreateRequest;
import courseitda.workspace.ui.dto.response.CategoryPlaceCreateResponse;
import courseitda.workspace.ui.dto.response.CategoryPlacesResponse;
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
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final Long categoryId,
            @Valid @RequestBody final CategoryPlaceCreateRequest request
    ) {

        // ✅ 201 Created	카테고리 장소 생성 성공
        final var result = categoryPlaceService.createCategoryPlace(
                request.toCommandWith(memberAuthInfo, categoryId)
        );

        return ResponseEntity.created(URI.create("/api/categories/" + categoryId + "/category-places/" + result.id()))
                .body(CategoryPlaceCreateResponse.from(result));
    }

    // 카테고리 장소 삭제
    @DeleteMapping("/{categoryPlaceId}")
    public ResponseEntity<Void> deleteCategoryPlace(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final Long categoryId,
            @PathVariable final Long categoryPlaceId
    ) {

        // ✅ 204 No Content	카테고리 장소 삭제 성공
        // ✅ 403 Forbidden 카테고리 안의 장소가 아닐때
        categoryPlaceService.deleteCategoryPlace(
                new DeleteCategoryPlaceCommand(memberAuthInfo, categoryId, categoryPlaceId)
        );

        return ResponseEntity.noContent().build();
    }

    // 카테고리 장소 목록 조회
    @GetMapping
    public ResponseEntity<CategoryPlacesResponse> readCategoryPlaces(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final Long categoryId
    ) {

        // ✅ 200 OK	카테고리 장소 목록 조회 성공
        final var result = categoryPlaceService.findCategoryPlaces(
                new FindCategoryPlacesCommand(memberAuthInfo, categoryId)
        );

        return ResponseEntity.ok(CategoryPlacesResponse.from(result));
    }
}
