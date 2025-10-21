package courseitda.member.ui.dto.response;

import courseitda.member.application.dto.response.IsNicknameDuplicateResult;

public record CheckNicknameDuplicateResponse(
        boolean isDuplicated
) {

    public static CheckNicknameDuplicateResponse from(final IsNicknameDuplicateResult result) {
        return new CheckNicknameDuplicateResponse(
                result.isDuplicated()
        );
    }
}
