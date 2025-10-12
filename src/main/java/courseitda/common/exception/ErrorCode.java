package courseitda.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    TEMPORARY_ERROR(
            "0000",
            "커스텀 예외로 전환된 임시 에러입니다.",
            HttpStatus.INTERNAL_SERVER_ERROR
    ),
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    public ProblemDetail toProblemDetail() {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatus);
        problemDetail.setDetail(message);
        problemDetail.setProperty("code", code);

        return problemDetail;
    }
}
