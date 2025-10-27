package courseitda.member.ui.dto.response;

import courseitda.member.application.dto.response.CheckNicknameDuplicateResult;

public record CheckNicknameDuplicateResponse(
        boolean isDuplicated
) {

    public static CheckNicknameDuplicateResponse from(final CheckNicknameDuplicateResult result) {
        return new CheckNicknameDuplicateResponse(
                result.isDuplicated()
        );
    }
}
