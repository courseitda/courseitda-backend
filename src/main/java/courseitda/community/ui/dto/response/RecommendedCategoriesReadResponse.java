package courseitda.community.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.community.domain.RecommendedCategory;
import java.time.LocalDateTime;
import java.util.List;

public record RecommendedCategoriesReadResponse(
        @JsonProperty("recommendedCategories") List<RecommendedCategoryResponse> recommendedCategoryResponses
) {

    public static RecommendedCategoriesReadResponse from(final List<RecommendedCategory> recommendedCategories) {
        return new RecommendedCategoriesReadResponse(
                recommendedCategories.stream()
                        .map(RecommendedCategoryResponse::from)
                        .toList()
        );
    }

    public record RecommendedCategoryResponse(
            Long id,
            String imageUrl,
            Long sharedCategoryId,
            String name,
            String authorNickname,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss+09:00") LocalDateTime createdAt,
            int placeCount,
            int likeCount
    ) {

        public static RecommendedCategoryResponse from(final RecommendedCategory recommendedCategory) {
            final var sharedCategory = recommendedCategory.getSharedCategory();
            return new RecommendedCategoryResponse(
                    recommendedCategory.getId(),
                    recommendedCategory.getImageUrl(),
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
