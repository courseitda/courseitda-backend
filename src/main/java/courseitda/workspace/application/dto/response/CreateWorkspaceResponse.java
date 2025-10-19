package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.Workspace;

public record CreateWorkspaceResponse(
        String identifier,
        String title
) {

    public static CreateWorkspaceResponse from(final Workspace workspace) {
        return new CreateWorkspaceResponse(
                workspace.getIdentifier(),
                workspace.getTitle()
        );
    }
}
