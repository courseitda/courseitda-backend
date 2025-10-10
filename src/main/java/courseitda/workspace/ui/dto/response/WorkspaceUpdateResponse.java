package courseitda.workspace.ui.dto.response;

import courseitda.workspace.domain.Workspace;

public record WorkspaceUpdateResponse(
        Long id,
        String title
) {

    public static WorkspaceUpdateResponse from(final Workspace workspace) {
        return new WorkspaceUpdateResponse(
                workspace.getId(), workspace.getTitle()
        );
    }
}
