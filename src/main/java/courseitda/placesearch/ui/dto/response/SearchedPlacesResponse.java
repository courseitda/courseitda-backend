package courseitda.placesearch.ui.dto.response;

import courseitda.placesearch.domain.SearchedPlace;
import java.util.List;

public record SearchedPlacesResponse(
        List<SearchedPlaceResponse> searchedPlaces
) {

    public static SearchedPlacesResponse from(final List<SearchedPlace> searchedPlaces) {
        return new SearchedPlacesResponse(
                searchedPlaces.stream()
                        .map(SearchedPlaceResponse::from)
                        .toList()
        );
    }

    public record SearchedPlaceResponse(
            String name,
            String roadAddressName,
            String addressName,
            Double latitude,
            Double longitude
    ) {

        public static SearchedPlaceResponse from(final SearchedPlace searchedPlace) {
            return new SearchedPlaceResponse(
                    searchedPlace.name(),
                    searchedPlace.roadAddressName(),
                    searchedPlace.addressName(),
                    searchedPlace.latitude(),
                    searchedPlace.longitude()
            );
        }
    }
}
