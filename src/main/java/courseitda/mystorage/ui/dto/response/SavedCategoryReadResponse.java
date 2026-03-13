package courseitda.mystorage.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.mystorage.domain.SavedCategory;
import courseitda.mystorage.domain.SavedCategoryPlace;
import java.util.List;

public record SavedCategoryReadResponse(
        Long id,
        String name,
        Long sourceSharedCategoryId,
        boolean canPublish,
        @JsonProperty("savedCategoryPlaces") List<SavedCategoryPlaceResponse> savedCategoryPlaceResponses
) {

    public static SavedCategoryReadResponse from(final SavedCategory savedCategory, final boolean canPublish) {
        return new SavedCategoryReadResponse(
                savedCategory.getId(),
                savedCategory.getName(),
                savedCategory.getSourceSharedCategoryId(),
                canPublish,
                savedCategory.getSavedCategoryPlaces().stream()
                        .map(SavedCategoryPlaceResponse::from)
                        .toList()
        );
    }

    public record SavedCategoryPlaceResponse(
            Long id,
            String name,
            String placeUrl,
            String roadAddressName,
            String addressName,
            double latitude,
            double longitude
    ) {

        public static SavedCategoryPlaceResponse from(final SavedCategoryPlace savedCategoryPlace) {
            return new SavedCategoryPlaceResponse(
                    savedCategoryPlace.getId(),
                    savedCategoryPlace.getPlace().getName(),
                    savedCategoryPlace.getPlace().getPlaceUrl(),
                    savedCategoryPlace.getPlace().getRoadAddressName(),
                    savedCategoryPlace.getPlace().getAddressName(),
                    savedCategoryPlace.getPlace().getLatitude(),
                    savedCategoryPlace.getPlace().getLongitude()
            );
        }
    }
}
