package courseitda.community.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.community.domain.SharedCategory;
import courseitda.community.domain.SharedCategoryPlace;
import java.time.LocalDateTime;
import java.util.List;

public record SharedCategoryReadResponse(
        Long id,
        String name,
        String authorNickname,
        LocalDateTime createdAt,
        @JsonProperty("sharedCategoryPlaces") List<SharedCategoryPlaceResponse> sharedCategoryPlaceResponses
) {

    public static SharedCategoryReadResponse from(final SharedCategory sharedCategory) {
        return new SharedCategoryReadResponse(
                sharedCategory.getId(),
                sharedCategory.getName(),
                sharedCategory.getAuthor().getNickname(),
                sharedCategory.getCreatedAt(),
                sharedCategory.getSharedCategoryPlaces().stream()
                        .map(SharedCategoryPlaceResponse::from)
                        .toList()
        );
    }

    public record SharedCategoryPlaceResponse(
            Long id,
            String name,
            String placeUrl,
            String roadAddressName,
            String addressName,
            double latitude,
            double longitude
    ) {

        public static SharedCategoryPlaceResponse from(final SharedCategoryPlace sharedCategoryPlace) {
            return new SharedCategoryPlaceResponse(
                    sharedCategoryPlace.getId(),
                    sharedCategoryPlace.getPlace().getName(),
                    sharedCategoryPlace.getPlace().getPlaceUrl(),
                    sharedCategoryPlace.getPlace().getRoadAddressName(),
                    sharedCategoryPlace.getPlace().getAddressName(),
                    sharedCategoryPlace.getPlace().getLatitude(),
                    sharedCategoryPlace.getPlace().getLongitude()
            );
        }
    }
}
