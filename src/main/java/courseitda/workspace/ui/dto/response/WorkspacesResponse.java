package courseitda.workspace.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import courseitda.workspace.application.dto.response.ReadWorkspacesByMemberIdResult;
import java.time.LocalDateTime;
import java.util.List;

public record WorkspacesResponse(
        List<WorkspaceResponse> workspaces
) {

    public static WorkspacesResponse from(final ReadWorkspacesByMemberIdResult response) {
        final List<WorkspaceResponse> workspaceResponses = response.workspaces().stream()
                .map(WorkspaceResponse::from)
                .toList();

        return new WorkspacesResponse(workspaceResponses);
    }

    public record WorkspaceResponse(
            String identifier,
            String title,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime modifiedAt
    ) {

        public static WorkspaceResponse from(final ReadWorkspacesByMemberIdResult.WorkspaceResponse response) {
            return new WorkspaceResponse(
                    response.identifier(),
                    response.title(),
                    response.modifiedAt()
            );
        }
    }
}
