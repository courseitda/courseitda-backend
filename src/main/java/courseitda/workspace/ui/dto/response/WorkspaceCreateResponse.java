package courseitda.workspace.ui.dto.response;

import courseitda.workspace.domain.Workspace;

public record WorkspaceCreateResponse(
        String identifier,
        String title
) {

    public static WorkspaceCreateResponse from(final Workspace workspace) {
        return new WorkspaceCreateResponse(
                workspace.getIdentifier(),
                workspace.getTitle()
        );
    }
}
