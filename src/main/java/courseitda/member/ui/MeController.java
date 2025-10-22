package courseitda.member.ui;

import static courseitda.auth.domain.AuthRole.MEMBER;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.auth.domain.RequiresRole;
import courseitda.member.domain.Member;
import courseitda.member.ui.dto.response.MemberDropdownReadResponse;
import courseitda.member.ui.dto.response.MemberNavigatorReadResponse;
import courseitda.member.ui.dto.response.MemberProfileReadResponse;
import courseitda.member.ui.dto.response.MyWorkspacesReadResponse;
import courseitda.workspace.application.WorkspaceService;
import courseitda.workspace.application.dto.request.ReadWorkspacesByMemberIdCommand;
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
    public ResponseEntity<MemberNavigatorReadResponse> readMemberNavigator(
            final Member member
    ) {
        final var response = MemberNavigatorReadResponse.from(member);

        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/dropdown")
    public ResponseEntity<MemberDropdownReadResponse> readMemberDropdown(
            final Member member
    ) {
        final var response = MemberDropdownReadResponse.from(member);

        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<MemberProfileReadResponse> readMemberProfile(
            final Member member
    ) {
        final var response = MemberProfileReadResponse.from(member);

        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/workspaces")
    // TODO: 페이징 고려 필요
    public ResponseEntity<MyWorkspacesReadResponse> readMyWorkspaces(
            final MemberAuthInfo memberAuthInfo
    ) {
        final var response = workspaceService.readWorkspacesByMemberId(
                new ReadWorkspacesByMemberIdCommand(memberAuthInfo.id())
        );

        return ResponseEntity.ok(MyWorkspacesReadResponse.from(response));
    }
}
