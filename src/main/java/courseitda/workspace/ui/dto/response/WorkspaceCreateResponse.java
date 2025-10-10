package courseitda.workspace.ui.dto.response;

import courseitda.workspace.domain.Workspace;

public record WorkspaceCreateResponse(
        Long id,
        Long memberId,
        String title
) {

    public static WorkspaceCreateResponse from(Workspace workspace) {
        return new WorkspaceCreateResponse(
                workspace.getId(),
                workspace.getMember().getId(),
                workspace.getTitle()
        );
    }
}
