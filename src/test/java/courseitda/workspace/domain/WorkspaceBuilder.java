package courseitda.workspace.domain;

import courseitda.member.domain.Member;
import courseitda.member.domain.MemberFixture;

public class WorkspaceBuilder {

    private Member owner = MemberFixture.anyMember();
    private String title = WorkspaceFixture.anyTitle();

    public WorkspaceBuilder owner(final Member owner) {
        this.owner = owner;
        return this;
    }

    public WorkspaceBuilder title(final String title) {
        this.title = title;
        return this;
    }

    public Workspace build() {
        return Workspace.builder()
                .owner(owner)
                .title(title)
                .build();
    }
}
