package courseitda.workspace.domain;

import java.util.ArrayList;
import java.util.List;

public class CategoryBuilder {

    private Workspace workspace = WorkspaceFixture.anyWorkspace();
    private String name = CategoryFixture.anyName();
    private String color = CategoryFixture.anyColor();
    private Integer sequence = CategoryFixture.anySequence();
    private List<CategoryPlace> categoryPlaces = new ArrayList<>();
    private CategoryPlace representativePlace = null;

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

    public CategoryBuilder categoryPlaces(final List<CategoryPlace> categoryPlaces) {
        this.categoryPlaces = categoryPlaces;
        return this;
    }

    public CategoryBuilder representativePlace(final CategoryPlace representativePlace) {
        this.representativePlace = representativePlace;
        return this;
    }

    public Category build() {
        return Category.builder()
                .workspace(workspace)
                .categoryPlaces(categoryPlaces)
                .name(name)
                .color(color)
                .sequence(sequence)
                .representativePlace(representativePlace)
                .build();
    }
}
