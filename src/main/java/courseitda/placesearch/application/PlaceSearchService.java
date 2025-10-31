package courseitda.placesearch.application;

import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.placesearch.domain.PlaceSearcher;
import courseitda.placesearch.domain.SearchedPlace;
import courseitda.placesearch.ui.dto.response.SearchedPlacesResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceSearchService {

    private static final int SEARCH_SIZE = 5;
    private final PlaceSearcher placeSearcher;

    public SearchedPlacesResponse search(final String query) {
        validateQuery(query);

        final List<SearchedPlace> searchedPlaces = placeSearcher.searchPlaces(query, SEARCH_SIZE);

        return SearchedPlacesResponse.from(searchedPlaces);
    }

    private void validateQuery(final String query) {
        if (query == null || query.isBlank()) {
            throw new BusinessException(ErrorCode.PLACE_SEARCH_KEYWORD_EMPTY);
        }
    }
}
