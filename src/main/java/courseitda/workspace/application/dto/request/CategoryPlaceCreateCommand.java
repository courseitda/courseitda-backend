package courseitda.workspace.application.dto.request;

public record CategoryPlaceCreateCommand(
        String name,
        String roadAddressName,
        String addressName,
        double lat,
        double lng
) {
}
