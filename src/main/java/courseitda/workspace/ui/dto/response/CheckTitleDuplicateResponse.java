package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.CheckTitleDuplicateResult;

public record CheckTitleDuplicateResponse(
        boolean isDuplicate
) {

    public static CheckTitleDuplicateResponse from(final CheckTitleDuplicateResult result) {
        return new CheckTitleDuplicateResponse(result.isDuplicate());
    }
}
