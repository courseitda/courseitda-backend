package courseitda.member.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.workspace.domain.Workspace;
import java.time.LocalDateTime;
import java.util.List;

public record MyWorkspacesResponse(
        List<WorkspaceResponse> workspaces
) {

    public static MyWorkspacesResponse from(final List<Workspace> workspaces) {
        final List<WorkspaceResponse> workspaceResponses = workspaces.stream()
                .map(WorkspaceResponse::from)
                .toList();

        return new MyWorkspacesResponse(workspaceResponses);
    }

    public record WorkspaceResponse(
            String identifier,
            String title,
            @JsonProperty("modifiedAt") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss+09:00") LocalDateTime lastActivityAt
    ) {

        public static WorkspaceResponse from(final Workspace workspace) {
            return new WorkspaceResponse(
                    workspace.getIdentifier(),
                    workspace.getTitle(),
                    workspace.getLastActivityAt()
            );
        }
    }
}
