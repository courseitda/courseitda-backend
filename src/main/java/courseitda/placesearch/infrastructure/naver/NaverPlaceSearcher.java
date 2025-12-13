package courseitda.placesearch.infrastructure.naver;

import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.placesearch.domain.PlaceSearcher;
import courseitda.placesearch.domain.SearchedPlace;
import courseitda.placesearch.infrastructure.naver.dto.request.NaverPlaceSearchRequest;
import courseitda.placesearch.infrastructure.naver.dto.response.NaverPlaceSearchResponse.NaverPlaceItem;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverPlaceSearcher implements PlaceSearcher {

    private final NaverPlaceSearchRestClient naverPlaceSearchRestClient;

    @Override
    public List<SearchedPlace> searchPlaces(String keyword, Integer size) {
        final var naverPlaceSearchResponse = naverPlaceSearchRestClient.search(
                new NaverPlaceSearchRequest(keyword, size)
        );

        if (naverPlaceSearchResponse == null) {
            throw new BusinessException(ErrorCode.NAVER_PLACE_SEARCH_RESPONSE_NULL);
        }

        return Arrays.stream(naverPlaceSearchResponse.items()).toList()
                .stream()
                .map(NaverPlaceItem::toSearchedPlace)
                .toList();
    }
}
