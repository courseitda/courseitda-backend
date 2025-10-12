package courseitda.placesearch.application;

import courseitda.placesearch.domain.PlaceSearcher;
import courseitda.placesearch.domain.SearchedPlace;
import courseitda.placesearch.ui.dto.response.SearchedPlacesResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceSearchService {

    private static final int SEARCH_SIZE = 10;
    private final PlaceSearcher placeSearcher;

    public SearchedPlacesResponse search(final String query) {
        final List<SearchedPlace> searchedPlaces = placeSearcher.searchPlaces(query, SEARCH_SIZE);

        return SearchedPlacesResponse.from(searchedPlaces);
    }
}
