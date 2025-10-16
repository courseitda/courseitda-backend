package courseitda.workspace.domain;

import courseitda.member.domain.Member;
import courseitda.member.domain.MemberFixture;
import java.util.UUID;

public class WorkspaceBuilder {

    private Member owner = MemberFixture.anyMember();
    private String identifier = UUID.randomUUID().toString();
    private String title = WorkspaceFixture.anyTitle();

    public WorkspaceBuilder owner(final Member owner) {
        this.owner = owner;
        return this;
    }

    public WorkspaceBuilder identifier(final String identifier) {
        this.identifier = identifier;
        return this;
    }

    public WorkspaceBuilder title(final String title) {
        this.title = title;
        return this;
    }

    public Workspace build() {
        return Workspace.builder()
                .owner(owner)
                .identifier(identifier)
                .title(title)
                .build();
    }
}
