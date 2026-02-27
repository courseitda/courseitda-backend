package courseitda.community.ui;

import courseitda.auth.domain.AuthRole;
import courseitda.auth.domain.MemberAuthInfo;
import courseitda.auth.domain.RequiresRole;
import courseitda.community.application.SharedCategoryService;
import courseitda.community.ui.dto.request.SharedCategoryCreateRequest;
import courseitda.community.ui.dto.response.SharedCategoriesReadResponse;
import courseitda.community.ui.dto.response.SharedCategoryCreateResponse;
import courseitda.community.ui.dto.response.SharedCategoryReadResponse;
import courseitda.community.ui.dto.response.SharedCategorySearchResponse;
import courseitda.member.domain.Member;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shared-categories")
public class SharedCategoryController {

    private final SharedCategoryService sharedCategoryService;

    @PostMapping
    @RequiresRole(authRoles = {AuthRole.MEMBER})
    public ResponseEntity<SharedCategoryCreateResponse> createSharedCategory(
            final Member member,
            @Valid @RequestBody final SharedCategoryCreateRequest request
    ) {
        final var response = sharedCategoryService.createSharedCategory(request, member);

        return ResponseEntity.created(URI.create("/api/shared-categories/" + response.id()))
                .body(response);
    }

    @DeleteMapping("/{sharedCategoryId}")
    @RequiresRole(authRoles = {AuthRole.MEMBER})
    public ResponseEntity<Void> deleteSharedCategory(
            final MemberAuthInfo memberAuthInfo,
            @PathVariable final Long sharedCategoryId
    ) {
        sharedCategoryService.deleteSharedCategory(memberAuthInfo, sharedCategoryId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sharedCategoryId}")
    public ResponseEntity<SharedCategoryReadResponse> readSharedCategory(
            @PathVariable final Long sharedCategoryId
    ) {
        final var response = sharedCategoryService.findSharedCategory(sharedCategoryId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<SharedCategoriesReadResponse> readAllSharedCategories(
            @RequestParam(required = false) final Long cursor,
            @RequestParam(defaultValue = "10") final int size
    ) {
        final var response = sharedCategoryService.findAllSharedCategories(cursor, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<SharedCategorySearchResponse> searchSharedCategories(
            @RequestParam final String keyword,
            @RequestParam(required = false) final Long cursor,
            @RequestParam(defaultValue = "10") final int size
    ) {
        final var response = sharedCategoryService.searchSharedCategories(keyword, cursor, size);

        return ResponseEntity.ok(response);
    }
}
