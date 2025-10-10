package courseitda.workspace.domain;

import courseitda.member.domain.Member;
import courseitda.member.domain.MemberFixture;

public class WorkspaceBuilder {

    private Member member = MemberFixture.anyMember();
    private String title = WorkspaceFixture.anyTitle();

    public WorkspaceBuilder member(final Member member) {
        this.member = member;
        return this;
    }

    public WorkspaceBuilder title(final String title) {
        this.title = title;
        return this;
    }

    public Workspace build() {
        return Workspace.createEmpty(member, title);
    }
}

