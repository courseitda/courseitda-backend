package courseitda.workspace.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import courseitda.workspace.application.dto.response.ReadWorkspaceResult;
import java.time.LocalDateTime;

public record WorkspaceReadResponse(
        String identifier,
        String title,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime modifiedAt
) {

    public static WorkspaceReadResponse from(final ReadWorkspaceResult result) {
        return new WorkspaceReadResponse(
                result.identifier(),
                result.title(),
                result.modifiedAt()
        );
    }
}
