package courseitda.workspace.ui.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.workspace.application.dto.request.UpdateCategorySequenceCommand;
import courseitda.workspace.application.dto.request.UpdateCategorySequenceCommand.CategorySequenceCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CategoryReorderRequest(
        @NotNull(message = "카테고리 목록은 필수입니다") @NotEmpty(message = "카테고리 목록이 비어있을 수 없습니다") @Valid @JsonProperty("categories") List<CategorySequenceRequest> categorySequenceRequests
) {

    public UpdateCategorySequenceCommand toCommand() {
        final List<CategorySequenceCommand> commands = categorySequenceRequests.stream()
                .map(CategorySequenceRequest::toCommand)
                .toList();
        return new UpdateCategorySequenceCommand(commands);
    }

    public record CategorySequenceRequest(
            @NotNull(message = "카테고리 ID는 필수입니다") Long id,

            @NotNull(message = "순서는 필수입니다") @Min(value = 1, message = "순서는 1 이상이어야 합니다") Integer sequence
    ) {

        public CategorySequenceCommand toCommand() {
            return new CategorySequenceCommand(id, sequence);
        }
    }
}
