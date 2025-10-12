package courseitda.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /*
     * 0000 Series: General Errors
     * 1000 Series: Authentication and Authorization Errors
     * 2000 Series: Workspace Errors
     * 3000 Series: Category Errors
     * 4000 Series: Category Place Errors
     * 5000 Series: Member Errors
     * */

    // TEMPORARY_ERROR - 0000
    TEMPORARY_ERROR(
            "0000",
            "커스텀 예외로 전환된 임시 에러입니다.",
            HttpStatus.INTERNAL_SERVER_ERROR
    ),

    // 1000 Series: Authentication and Authorization Errors
    AUTH_TOKEN_NOT_FOUND(
            "1001",
            "Authorization 헤더가 존재하지 않습니다.",
            HttpStatus.UNAUTHORIZED
    ),

    INVALID_TOKEN_FORMAT(
            "1002",
            "Bearer 토큰 형식이 아닙니다.",
            HttpStatus.UNAUTHORIZED
    ),

    INVALID_TOKEN(
            "1003",
            "유효하지 않은 토큰입니다.",
            HttpStatus.UNAUTHORIZED
    ),

    INVALID_PASSWORD(
            "1004",
            "비밀번호가 올바르지 않습니다.",
            HttpStatus.UNAUTHORIZED
    ),

    UNAUTHORIZED_ACCESS(
            "1005",
            "권한이 없습니다.",
            HttpStatus.FORBIDDEN
    ),

    // 2000 Series: Workspace Errors
    WORKSPACE_TITLE_BLANK(
            "2001",
            "워크스페이스 제목은 공백일 수 없습니다.",
            HttpStatus.BAD_REQUEST
    ),

    WORKSPACE_TITLE_TOO_LONG(
            "2002",
            "워크스페이스 제목은 20자 이하이어야 합니다.",
            HttpStatus.UNPROCESSABLE_ENTITY
    ),

    WORKSPACE_NOT_FOUND(
            "2003",
            "ID에 해당하는 워크스페이스를 찾을 수 없습니다.",
            HttpStatus.NOT_FOUND
    ),

    WORKSPACE_TITLE_DUPLICATE(
            "2004",
            "이미 사용중인 워크스페이스 제목입니다.",
            HttpStatus.CONFLICT
    ),

    WORKSPACE_FORBIDDEN(
            "2005",
            "해당 워크스페이스의 수정 권한이 없습니다.",
            HttpStatus.FORBIDDEN
    ),

    // 3000 Series: Category Errors
    CATEGORY_NAME_REQUIRED(
            "3001",
            "카테고리 이름은 필수입니다.",
            HttpStatus.BAD_REQUEST
    ),

    CATEGORY_NAME_TOO_LONG(
            "3002",
            "카테고리 이름은 10자를 초과할 수 없습니다.",
            HttpStatus.UNPROCESSABLE_ENTITY
    ),

    CATEGORY_COLOR_REQUIRED(
            "3003",
            "카테고리 색상은 필수입니다.",
            HttpStatus.BAD_REQUEST
    ),

    CATEGORY_COLOR_INVALID(
            "3004",
            "올바른 색상 형식이 아닙니다.",
            HttpStatus.UNPROCESSABLE_ENTITY
    ),

    CATEGORY_NOT_FOUND(
            "3005",
            "ID에 해당하는 카테고리를 찾을 수 없습니다.",
            HttpStatus.NOT_FOUND
    ),

    CATEGORY_SOME_NOT_FOUND(
            "3006",
            "일부 카테고리를 찾을 수 없습니다.",
            HttpStatus.NOT_FOUND
    ),

    CATEGORY_DUPLICATE_ID(
            "3007",
            "중복된 카테고리 ID가 있습니다.",
            HttpStatus.BAD_REQUEST
    ),

    CATEGORY_DUPLICATE_ORDER(
            "3008",
            "중복된 순서 값이 있습니다.",
            HttpStatus.BAD_REQUEST
    ),

    CATEGORY_FORBIDDEN(
            "3009",
            "해당 카테고리를 수정할 권한이 없습니다.",
            HttpStatus.FORBIDDEN
    ),

    CATEGORY_NOT_BELONG_TO_WORKSPACE(
            "3010",
            "해당 워크스페이스에 속한 카테고리가 아닙니다.",
            HttpStatus.FORBIDDEN
    ),

    CATEGORY_REPRESENTATIVE_PLACE_FORBIDDEN(
            "3011",
            "다른 카테고리의 장소를 대표로 지정할 수 없습니다.",
            HttpStatus.FORBIDDEN
    ),

    // 4000 Series: Category Place Errors
    PLACE_NAME_REQUIRED(
            "4001",
            "장소 이름은 필수입니다.",
            HttpStatus.BAD_REQUEST
    ),

    PLACE_ADDRESS_REQUIRED(
            "4002",
            "주소는 필수입니다.",
            HttpStatus.BAD_REQUEST
    ),

    PLACE_LATITUDE_INVALID(
            "4003",
            "위도는 -90에서 90 사이여야 합니다.",
            HttpStatus.BAD_REQUEST
    ),

    PLACE_LONGITUDE_INVALID(
            "4004",
            "경도는 -180에서 180 사이여야 합니다.",
            HttpStatus.BAD_REQUEST
    ),

    CATEGORY_PLACE_NOT_FOUND(
            "4005",
            "ID에 해당하는 카테고리 장소를 찾을 수 없습니다.",
            HttpStatus.NOT_FOUND
    ),

    CATEGORY_PLACE_FORBIDDEN(
            "4006",
            "해당 카테고리에 속한 장소가 아닙니다.",
            HttpStatus.FORBIDDEN
    ),

    // 5000 Series: Member Errors
    MEMBER_NOT_FOUND(
            "5001",
            "회원을 찾을 수 없습니다.",
            HttpStatus.NOT_FOUND
    ),

    MEMBER_NOT_FOUND_BY_EMAIL(
            "5002",
            "해당 이메일을 가진 회원이 존재하지 않습니다.",
            HttpStatus.NOT_FOUND
    ),

    MEMBER_NICKNAME_BLANK(
            "5003",
            "닉네임은 null 또는 공백일 수 없습니다.",
            HttpStatus.BAD_REQUEST
    ),

    MEMBER_NICKNAME_LENGTH_INVALID(
            "5004",
            "닉네임은 2자 이상 20자 이하이어야 합니다.",
            HttpStatus.BAD_REQUEST
    ),

    MEMBER_EMAIL_BLANK(
            "5005",
            "이메일은 null 이거나 빈 문자열일 수 없습니다.",
            HttpStatus.BAD_REQUEST
    ),

    MEMBER_EMAIL_INVALID(
            "5006",
            "유효한 이메일 형식이 아닙니다.",
            HttpStatus.BAD_REQUEST
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
