package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.Workspace;

public record UpdateWorkspaceResult(
        String identifier,
        String title
) {

    public static UpdateWorkspaceResult from(final Workspace workspace) {
        return new UpdateWorkspaceResult(
                workspace.getIdentifier(),
                workspace.getTitle()
        );
    }
}
