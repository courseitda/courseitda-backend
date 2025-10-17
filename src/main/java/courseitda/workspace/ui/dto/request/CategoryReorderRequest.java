package courseitda.workspace.ui.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.workspace.application.dto.request.CategoryReorderCommand;
import courseitda.workspace.application.dto.request.CategoryReorderCommand.CategorySequenceCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CategoryReorderRequest(
        @NotNull(message = "카테고리 목록은 필수입니다") @NotEmpty(message = "카테고리 목록이 비어있을 수 없습니다") @Valid @JsonProperty("categories") List<CategorySequenceRequest> categorySequenceRequests
) {

    public CategoryReorderCommand toCommand() {
        final List<CategorySequenceCommand> commands = categorySequenceRequests.stream()
                .map(CategorySequenceRequest::toCommand)
                .toList();
        return new CategoryReorderCommand(commands);
    }
}
