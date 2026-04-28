package courseitda.community.domain;

public class RecommendedCategoryBuilder {

    private String imageUrl = RecommendedCategoryFixture.anyImageUrl();
    private SharedCategory sharedCategory = SharedCategoryFixture.anySharedCategory();

    public RecommendedCategoryBuilder imageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public RecommendedCategoryBuilder sharedCategory(final SharedCategory sharedCategory) {
        this.sharedCategory = sharedCategory;
        return this;
    }

    public RecommendedCategory build() {
        return RecommendedCategory.builder()
                .imageUrl(imageUrl)
                .sharedCategory(sharedCategory)
                .build();
    }
}
