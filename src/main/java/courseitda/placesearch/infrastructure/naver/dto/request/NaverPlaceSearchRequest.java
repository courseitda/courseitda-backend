package courseitda.placesearch.infrastructure.naver.dto.request;

import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;

public record NaverPlaceSearchRequest(
        String query,
        Integer display
) {

    public NaverPlaceSearchRequest {
        validateQuery(query);
        validateDisplay(display);
    }

    private void validateQuery(final String query) {
        if (query == null || query.isBlank()) {
            throw new BusinessException(ErrorCode.PLACE_SEARCH_KEYWORD_EMPTY);
        }
    }

    private void validateDisplay(final Integer display) {
        if (display == null || display < 1 || display > 15) {
            throw new BusinessException(ErrorCode.INVALID_PLACE_SEARCH_SIZE);
        }
    }
}
