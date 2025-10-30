package courseitda.common.exception;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleException(final Exception e) {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        problemDetail.setDetail(e.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(final BusinessException e) {
        return e.getErrorCode().toProblemDetail();
    }

    /*
     * 요청 DTO에서 validation 어노테이션 검증에 실패했을 때 동작하는 예외 처리기입니다.
     * 응답 JSON 예시
     * {
     *     "type": "about:blank",
     *     "title": "Bad Request",
     *     "status": 400,
     *     "detail": "요청 데이터 검증에 실패했습니다.",
     *     "code": "0001",
     *     "fieldErrors": {
     *         "nickname": "공백일 수 없습니다",
     *         "email": "공백일 수 없습니다",
     *     }
     *  }
     * */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleValidationException(final MethodArgumentNotValidException e) {
        final ProblemDetail problemDetail = ErrorCode.REQUEST_VALIDATION_FAILED.toProblemDetail();

        final Map<String, String> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), "유효하지 않은 값입니다."),
                        (existing, replacement) -> existing
                ));

        problemDetail.setProperty("fieldErrors", fieldErrors);
        return problemDetail;
    }
}
