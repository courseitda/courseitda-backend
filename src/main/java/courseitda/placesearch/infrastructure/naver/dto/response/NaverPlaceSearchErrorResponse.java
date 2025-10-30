package courseitda.placesearch.infrastructure.naver.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NaverPlaceSearchErrorResponse(
        @JsonProperty("errorMessage") String message,
        @JsonProperty("errorCode") String code
) {
}
