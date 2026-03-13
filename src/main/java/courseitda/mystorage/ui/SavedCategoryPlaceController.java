package courseitda.mystorage.ui;

import courseitda.auth.domain.AuthRole;
import courseitda.auth.domain.MemberAuthInfo;
import courseitda.auth.domain.RequiresRole;
import courseitda.mystorage.application.SavedCategoryPlaceService;
import courseitda.mystorage.ui.dto.request.SavedCategoryPlaceCreateRequest;
import courseitda.mystorage.ui.dto.request.SavedCategoryPlaceUpdateRequest;
import courseitda.mystorage.ui.dto.response.SavedCategoryPlaceCreateResponse;
import courseitda.mystorage.ui.dto.response.SavedCategoryPlaceUpdateResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequiresRole(authRoles = {AuthRole.MEMBER})
@RequestMapping("/api/saved-categories/{savedCategoryId}/places")
public class SavedCategoryPlaceController {

    private final SavedCategoryPlaceService savedCategoryPlaceService;

    @PostMapping
    public ResponseEntity<SavedCategoryPlaceCreateResponse> createSavedCategoryPlaces(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final Long savedCategoryId,
            @Valid @RequestBody final SavedCategoryPlaceCreateRequest request
    ) {
        final var response = savedCategoryPlaceService.createSavedCategoryPlaces(request, memberAuthInfo,
                savedCategoryId);

        return ResponseEntity.created(URI.create("/api/saved-categories/" + savedCategoryId + "/places"))
                .body(response);
    }

    @PatchMapping
    public ResponseEntity<SavedCategoryPlaceUpdateResponse> updateSavedCategoryPlaces(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final Long savedCategoryId,
            @Valid @RequestBody final SavedCategoryPlaceUpdateRequest request
    ) {
        final var response = savedCategoryPlaceService.updateSavedCategoryPlaces(request, memberAuthInfo,
                savedCategoryId);

        return ResponseEntity.ok(response);
    }
}
