package courseitda.workspace.ui;

import courseitda.auth.domain.AuthRole;
import courseitda.auth.domain.MemberAuthInfo;
import courseitda.auth.domain.RequiresRole;
import courseitda.workspace.application.RepresentativeCategoryPlaceService;
import courseitda.workspace.ui.dto.request.RepresentativeCategoryPlaceUpdateRequest;
import courseitda.workspace.ui.dto.response.RepresentativeCategoryPlaceUpdateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequiresRole(authRoles = {AuthRole.MEMBER})
@RequestMapping("/api/categories/{categoryId}/representative-place")
public class RepresentativeCategoryPlaceController {

    private final RepresentativeCategoryPlaceService representativeCategoryPlaceService;

    // 카테고리 대표 장소 수정
    @PutMapping
    public ResponseEntity<RepresentativeCategoryPlaceUpdateResponse> updateRepresentativeCategoryPlace(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final Long categoryId,
            @Valid @RequestBody final RepresentativeCategoryPlaceUpdateRequest request
    ) {
        final RepresentativeCategoryPlaceUpdateResponse response = representativeCategoryPlaceService
                .updateRepresentativeCategoryPlace(
                        memberAuthInfo,
                        categoryId,
                        request.toCommand()
                );

        return ResponseEntity.ok(response);
    }

    // 카테고리 대표 장소 해제
    @DeleteMapping
    public ResponseEntity<Void> deleteRepresentativeCategoryPlace(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final Long categoryId
    ) {
        representativeCategoryPlaceService.deleteRepresentativeCategoryPlace(memberAuthInfo, categoryId);

        return ResponseEntity.noContent().build();
    }
}
