package courseitda.community.domain;

import courseitda.member.domain.Member;
import courseitda.member.domain.MemberFixture;
import java.util.ArrayList;

public class SharedCategoryBuilder {

    private Member author = MemberFixture.anyMember();
    private String name = SharedCategoryFixture.anyName();

    public SharedCategoryBuilder author(final Member author) {
        this.author = author;
        return this;
    }

    public SharedCategoryBuilder name(final String name) {
        this.name = name;
        return this;
    }

    public SharedCategory build() {
        return SharedCategory.builder()
                .name(name)
                .author(author)
                .sharedCategoryPlaces(new ArrayList<>())
                .build();
    }
}
