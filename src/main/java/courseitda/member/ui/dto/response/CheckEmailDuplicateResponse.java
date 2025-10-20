package courseitda.member.ui.dto.response;

import courseitda.member.application.dto.response.IsEmailDuplicateResult;

public record CheckEmailDuplicateResponse(
        boolean isDuplicated
) {

    public static CheckEmailDuplicateResponse from(final IsEmailDuplicateResult result) {
        return new CheckEmailDuplicateResponse(
                result.isDuplicated()
        );
    }
}
