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
     * 6000 Series: Place Search Errors
     * 7000 Series: SavedCategory Errors
     * 8000 Series: SharedCategory Errors
     * */

    // TEMPORARY_ERROR - 0000
    TEMPORARY_ERROR(
            "0000",
            "커스텀 예외로 전환된 임시 에러입니다.",
            HttpStatus.INTERNAL_SERVER_ERROR // 500
    ),

    REQUEST_VALIDATION_FAILED(
            "0001",
            "요청 데이터 검증에 실패했습니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    // 1000 Series: Authentication and Authorization Errors
    MISSING_AUTH_HEADER(
            "1001",
            "Authorization 헤더가 존재하지 않습니다.",
            HttpStatus.UNAUTHORIZED // 401
    ),

    MALFORMED_BEARER_TOKEN(
            "1002",
            "Bearer 토큰 형식이 아닙니다.",
            HttpStatus.UNAUTHORIZED // 401
    ),

    INVALID_TOKEN(
            "1003",
            "유효하지 않은 토큰입니다.",
            HttpStatus.UNAUTHORIZED // 401
    ),

    INCORRECT_PASSWORD(
            "1004",
            "비밀번호가 올바르지 않습니다.",
            HttpStatus.UNAUTHORIZED // 401
    ),

    ACCESS_FORBIDDEN(
            "1005",
            "권한이 없습니다.",
            HttpStatus.FORBIDDEN // 403
    ),

    //-----------------------------------------------------------------------------------
    // 2000 Series: Workspace Errors
    WORKSPACE_TITLE_EMPTY(
            "2001",
            "워크스페이스 제목은 null 또는 공백일 수 없습니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    WORKSPACE_MODIFY_FORBIDDEN(
            "2002",
            "해당 워크스페이스의 수정 권한이 없습니다.",
            HttpStatus.FORBIDDEN // 403
    ),

    WORKSPACE_NOT_FOUND(
            "2003",
            "존재하지 않는 워크스페이스 입니다.",
            HttpStatus.NOT_FOUND // 404
    ),

    DUPLICATE_WORKSPACE_TITLE(
            "2004",
            "이미 사용중인 워크스페이스 제목입니다.",
            HttpStatus.CONFLICT // 409
    ),

    WORKSPACE_TITLE_LENGTH_EXCEEDED(
            "2005",
            "워크스페이스 제목은 20자 이하이어야 합니다.",
            HttpStatus.UNPROCESSABLE_ENTITY // 422
    ),

    WORKSPACE_LAST_ACTIVITY_AT_NULL(
            "2006",
            "워크스페이스의 마지막 활동 시간은 null 일 수 없습니다. (내부 오류)",
            HttpStatus.INTERNAL_SERVER_ERROR // 500
    ),

    //-----------------------------------------------------------------------------------
    // 3000 Series: Category Errors
    CATEGORY_NAME_EMPTY(
            "3001",
            "카테고리 이름은 null 또는 공백일 수 없습니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    CATEGORY_COLOR_EMPTY(
            "3002",
            "카테고리 색상은 null 또는 공백일 수 없습니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    DUPLICATE_CATEGORY_ORDER_IN_REQUEST(
            "3003",
            "요청에 중복된 순서 값이 있습니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    DUPLICATE_CATEGORY_ID_IN_REQUEST(
            "3004",
            "요청에 중복된 카테고리 ID가 있습니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    CATEGORY_MODIFY_FORBIDDEN(
            "3005",
            "해당 카테고리의 수정 권한이 없습니다.",
            HttpStatus.FORBIDDEN // 403
    ),

    CATEGORY_OUT_OF_WORKSPACE(
            "3006",
            "해당 워크스페이스에 속한 카테고리가 아닙니다.",
            HttpStatus.FORBIDDEN // 403
    ),

    INVALID_REPRESENTATIVE_PLACE_ASSIGNMENT(
            "3007",
            "다른 카테고리의 장소를 대표로 지정할 수 없습니다.",
            HttpStatus.FORBIDDEN // 403
    ),

    CATEGORY_NOT_FOUND(
            "3008",
            "존재하지 않는 카테고리 입니다.",
            HttpStatus.NOT_FOUND // 404
    ),

    PARTIAL_CATEGORY_NOT_FOUND(
            "3009",
            "일부 카테고리를 찾을 수 없습니다.",
            HttpStatus.NOT_FOUND // 404
    ),

    CATEGORY_NAME_LENGTH_EXCEEDED(
            "3010",
            "카테고리 이름은 10자를 초과할 수 없습니다.",
            HttpStatus.UNPROCESSABLE_ENTITY // 422
    ),

    INVALID_CATEGORY_COLOR_FORMAT(
            "3011",
            "유효하지 않은 색상 형식입니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    //-----------------------------------------------------------------------------------
    // 4000 Series: Category Place Errors
    PLACE_NAME_EMPTY(
            "4001",
            "장소 이름은 null 또는 공백일 수 없습니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    PLACE_ADDRESS_EMPTY(
            "4002",
            "주소는 null 또는 공백일 수 없습니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    INVALID_LATITUDE_RANGE(
            "4003",
            "위도는 -90에서 90 사이여야 합니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    INVALID_LONGITUDE_RANGE(
            "4004",
            "경도는 -180에서 180 사이여야 합니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    PLACE_NOT_BELONG_TO_CATEGORY(
            "4005",
            "해당 카테고리에 속한 장소가 아닙니다.",
            HttpStatus.FORBIDDEN // 403
    ),

    CATEGORY_PLACE_NOT_FOUND(
            "4006",
            "존재하지 않는 카테고리 장소 입니다.",
            HttpStatus.NOT_FOUND // 404
    ),

    //-----------------------------------------------------------------------------------
    // 5000 Series: Member Errors
    MEMBER_NICKNAME_EMPTY(
            "5001",
            "닉네임은 null 또는 공백일 수 없습니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    MEMBER_EMAIL_EMPTY(
            "5002",
            "이메일은 null 또는 공백일 수 없습니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    INVALID_NICKNAME_LENGTH(
            "5003",
            "닉네임은 2자 이상 20자 이하이어야 합니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    INVALID_EMAIL_FORMAT(
            "5004",
            "유효하지 않은 이메일 형식입니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    MEMBER_NOT_FOUND(
            "5005",
            "존재하지 않는 회원 입니다.",
            HttpStatus.NOT_FOUND // 404
    ),

    MEMBER_NOT_FOUND_BY_EMAIL(
            "5006",
            "해당 이메일을 가진 회원이 존재하지 않습니다.",
            HttpStatus.NOT_FOUND // 404
    ),

    DUPLICATE_EMAIL(
            "5007",
            "이미 사용중인 이메일입니다.",
            HttpStatus.CONFLICT // 409
    ),

    DUPLICATE_NICKNAME(
            "5008",
            "이미 사용중인 닉네임입니다.",
            HttpStatus.CONFLICT // 409
    ),

    MEMBER_PASSWORD_EMPTY(
            "5009",
            "비밀번호는 null 또는 공백일 수 없습니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    INVALID_PASSWORD_LENGTH(
            "5010",
            "비밀번호는 6자 이상 20자 이하여야 합니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    //-----------------------------------------------------------------------------------
    // 6000 Series: Place Search Errors
    PLACE_SEARCH_KEYWORD_EMPTY(
            "6001",
            "검색어는 null 또는 공백일 수 없습니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    INVALID_PLACE_SEARCH_SIZE(
            "6002",
            "검색 결과 개수는 1에서 15 사이여야 합니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    SEARCHED_PLACE_NAME_EMPTY(
            "6003",
            "검색된 장소의 이름은 null 또는 공백일 수 없습니다.",
            HttpStatus.BAD_GATEWAY // 502
    ),

    SEARCHED_PLACE_ADDRESS_EMPTY(
            "6004",
            "검색된 장소의 주소는 null 또는 공백일 수 없습니다.",
            HttpStatus.BAD_GATEWAY // 502
    ),

    INVALID_SEARCHED_PLACE_LATITUDE(
            "6005",
            "검색된 장소의 위도는 -90에서 90 사이여야 합니다.",
            HttpStatus.BAD_GATEWAY // 502
    ),

    INVALID_SEARCHED_PLACE_LONGITUDE(
            "6006",
            "검색된 장소의 경도는 -180에서 180 사이여야 합니다.",
            HttpStatus.BAD_GATEWAY // 502
    ),

    KAKAO_PLACE_SEARCH_RESPONSE_NULL(
            "6007",
            "카카오 장소 검색 API 응답이 null입니다.",
            HttpStatus.BAD_GATEWAY // 502
    ),

    KAKAO_PLACE_SEARCH_STATUS_CHECK_ERROR(
            "6008",
            "카카오 장소 검색 API 응답 상태 확인 중 오류가 발생했습니다.",
            HttpStatus.BAD_GATEWAY // 502
    ),

    KAKAO_PLACE_SEARCH_ERROR(
            "6009",
            "카카오 장소 검색 API 호출 중 오류가 발생했습니다.",
            HttpStatus.BAD_GATEWAY // 502
    ),

    NAVER_PLACE_SEARCH_RESPONSE_NULL(
            "6010",
            "네이버 장소 검색 API 응답이 null입니다.",
            HttpStatus.BAD_GATEWAY // 502
    ),

    NAVER_PLACE_SEARCH_STATUS_CHECK_ERROR(
            "6011",
            "네이버 장소 검색 API 응답 상태 확인 중 오류가 발생했습니다.",
            HttpStatus.BAD_GATEWAY // 502
    ),

    NAVER_PLACE_SEARCH_ERROR(
            "6012",
            "네이버 장소 검색 API 호출 중 오류가 발생했습니다.",
            HttpStatus.BAD_GATEWAY // 502
    ),

    SEARCHED_PLACE_URL_EMPTY(
            "6013",
            "검색된 장소의 URL은 null 또는 공백일 수 없습니다.",
            HttpStatus.BAD_GATEWAY // 502
    ),

    //-----------------------------------------------------------------------------------
    // 7000 Series: SavedCategory Errors
    SAVED_CATEGORY_NAME_EMPTY(
            "7001",
            "보관 카테고리 이름은 null 또는 공백일 수 없습니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    SAVED_CATEGORY_NAME_LENGTH_EXCEEDED(
            "7002",
            "보관 카테고리 이름은 10자를 초과할 수 없습니다.",
            HttpStatus.UNPROCESSABLE_ENTITY // 422
    ),

    SAVED_CATEGORY_NOT_FOUND(
            "7003",
            "존재하지 않는 보관 카테고리 입니다.",
            HttpStatus.NOT_FOUND // 404
    ),

    SAVED_CATEGORY_MODIFY_FORBIDDEN(
            "7004",
            "해당 보관 카테고리의 수정 권한이 없습니다.",
            HttpStatus.FORBIDDEN // 403
    ),

    //-----------------------------------------------------------------------------------
    // 8000 Series: SharedCategory Errors
    SHARED_CATEGORY_NAME_EMPTY(
            "8001",
            "공유 카테고리 이름은 null 또는 공백일 수 없습니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    SHARED_CATEGORY_NAME_LENGTH_EXCEEDED(
            "8002",
            "공유 카테고리 이름은 10자를 초과할 수 없습니다.",
            HttpStatus.UNPROCESSABLE_ENTITY // 422
    ),

    SHARED_CATEGORY_NOT_FOUND(
            "8003",
            "존재하지 않는 공유 카테고리 입니다.",
            HttpStatus.NOT_FOUND // 404
    ),

    SHARED_CATEGORY_MODIFY_FORBIDDEN(
            "8004",
            "해당 공유 카테고리의 수정 권한이 없습니다.",
            HttpStatus.FORBIDDEN // 403
    ),

    INVALID_SHARED_CATEGORY_SIZE(
            "8005",
            "조회 개수는 1에서 100 사이여야 합니다.",
            HttpStatus.BAD_REQUEST // 400
    ),

    BLANK_SHARED_CATEGORY_SEARCH_KEYWORD(
            "8006",
            "검색어를 입력해 주세요.",
            HttpStatus.BAD_REQUEST // 400
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
