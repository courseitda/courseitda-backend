package courseitda.member.ui;

import static courseitda.auth.domain.AuthRole.MEMBER;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.auth.domain.RequiresRole;
import courseitda.member.application.MeService;
import courseitda.member.domain.Member;
import courseitda.member.ui.dto.response.ForkedSharedCategoryIdsResponse;
import courseitda.member.ui.dto.response.MemberDropdownResponse;
import courseitda.member.ui.dto.response.MemberNavigatorResponse;
import courseitda.member.ui.dto.response.MemberProfileResponse;
import courseitda.member.ui.dto.response.MySavedCategoriesResponse;
import courseitda.member.ui.dto.response.MySharedCategoriesResponse;
import courseitda.member.ui.dto.response.MyWorkspacesResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me")
@RequiresRole(authRoles = MEMBER)
public class MeController {

    private final MeService meService;

    @GetMapping("/navigator")
    public ResponseEntity<MemberNavigatorResponse> readMemberNavigator(
            final Member member
    ) {
        final var response = MemberNavigatorResponse.from(member);

        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/dropdown")
    public ResponseEntity<MemberDropdownResponse> readMemberDropdown(
            final Member member
    ) {
        final var response = MemberDropdownResponse.from(member);

        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<MemberProfileResponse> readMemberProfile(
            final Member member
    ) {
        final var response = MemberProfileResponse.from(member);

        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/workspaces")
    public ResponseEntity<MyWorkspacesResponse> readMyWorkspaces(
            final MemberAuthInfo memberAuthInfo,
            @RequestParam(required = false) final Long cursor,
            @RequestParam(defaultValue = "10") final int size
    ) {
        final var response = meService.readMyWorkspaces(memberAuthInfo.id(), cursor, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/saved-categories")
    public ResponseEntity<MySavedCategoriesResponse> readMySavedCategories(
            final MemberAuthInfo memberAuthInfo,
            @RequestParam(required = false) final Long cursor,
            @RequestParam(defaultValue = "10") final int size
    ) {
        final var response = meService.readMySavedCategories(memberAuthInfo.id(), cursor, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/shared-categories")
    public ResponseEntity<MySharedCategoriesResponse> readMySharedCategories(
            final MemberAuthInfo memberAuthInfo,
            @RequestParam(required = false) final Long cursor,
            @RequestParam(defaultValue = "10") final int size
    ) {
        final var response = meService.readMySharedCategories(memberAuthInfo.id(), cursor, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/saved-categories/contains")
    public ResponseEntity<ForkedSharedCategoryIdsResponse> readForkedSharedCategoryIds(
            final MemberAuthInfo memberAuthInfo,
            @RequestParam final List<Long> sharedCategoryIds
    ) {
        final var response = meService.readForkedSharedCategoryIds(memberAuthInfo.id(), sharedCategoryIds);

        return ResponseEntity.ok(response);
    }
}
