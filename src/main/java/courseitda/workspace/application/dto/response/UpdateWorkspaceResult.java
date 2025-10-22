package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.Workspace;
import java.time.LocalDateTime;

public record UpdateWorkspaceResult(
        String identifier,
        String title,
        LocalDateTime modifiedAt
) {

    public static UpdateWorkspaceResult from(final Workspace workspace) {
        return new UpdateWorkspaceResult(
                workspace.getIdentifier(),
                workspace.getTitle(),
                workspace.getModifiedAt()
        );
    }
}
