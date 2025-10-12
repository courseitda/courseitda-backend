package courseitda.placesearch.infrastructure.kakao.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoPlaceSearchErrorResponse(
        @JsonProperty("error_type") String errorType,
        @JsonProperty("message") String message
) {
}
