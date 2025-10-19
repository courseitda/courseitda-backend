package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.Workspace;

public record ReadWorkspaceResponse(
        String identifier,
        String title
) {

    public static ReadWorkspaceResponse from(final Workspace workspace) {
        return new ReadWorkspaceResponse(
                workspace.getIdentifier(),
                workspace.getTitle()
        );
    }
}
