package courseitda.member.ui;

import static courseitda.auth.domain.AuthRole.MEMBER;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.auth.domain.RequiresRole;
import courseitda.member.domain.Member;
import courseitda.member.ui.dto.response.MemberReadDropdownResponse;
import courseitda.member.ui.dto.response.MemberReadNavigatorResponse;
import courseitda.member.ui.dto.response.MemberReadProfileResponse;
import courseitda.workspace.application.WorkspaceService;
import courseitda.workspace.application.dto.request.ReadWorkspacesByMemberIdCommand;
import courseitda.workspace.ui.dto.response.WorkspacesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me")
@RequiresRole(authRoles = MEMBER)
public class MeController {

    private final WorkspaceService workspaceService;

    @GetMapping("/navigator")
    public ResponseEntity<MemberReadNavigatorResponse> readMemberNavigator(
            final Member member
    ) {
        final var response = MemberReadNavigatorResponse.from(member);

        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/dropdown")
    public ResponseEntity<MemberReadDropdownResponse> readMemberDropdown(
            final Member member
    ) {
        final var response = MemberReadDropdownResponse.from(member);

        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<MemberReadProfileResponse> readMemberProfile(
            final Member member
    ) {
        final var response = MemberReadProfileResponse.from(member);

        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/workspaces")
    // TODO: 페이징 고려 필요
    public ResponseEntity<WorkspacesResponse> readMyWorkspaces(final MemberAuthInfo memberAuthInfo) {
        final var response = workspaceService.readWorkspacesByMemberId(
                new ReadWorkspacesByMemberIdCommand(memberAuthInfo.id())
        );

        return ResponseEntity.ok(WorkspacesResponse.from(response));
    }
}
