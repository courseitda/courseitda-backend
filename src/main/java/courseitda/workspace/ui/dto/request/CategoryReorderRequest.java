package courseitda.workspace.ui.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CategoryReorderRequest(
        @NotNull @NotEmpty @Valid @JsonProperty("categories") List<CategorySequenceRequest> categories
) {

    public record CategorySequenceRequest(
            @NotNull Long id,
            @NotNull Integer sequence
    ) {
    }
}
