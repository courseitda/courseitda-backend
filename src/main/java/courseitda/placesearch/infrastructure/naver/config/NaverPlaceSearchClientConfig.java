package courseitda.placesearch.infrastructure.naver.config;

import courseitda.placesearch.infrastructure.naver.NaverPlaceSearchRestClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class NaverPlaceSearchClientConfig {

    private static final String BASE_URL = "https://openapi.naver.com";

    private final String naverClientId;
    private final String naverClientSecret;
    private final NaverPlaceSearchErrorHandler naverPlaceSearchErrorHandler;

    public NaverPlaceSearchClientConfig(
            @Value("${naver.api.client-id}") final String naverClientId,
            @Value("${naver.api.client-secret}") final String naverClientSecret,
            final NaverPlaceSearchErrorHandler naverPlaceSearchErrorHandler
    ) {
        this.naverClientId = naverClientId;
        this.naverClientSecret = naverClientSecret;
        this.naverPlaceSearchErrorHandler = naverPlaceSearchErrorHandler;
    }

    @Bean
    public NaverPlaceSearchRestClient naverLocalApiClient() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(4));

        final RestClient restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(BASE_URL)
                .defaultHeader("X-Naver-Client-Id", naverClientId)
                .defaultHeader("X-Naver-Client-Secret", naverClientSecret)
                .defaultStatusHandler(naverPlaceSearchErrorHandler)
                .build();

        return new NaverPlaceSearchRestClient(restClient);
    }

}
