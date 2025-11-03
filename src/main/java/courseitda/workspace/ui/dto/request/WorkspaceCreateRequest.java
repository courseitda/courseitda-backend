package courseitda.workspace.ui.dto.request;

import jakarta.validation.constraints.NotBlank;

public record WorkspaceCreateRequest(
        @NotBlank String title
) {
}
