package courseitda.workspace.application.dto.request;

public record CreateCategoryCommand(
        String name,
        String color
) {
}
