package courseitda.community.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.community.domain.SharedCategory;
import java.util.List;

public record SharedCategorySearchResponse(
        @JsonProperty("sharedCategories") List<SharedCategoryResponse> sharedCategoryResponses,
        boolean hasNext,
        Long nextCursor
) {

    public static SharedCategorySearchResponse from(
            final List<SharedCategory> sharedCategories,
            final boolean hasNext,
            final Long nextCursor
    ) {
        return new SharedCategorySearchResponse(
                sharedCategories.stream()
                        .map(SharedCategoryResponse::from)
                        .toList(),
                hasNext,
                nextCursor
        );
    }

    public record SharedCategoryResponse(
            Long id,
            String name,
            String authorNickname,
            int placeCount
    ) {

        public static SharedCategoryResponse from(final SharedCategory sharedCategory) {
            return new SharedCategoryResponse(
                    sharedCategory.getId(),
                    sharedCategory.getName(),
                    sharedCategory.getAuthor().getNickname(),
                    sharedCategory.getSharedCategoryPlaces().size()
            );
        }
    }
}
