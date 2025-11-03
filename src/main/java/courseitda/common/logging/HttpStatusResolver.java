package courseitda.common.logging;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class HttpStatusResolver {

    // 정상 응답 시 상태 코드를 추출
    // 1) HttpServletResponse에 이미 설정된 값이 있으면 우선 사용
    // 2) ResponseEntity로 반환된 경우 그 안의 상태 코드 사용
    // 3) 아무 것도 없으면 기본값 200(OK) 반환
    public int resolveStatus(final HttpServletResponse response, final Object result) {
        if (response != null && response.getStatus() > 0) {
            return response.getStatus(); // 실제 HTTP 응답 코드
        }
        if (result instanceof ResponseEntity<?> entity) {
            return entity.getStatusCode().value(); // ResponseEntity의 상태 코드
        }
        return HttpStatus.OK.value(); // 기본값
    }

    // 예외 발생 시 상태 코드를 추출
    // Spring 에서 자주 사용하는 예외 타입들에 따라 우선순위를 두고 처리
    public int resolveStatusFromException(final Throwable t, final HttpServletResponse response) {
        // ResponseStatusException: @ResponseStatus 기반 예외
        if (t instanceof ResponseStatusException rse) {
            return rse.getStatusCode().value();
        }
        // HttpStatusCodeException: RestTemplate 등 외부 요청 실패 시 발생
        if (t instanceof HttpStatusCodeException httpStatusCodeException) {
            return httpStatusCodeException.getStatusCode().value();
        }
        // BindException: 요청 파라미터 바인딩 오류 (주로 400)
        if (t instanceof BindException) {
            return HttpStatus.BAD_REQUEST.value();
        }
        // 응답 객체에 이미 4xx 이상 코드가 있다면 그대로 사용
        if (response != null && response.getStatus() >= 400) {
            return response.getStatus();
        }
        // 위에 해당하지 않으면 서버 내부 오류로 간주 (500)
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    // 정상 응답 객체(result)에서 에러 메시지 추출
    // 주로 4xx 상태 코드로 처리된 에러 응답에서 메시지를 가져올 때 사용
    public String getErrorMessageFromResult(final Object result) {
        if (result == null) {
            return "";
        }

        // ResponseEntity<ProblemDetail> 형태인 경우
        if (result instanceof ResponseEntity<?> entity) {
            final var body = entity.getBody();
            if (body instanceof org.springframework.http.ProblemDetail problemDetail) {
                final String detail = problemDetail.getDetail();
                return detail != null ? detail : "";
            }
        }

        // ProblemDetail 형태인 경우
        if (result instanceof org.springframework.http.ProblemDetail problemDetail) {
            final String detail = problemDetail.getDetail();
            return detail != null ? detail : "";
        }

        return "";
    }

    // 예외 객체에서 메시지를 안전하게 추출
    // message가 없을 경우 예외 클래스 이름을 대신 사용
    public String getErrorMessage(final Throwable t) {
        final var msg = t.getMessage();
        if (msg != null && !msg.isBlank()) {
            return msg;
        }
        return t.getClass().getSimpleName();
    }
}
