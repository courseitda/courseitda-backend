package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.Workspace;
import java.time.LocalDateTime;

public record CreateWorkspaceResult(
        String identifier,
        String title,
        LocalDateTime modifiedAt
) {

    public static CreateWorkspaceResult from(final Workspace workspace) {
        return new CreateWorkspaceResult(
                workspace.getIdentifier(),
                workspace.getTitle(),
                workspace.getModifiedAt()
        );
    }
}
