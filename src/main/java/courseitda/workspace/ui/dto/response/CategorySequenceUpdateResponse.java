package courseitda.workspace.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.workspace.domain.Category;
import java.util.List;

public record CategorySequenceUpdateResponse(
        @JsonProperty("categories") List<CategorySequenceResponse> categorySequenceResponses
) {

    public static CategorySequenceUpdateResponse from(final List<Category> categories) {
        return new CategorySequenceUpdateResponse(
                categories.stream()
                        .map(CategorySequenceResponse::from)
                        .toList()
        );
    }

    public record CategorySequenceResponse(
            Long id,
            Integer sequence
    ) {

        public static CategorySequenceResponse from(final Category category) {
            return new CategorySequenceResponse(
                    category.getId(),
                    category.getSequence()
            );
        }
    }
}
