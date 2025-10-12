package courseitda.workspace.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.workspace.domain.Category;
import java.util.List;

public record CategoryReorderResponse(
        @JsonProperty("categories") List<CategorySequenceResponse> categorySequenceResponses
) {
    public static CategoryReorderResponse from(final List<Category> categories) {
        return new CategoryReorderResponse(
                categories.stream()
                        .map(CategorySequenceResponse::from)
                        .toList()
        );
    }
}
