package courseitda.category.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.category.domain.Category;
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
