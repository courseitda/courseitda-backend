package courseitda.placesearch.infrastructure.kakao.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import courseitda.exception.BusinessException;
import courseitda.exception.ErrorCode;
import courseitda.placesearch.infrastructure.kakao.dto.response.KakaoPlaceSearchErrorResponse;
import java.io.IOException;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoPlaceSearchErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    @Override
    public boolean hasError(@NonNull final ClientHttpResponse response) {
        try {
            final HttpStatusCode statusCode = response.getStatusCode();

            return statusCode.is4xxClientError() || statusCode.is5xxServerError();
        } catch (final IOException ioException) {
            throw new BusinessException(ErrorCode.TEMPORARY_ERROR);
        }
    }

    @Override
    public void handleError(
            @NonNull final URI url,
            @NonNull final HttpMethod method,
            final ClientHttpResponse response
    ) throws IOException {
        log.warn(
                "카카오 장소 검색 API 오류 발생: {} ",
                objectMapper.readValue(response.getBody(), KakaoPlaceSearchErrorResponse.class).message()
        );
        throw new BusinessException(ErrorCode.TEMPORARY_ERROR);
    }
}
