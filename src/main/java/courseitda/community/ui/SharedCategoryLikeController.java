package courseitda.community.ui;

import courseitda.auth.domain.AuthRole;
import courseitda.auth.domain.RequiresRole;
import courseitda.community.application.SharedCategoryLikeService;
import courseitda.community.ui.dto.response.SharedCategoryLikeCreateResponse;
import courseitda.member.domain.Member;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shared-categories")
@RequiresRole(authRoles = {AuthRole.MEMBER})
public class SharedCategoryLikeController {

    private final SharedCategoryLikeService sharedCategoryLikeService;

    @PostMapping("/{sharedCategoryId}/likes")
    public ResponseEntity<SharedCategoryLikeCreateResponse> createSharedCategoryLike(
            final Member member,
            @PathVariable final Long sharedCategoryId
    ) {
        final var response = sharedCategoryLikeService.createSharedCategoryLike(sharedCategoryId, member);

        return ResponseEntity.created(URI.create("/api/shared-categories/" + sharedCategoryId + "/likes"))
                .body(response);
    }
}
