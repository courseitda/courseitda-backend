package courseitda.workspace.domain;

public class CategoryBuilder {

    private Workspace workspace = WorkspaceFixture.anyWorkspace();
    private String name = CategoryFixture.anyName();
    private String color = CategoryFixture.anyColor();
    private Integer sequence = CategoryFixture.anySequence();

    public CategoryBuilder workspace(final Workspace workspace) {
        this.workspace = workspace;
        return this;
    }

    public CategoryBuilder name(final String name) {
        this.name = name;
        return this;
    }

    public CategoryBuilder color(final String color) {
        this.color = color;
        return this;
    }

    public CategoryBuilder sequence(final Integer sequence) {
        this.sequence = sequence;
        return this;
    }

    public Category build() {
        return Category.builder()
                .workspace(workspace)
                .name(name)
                .color(color)
                .sequence(sequence)
                .build();
    }
}
