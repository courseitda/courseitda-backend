package courseitda.workspace.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import courseitda.workspace.domain.Category;
import courseitda.workspace.domain.CategoryPlace;
import java.util.List;

public record CategoryReadResponse(
        Long id,
        String name,
        String color,
        Integer sequence,
        Long representativePlaceId,
        @JsonProperty("categoryPlaces") CategoryPlacesResponse categoryPlacesResponse
) {

    public static CategoryReadResponse from(final Category category) {
        return new CategoryReadResponse(
                category.getId(),
                category.getName(),
                category.getColor(),
                category.getSequence(),
                category.getRepresentativePlace() != null ? category.getRepresentativePlace().getId() : null,
                CategoryPlacesResponse.from(category.getCategoryPlaces(), category.getRepresentativePlace())
        );
    }

    public record CategoryPlacesResponse(
            @JsonProperty("categoryPlaces") List<CategoryPlaceResponse> categoryPlaceResponses
    ) {

        public static CategoryPlacesResponse from(
                final List<CategoryPlace> categoryPlaces,
                final CategoryPlace representativePlace
        ) {
            return new CategoryPlacesResponse(
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
                String placeUrl,
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
                        categoryPlace.getPlace().getPlaceUrl(),
                        categoryPlace.getPlace().getAddressName(),
                        categoryPlace.getPlace().getRoadAddressName(),
                        categoryPlace.getPlace().getLatitude(),
                        categoryPlace.getPlace().getLongitude(),
                        isRepresentative
                );
            }
        }
    }
}
