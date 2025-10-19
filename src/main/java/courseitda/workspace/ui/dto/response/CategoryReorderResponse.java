package courseitda.workspace.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.workspace.application.dto.response.ReorderCategoryResponse;
import java.util.List;

public record CategoryReorderResponse(
        @JsonProperty("categories") List<CategorySequenceResponse> categorySequenceResponses
) {
    public static CategoryReorderResponse from(final ReorderCategoryResponse response) {
        return new CategoryReorderResponse(
                response.categorySequenceResponses().stream()
                        .map(CategorySequenceResponse::from)
                        .toList()
        );
    }
}
