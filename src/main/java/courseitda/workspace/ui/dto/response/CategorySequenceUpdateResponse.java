package courseitda.workspace.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.workspace.application.dto.response.UpdateCategorySequenceResult;
import courseitda.workspace.application.dto.response.UpdateCategorySequenceResult.CategorySequenceResult;
import java.util.List;

public record CategorySequenceUpdateResponse(
        @JsonProperty("categories") List<CategorySequenceResponse> categorySequenceResponses
) {

    public static CategorySequenceUpdateResponse from(final UpdateCategorySequenceResult result) {
        return new CategorySequenceUpdateResponse(
                result.categorySequenceResults().stream()
                        .map(CategorySequenceResponse::from)
                        .toList()
        );
    }

    public record CategorySequenceResponse(
            Long id,
            Integer sequence
    ) {

        public static CategorySequenceResponse from(final CategorySequenceResult result) {
            return new CategorySequenceResponse(
                    result.id(),
                    result.sequence()
            );
        }
    }
}
