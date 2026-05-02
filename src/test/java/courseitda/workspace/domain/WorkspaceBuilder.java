package courseitda.workspace.domain;

import courseitda.member.domain.Member;
import courseitda.member.domain.MemberFixture;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

public class WorkspaceBuilder {

    private Member owner = MemberFixture.anyMember();
    private String identifier = UUID.randomUUID().toString();
    private String title = WorkspaceFixture.anyTitle();
    private LocalDateTime lastActivityAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

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

    public WorkspaceBuilder lastActivityAt(final LocalDateTime lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
        return this;
    }

    public Workspace build() {
        return Workspace.builder()
                .owner(owner)
                .identifier(identifier)
                .title(title)
                .lastActivityAt(lastActivityAt)
                .build();
    }
}
