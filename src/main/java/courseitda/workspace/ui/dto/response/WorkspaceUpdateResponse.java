package courseitda.workspace.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import courseitda.workspace.application.dto.response.UpdateWorkspaceResult;
import java.time.LocalDateTime;

public record WorkspaceUpdateResponse(
        String identifier,
        String title,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime modifiedAt
) {

    public static WorkspaceUpdateResponse from(final UpdateWorkspaceResult result) {
        return new WorkspaceUpdateResponse(
                result.identifier(),
                result.title(),
                result.modifiedAt()
        );
    }
}
