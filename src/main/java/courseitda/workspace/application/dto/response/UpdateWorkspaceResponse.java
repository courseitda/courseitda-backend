package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.Workspace;

public record UpdateWorkspaceResponse(
        String identifier,
        String title
) {

    public static UpdateWorkspaceResponse from(final Workspace workspace) {
        return new UpdateWorkspaceResponse(
                workspace.getIdentifier(),
                workspace.getTitle()
        );
    }
}
