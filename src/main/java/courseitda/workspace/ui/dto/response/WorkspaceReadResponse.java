package courseitda.workspace.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.workspace.domain.Workspace;
import java.time.LocalDateTime;

public record WorkspaceReadResponse(
        String identifier,
        String title,
        @JsonProperty("modifiedAt") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss+09:00") LocalDateTime lastActivityAt
) {

    public static WorkspaceReadResponse from(final Workspace workspace) {
        return new WorkspaceReadResponse(
                workspace.getIdentifier(),
                workspace.getTitle(),
                workspace.getLastActivityAt()
        );
    }
}
