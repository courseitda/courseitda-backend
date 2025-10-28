package courseitda.workspace.ui.dto.request;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.workspace.application.dto.request.CreateCategoryPlaceCommand;
import jakarta.validation.constraints.NotBlank;

public record CategoryPlaceCreateRequest(
        @NotBlank String name,
        String roadAddressName,
        @NotBlank String addressName,
        double latitude,
        double longitude
) {

    public CreateCategoryPlaceCommand toCommandWith(
            final MemberAuthInfo memberAuthInfo,
            final Long categoryId
    ) {
        return new CreateCategoryPlaceCommand(
                memberAuthInfo,
                categoryId,
                name,
                roadAddressName,
                addressName,
                latitude,
                longitude
        );
    }
}
