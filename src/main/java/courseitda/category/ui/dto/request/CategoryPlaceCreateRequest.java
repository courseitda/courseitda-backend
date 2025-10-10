package courseitda.category.ui.dto.request;

public record CategoryPlaceCreateRequest(
        String name,
        String roadAddressName,
        String addressName,
        double lat,
        double lng
// todo: 검색 결과 상으로 도로명 주소의 유무에 따라 변경되어야 하는데, 해당 과정을 확인한 후 수정할 것
) {
}
