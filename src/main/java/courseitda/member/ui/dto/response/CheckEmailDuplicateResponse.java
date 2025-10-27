package courseitda.member.ui.dto.response;

import courseitda.member.application.dto.response.CheckEmailDuplicateResult;

public record CheckEmailDuplicateResponse(
        boolean isDuplicated
) {

    public static CheckEmailDuplicateResponse from(final CheckEmailDuplicateResult result) {
        return new CheckEmailDuplicateResponse(
                result.isDuplicated()
        );
    }
}
