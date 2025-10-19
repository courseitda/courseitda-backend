package courseitda.workspace.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import courseitda.workspace.domain.Workspace;
import java.time.LocalDateTime;
import java.util.List;

public record ReadWorkspacesResponse(
        List<WorkspaceResponse> workspaces
) {

    public static ReadWorkspacesResponse from(final List<Workspace> workspaces) {
        final List<WorkspaceResponse> workspaceResponses = workspaces.stream()
                .map(WorkspaceResponse::from)
                .toList();

        return new ReadWorkspacesResponse(workspaceResponses);
    }

    public record WorkspaceResponse(
            String identifier,
            String title,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime modifiedAt
    ) {

        public static WorkspaceResponse from(final Workspace workspace) {
            return new WorkspaceResponse(
                    workspace.getIdentifier(),
                    workspace.getTitle(),
                    workspace.getModifiedAt()
            );
        }
    }
}
