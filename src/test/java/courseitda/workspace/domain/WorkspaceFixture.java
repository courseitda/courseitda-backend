package courseitda.workspace.domain;

import courseitda.member.domain.Member;
import courseitda.member.domain.MemberFixture;
import java.util.concurrent.atomic.AtomicLong;

public class WorkspaceFixture {

    public static AtomicLong sequenceTitle = new AtomicLong(0L);

    public static String anyTitle() {
        return "workspace" + sequenceTitle.incrementAndGet();
    }

    public static Workspace anyWorkspace() {
        return anyWorkspace(MemberFixture.anyMember());
    }

    public static Workspace anyWorkspace(final Member member) {
        return new WorkspaceBuilder()
                .member(member)
                .build();
    }
}

