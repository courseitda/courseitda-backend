package courseitda.placesearch.infrastructure.kakao.config;

import courseitda.placesearch.infrastructure.kakao.KakaoPlaceSearchRestClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class KakaoPlaceSearchClientConfig {

    private final String kakaoApiKey;
    private final KakaoPlaceSearchErrorHandler kakaoPlaceSearchErrorHandler;

    public KakaoPlaceSearchClientConfig(
            @Value("${kakao.api.key}") final String kakaoApiKey,
            final KakaoPlaceSearchErrorHandler kakaoPlaceSearchErrorHandler
    ) {
        this.kakaoApiKey = kakaoApiKey;
        this.kakaoPlaceSearchErrorHandler = kakaoPlaceSearchErrorHandler;
    }

    @Bean
    public KakaoPlaceSearchRestClient kakaoLocalApiClient() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(4));

        final RestClient restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader("Authorization", "KakaoAK " + kakaoApiKey)
                .defaultStatusHandler(kakaoPlaceSearchErrorHandler)
                .build();

        return new KakaoPlaceSearchRestClient(restClient);
    }
}
