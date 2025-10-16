package courseitda.workspace.ui.dto.response;

import courseitda.workspace.domain.Workspace;

public record WorkspaceUpdateResponse(
        String identifier,
        String title
) {

    public static WorkspaceUpdateResponse from(final Workspace workspace) {
        return new WorkspaceUpdateResponse(
                workspace.getIdentifier(),
                workspace.getTitle()
        );
    }
}
