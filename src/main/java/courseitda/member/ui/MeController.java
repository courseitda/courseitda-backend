package courseitda.member.ui;

import static courseitda.auth.domain.AuthRole.MEMBER;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.auth.domain.RequiresRole;
import courseitda.member.domain.Member;
import courseitda.member.ui.dto.response.MemberReadDropdownResponse;
import courseitda.member.ui.dto.response.MemberReadNavigatorResponse;
import courseitda.member.ui.dto.response.MemberReadProfileResponse;
import courseitda.workspace.application.WorkspaceService;
import courseitda.workspace.ui.dto.response.WorkspacesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me")
public class MeController {

    private final WorkspaceService workspaceService;

    @GetMapping("/navigator")
    @RequiresRole(authRoles = MEMBER)
    public ResponseEntity<MemberReadNavigatorResponse> readMemberNavigator(
            final Member member
    ) {
        final var response = MemberReadNavigatorResponse.from(member);

        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/dropdown")
    @RequiresRole(authRoles = MEMBER)
    public ResponseEntity<MemberReadDropdownResponse> readMemberDropdown(
            final Member member
    ) {
        final var response = MemberReadDropdownResponse.from(member);

        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/profile")
    @RequiresRole(authRoles = MEMBER)
    public ResponseEntity<MemberReadProfileResponse> readMemberProfile(
            final Member member
    ) {
        final var response = MemberReadProfileResponse.from(member);

        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/workspaces")
    @RequiresRole(authRoles = MEMBER)
    // TODO: 페이징 고려 필요
    public ResponseEntity<WorkspacesResponse> readMyWorkspaces(final MemberAuthInfo memberAuthInfo) {
        final var workspacesResponse = workspaceService.readWorkspacesByMemberId(memberAuthInfo.id());

        return ResponseEntity.ok(workspacesResponse);
    }
}
