package courseitda.workspace.ui.dto.request;

import jakarta.validation.constraints.NotBlank;

public record WorkspaceUpdateRequest(
        @NotBlank String title
) {
}
