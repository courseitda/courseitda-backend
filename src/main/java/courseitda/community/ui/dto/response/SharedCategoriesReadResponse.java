package courseitda.community.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.community.domain.SharedCategory;
import java.time.LocalDateTime;
import java.util.List;

public record SharedCategoriesReadResponse(
        @JsonProperty("sharedCategories") List<SharedCategoryResponse> sharedCategoryResponses,
        boolean hasNext,
        Long nextCursor
) {

    public static SharedCategoriesReadResponse from(final List<SharedCategory> sharedCategories,
            final boolean hasNext,
            final Long nextCursor) {
        return new SharedCategoriesReadResponse(
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
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss+09:00") LocalDateTime createdAt,
            int placeCount,
            int likeCount
    ) {

        public static SharedCategoryResponse from(final SharedCategory sharedCategory) {
            return new SharedCategoryResponse(
                    sharedCategory.getId(),
                    sharedCategory.getName(),
                    sharedCategory.getAuthor().getNickname(),
                    sharedCategory.getCreatedAt(),
                    sharedCategory.getSharedCategoryPlaces().size(),
                    sharedCategory.getSharedCategoryLikes().size()
            );
        }
    }
}
