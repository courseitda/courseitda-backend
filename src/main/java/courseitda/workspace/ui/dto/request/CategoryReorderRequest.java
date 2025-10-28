package courseitda.workspace.ui.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.auth.domain.MemberAuthInfo;
import courseitda.workspace.application.dto.request.UpdateCategorySequenceCommand;
import courseitda.workspace.application.dto.request.UpdateCategorySequenceCommand.CategorySequenceCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CategoryReorderRequest(
        @NotNull @NotEmpty @Valid @JsonProperty("categories") List<CategorySequenceRequest> categorySequenceRequest
) {

    public UpdateCategorySequenceCommand toCommandWith(
            final MemberAuthInfo memberAuthInfo,
            final String workspaceIdentifier
    ) {
        final var categorySequenceCommands = categorySequenceRequest.stream()
                .map(CategorySequenceRequest::toCommand)
                .toList();
        return new UpdateCategorySequenceCommand(memberAuthInfo, workspaceIdentifier, categorySequenceCommands);
    }

    public record CategorySequenceRequest(
            @NotNull Long id,
            @NotNull Integer sequence
    ) {

        public CategorySequenceCommand toCommand() {
            return new CategorySequenceCommand(id, sequence);
        }
    }
}
