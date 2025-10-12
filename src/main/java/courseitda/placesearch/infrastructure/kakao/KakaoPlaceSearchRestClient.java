package courseitda.placesearch.infrastructure.kakao;

import courseitda.placesearch.infrastructure.kakao.dto.request.KakaoPlaceSearchRequest;
import courseitda.placesearch.infrastructure.kakao.dto.response.KakaoPlaceSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
public class KakaoPlaceSearchRestClient {

    private final RestClient restClient;

    public KakaoPlaceSearchResponse search(
            final KakaoPlaceSearchRequest kakaoPlaceSearchRequest
    ) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/search/keyword.json")
                        .queryParam("query", kakaoPlaceSearchRequest.query())
                        .queryParam("size", kakaoPlaceSearchRequest.size())
                        .build()
                )
                .retrieve()
                .body(KakaoPlaceSearchResponse.class);
    }
}
