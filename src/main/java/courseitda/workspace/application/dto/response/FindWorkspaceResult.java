package courseitda.workspace.application.dto.response;

import courseitda.workspace.domain.Workspace;
import java.time.LocalDateTime;

public record FindWorkspaceResult(
        String identifier,
        String title,
        LocalDateTime modifiedAt
) {

    public static FindWorkspaceResult from(final Workspace workspace) {
        return new FindWorkspaceResult(
                workspace.getIdentifier(),
                workspace.getTitle(),
                workspace.getModifiedAt()
        );
    }
}
