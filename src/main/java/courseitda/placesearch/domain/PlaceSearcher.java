package courseitda.placesearch.domain;

import java.util.List;

public interface PlaceSearcher {

    List<SearchedPlace> searchPlaces(String keyword, Integer size);
}
