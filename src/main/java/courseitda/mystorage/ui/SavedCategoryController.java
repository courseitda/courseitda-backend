package courseitda.mystorage.ui;

import courseitda.auth.domain.AuthRole;
import courseitda.auth.domain.RequiresRole;
import courseitda.member.domain.Member;
import courseitda.mystorage.application.SavedCategoryService;
import courseitda.mystorage.ui.dto.request.SavedCategoryCreateRequest;
import courseitda.mystorage.ui.dto.response.SavedCategoryCreateResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/saved-categories")
@RequiresRole(authRoles = {AuthRole.MEMBER})
public class SavedCategoryController {

    private final SavedCategoryService savedCategoryService;

    @PostMapping
    public ResponseEntity<SavedCategoryCreateResponse> createSavedCategory(
            final Member member,
            @Valid @RequestBody final SavedCategoryCreateRequest request
    ) {
        final var response = savedCategoryService.createSavedCategory(request, member);

        return ResponseEntity.created(URI.create("/api/saved-categories/" + response.id()))
                .body(response);
    }
}
