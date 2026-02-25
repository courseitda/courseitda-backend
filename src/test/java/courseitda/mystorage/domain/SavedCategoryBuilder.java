package courseitda.mystorage.domain;

import courseitda.member.domain.Member;
import courseitda.member.domain.MemberFixture;

public class SavedCategoryBuilder {

    private Member owner = MemberFixture.anyMember();
    private String name = SavedCategoryFixture.anyName();

    public SavedCategoryBuilder owner(final Member owner) {
        this.owner = owner;
        return this;
    }

    public SavedCategoryBuilder name(final String name) {
        this.name = name;
        return this;
    }

    public SavedCategory build() {
        return SavedCategory.createNew(owner, name);
    }
}
