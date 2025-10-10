package courseitda.category.ui.dto.request;

public record CategoryPlaceCreateRequest(
        String name,
        String roadAddressName,
        String addressName,
        double lat,
        double lng
) {
}
