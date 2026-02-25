package courseitda.community.ui;

import courseitda.auth.domain.AuthRole;
import courseitda.auth.domain.RequiresRole;
import courseitda.community.application.SharedCategoryService;
import courseitda.community.ui.dto.request.SharedCategoryCreateRequest;
import courseitda.community.ui.dto.response.SharedCategoryCreateResponse;
import courseitda.member.domain.Member;
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
@RequestMapping("/api/shared-categories")
@RequiresRole(authRoles = {AuthRole.MEMBER})
public class SharedCategoryController {

    private final SharedCategoryService sharedCategoryService;

    @PostMapping
    public ResponseEntity<SharedCategoryCreateResponse> createSharedCategory(
            final Member member,
            @Valid @RequestBody final SharedCategoryCreateRequest request
    ) {
        final var response = sharedCategoryService.createSharedCategory(request, member);

        return ResponseEntity.created(URI.create("/api/shared-categories/" + response.id()))
                .body(response);
    }
}
