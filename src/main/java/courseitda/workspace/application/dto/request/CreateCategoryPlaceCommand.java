package courseitda.workspace.application.dto.request;

public record CreateCategoryPlaceCommand(
        String name,
        String roadAddressName,
        String addressName,
        double lat,
        double lng
) {
}
