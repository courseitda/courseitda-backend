package courseitda.workspace.ui.dto.request;

import courseitda.workspace.application.dto.request.CreateCategoryPlaceCommand;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CategoryPlaceCreateRequest(
        @NotBlank(message = "장소 이름은 필수입니다.") String name,

        String roadAddressName,

        @NotBlank(message = "주소는 필수입니다.") String addressName,

        @Min(value = -90, message = "위도는 -90 이상이어야 합니다.") @Max(value = 90, message = "위도는 90 이하여야 합니다.") double lat,

        @Min(value = -180, message = "경도는 -180 이상이어야 합니다.") @Max(value = 180, message = "경도는 180 이하여야 합니다.") double lng
// todo: 검색 결과 상으로 도로명 주소의 유무에 따라 변경되어야 하는데, 해당 과정을 확인한 후 수정할 것
) {

    public CreateCategoryPlaceCommand toCommand() {
        return new CreateCategoryPlaceCommand(name, roadAddressName, addressName, lat, lng);
    }
}
