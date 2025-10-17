package courseitda.placesearch.infrastructure.kakao.dto.request;

import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;

public record KakaoPlaceSearchRequest(
        String query,
        Integer size
) {

    public KakaoPlaceSearchRequest {
        validateQuery(query);
        validateSize(size);
    }

    private void validateQuery(final String query) {
        if (query == null || query.isBlank()) {
            throw new BusinessException(ErrorCode.PLACE_SEARCH_KEYWORD_EMPTY);
        }
    }

    private void validateSize(final Integer size) {
        if (size == null || size < 1 || size > 15) {
            throw new BusinessException(ErrorCode.INVALID_PLACE_SEARCH_SIZE);
        }
    }
}
