package courseitda.mystorage.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.mystorage.domain.SavedCategoryPlace;
import java.util.List;

public record SavedCategoryPlaceCreateResponse(
        @JsonProperty("savedCategoryPlaces") List<SavedCategoryPlaceResponse> savedCategoryPlaceResponses
) {

    public static SavedCategoryPlaceCreateResponse from(final List<SavedCategoryPlace> savedCategoryPlaces) {
        return new SavedCategoryPlaceCreateResponse(
                savedCategoryPlaces.stream()
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
