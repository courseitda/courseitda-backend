package courseitda.placesearch.infrastructure.naver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.placesearch.infrastructure.naver.dto.response.NaverPlaceSearchErrorResponse;
import java.io.IOException;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverPlaceSearchErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    @Override
    public boolean hasError(@NonNull final ClientHttpResponse response) {
        try {
            final var statusCode = response.getStatusCode();

            return statusCode.is4xxClientError() || statusCode.is5xxServerError();
        } catch (final IOException ioException) {
            throw new BusinessException(ErrorCode.NAVER_PLACE_SEARCH_STATUS_CHECK_ERROR);
        }
    }

    @Override
    public void handleError(
            @NonNull final URI url,
            @NonNull final HttpMethod method,
            final ClientHttpResponse response
    ) throws IOException {
        log.warn(
                "네이버 장소 검색 API 오류 발생: {} ",
                objectMapper.readValue(response.getBody(), NaverPlaceSearchErrorResponse.class).message()
        );
        throw new BusinessException(ErrorCode.NAVER_PLACE_SEARCH_ERROR);
    }
}
