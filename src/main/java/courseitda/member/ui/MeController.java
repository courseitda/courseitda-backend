package courseitda.member.ui;

import static courseitda.auth.domain.AuthRole.MEMBER;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.auth.domain.RequiresRole;
import courseitda.member.domain.Member;
import courseitda.member.ui.dto.response.MemberDropdownResponse;
import courseitda.member.ui.dto.response.MemberNavigatorResponse;
import courseitda.member.ui.dto.response.MemberProfileResponse;
import courseitda.member.ui.dto.response.MyWorkspacesResponse;
import courseitda.workspace.application.WorkspaceService;
import courseitda.workspace.application.dto.request.FindWorkspacesByMemberIdCommand;
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
    // TODO: 페이징 고려 필요
    public ResponseEntity<MyWorkspacesResponse> readMyWorkspaces(
            final MemberAuthInfo memberAuthInfo
    ) {
        final var response = workspaceService.readWorkspacesByMemberId(
                new FindWorkspacesByMemberIdCommand(memberAuthInfo.id())
        );

        return ResponseEntity.ok(MyWorkspacesResponse.from(response));
    }
}
