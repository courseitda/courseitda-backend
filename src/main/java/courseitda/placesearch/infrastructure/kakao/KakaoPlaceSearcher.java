package courseitda.placesearch.infrastructure.kakao;

import courseitda.exception.BusinessException;
import courseitda.exception.ErrorCode;
import courseitda.placesearch.domain.PlaceSearcher;
import courseitda.placesearch.domain.SearchedPlace;
import courseitda.placesearch.infrastructure.kakao.dto.request.KakaoPlaceSearchRequest;
import courseitda.placesearch.infrastructure.kakao.dto.response.KakaoPlaceSearchResponse;
import courseitda.placesearch.infrastructure.kakao.dto.response.KakaoPlaceSearchResponse.Document;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoPlaceSearcher implements PlaceSearcher {

    private final KakaoPlaceSearchRestClient kakaoPlaceSearchRestClient;

    @Override
    public List<SearchedPlace> searchPlaces(final String keyword, final Integer size) {
        final KakaoPlaceSearchResponse kakaoPlaceSearchResponse = kakaoPlaceSearchRestClient.search(
                new KakaoPlaceSearchRequest(keyword, size)
        );

        if (kakaoPlaceSearchResponse == null) {
            throw new BusinessException(ErrorCode.TEMPORARY_ERROR);
        }

        return kakaoPlaceSearchResponse.documents()
                .stream()
                .map(Document::toSearchedPlace)
                .toList();
    }
}
