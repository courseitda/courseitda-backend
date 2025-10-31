package courseitda.placesearch.infrastructure.naver;

import courseitda.placesearch.infrastructure.naver.dto.request.NaverPlaceSearchRequest;
import courseitda.placesearch.infrastructure.naver.dto.response.NaverPlaceSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
public class NaverPlaceSearchRestClient {

    private final RestClient restClient;

    public NaverPlaceSearchResponse search(
            final NaverPlaceSearchRequest naverPlaceSearchRequest
    ) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/search/local.json")
                        .queryParam("query", naverPlaceSearchRequest.query())
                        .queryParam("display", naverPlaceSearchRequest.display())
                        .build()
                )
                .retrieve()
                .body(NaverPlaceSearchResponse.class);
    }
}
