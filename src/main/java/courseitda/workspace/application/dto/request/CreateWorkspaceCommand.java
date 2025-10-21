package courseitda.workspace.application.dto.request;

import courseitda.member.domain.Member;

public record CreateWorkspaceCommand(
        Member member,
        String title
) {
}
