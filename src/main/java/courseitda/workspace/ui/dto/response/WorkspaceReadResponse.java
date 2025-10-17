package courseitda.workspace.ui.dto.response;

import courseitda.workspace.domain.Workspace;

public record WorkspaceReadResponse(
        String identifier,
        String title
) {

    public static WorkspaceReadResponse from(final Workspace workspace) {
        return new WorkspaceReadResponse(
                workspace.getIdentifier(),
                workspace.getTitle()
        );
    }
}
