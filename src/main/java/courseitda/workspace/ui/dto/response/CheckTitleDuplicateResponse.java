package courseitda.workspace.ui.dto.response;

import courseitda.workspace.application.dto.response.IsTitleDuplicateResult;

public record CheckTitleDuplicateResponse(
        boolean isDuplicate
) {

    public static CheckTitleDuplicateResponse from(final IsTitleDuplicateResult result) {
        return new CheckTitleDuplicateResponse(result.isDuplicate());
    }
}
