package courseitda.workspace.ui.dto.response;

import courseitda.workspace.domain.CategoryPlace;
import java.util.List;

public record CategoryPlacesFindResponse(
        List<CategoryPlaceResponse> categoryPlaceResponses
) {

    public static CategoryPlacesFindResponse of(
            final List<CategoryPlace> categoryPlaces,
            final CategoryPlace representativePlace
    ) {
        return new CategoryPlacesFindResponse(
                categoryPlaces.stream()
                        .map(place -> CategoryPlaceResponse.from(
                                place,
                                representativePlace != null && place.getId().equals(representativePlace.getId())
                        ))
                        .toList()
        );
    }

    public record CategoryPlaceResponse(
            Long id,
            String name,
            String addressName,
            String roadAddressName,
            double latitude,
            double longitude,
            boolean isRepresentative
    ) {

        public static CategoryPlaceResponse from(
                final CategoryPlace categoryPlace,
                final boolean isRepresentative
        ) {
            return new CategoryPlaceResponse(
                    categoryPlace.getId(),
                    categoryPlace.getPlace().getName(),
                    categoryPlace.getPlace().getAddressName(),
                    categoryPlace.getPlace().getRoadAddressName(),
                    categoryPlace.getPlace().getLatitude(),
                    categoryPlace.getPlace().getLongitude(),
                    isRepresentative
            );
        }
    }
}
