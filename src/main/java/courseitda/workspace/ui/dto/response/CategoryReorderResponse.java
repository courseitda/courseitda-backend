package courseitda.workspace.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.workspace.application.dto.response.UpdateCategorySequenceResult;
import java.util.List;

public record CategoryReorderResponse(
        @JsonProperty("categories") List<CategorySequenceResponse> categorySequenceResponses
) {

    public static CategoryReorderResponse from(final UpdateCategorySequenceResult result) {
        return new CategoryReorderResponse(
                result.categorySequenceResults().stream()
                        .map(CategorySequenceResponse::from)
                        .toList()
        );
    }
}
