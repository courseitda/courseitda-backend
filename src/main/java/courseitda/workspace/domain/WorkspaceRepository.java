package courseitda.workspace.domain;

import java.util.Optional;

import courseitda.member.domain.Member;

public interface WorkspaceRepository {

    Workspace save(Workspace workspace);

    Optional<Workspace> findById(Long workspaceId);

    void deleteById(Long workspaceId);

    boolean existsByMemberAndTitle(Member member, String title);
}
