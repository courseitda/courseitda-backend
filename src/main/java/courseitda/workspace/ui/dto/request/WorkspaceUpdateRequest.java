package courseitda.workspace.ui.dto.request;

import courseitda.workspace.application.dto.request.UpdateWorkspaceCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WorkspaceUpdateRequest(
        @NotBlank(message = "워크스페이스 이름은 비어 있을 수 없습니다.") @Size(max = 20, message = "워크스페이스 이름은 20자를 초과할 수 없습니다.") String title
) {

    public UpdateWorkspaceCommand toCommand() {
        return new UpdateWorkspaceCommand(title);
    }
}
