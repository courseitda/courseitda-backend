package courseitda.workspace.domain;

import java.util.Optional;

public interface WorkspaceRepository {

    Workspace save(Workspace workspace);

    Optional<Workspace> findById(Long workspaceId);

    void deleteById(Long workspaceId);

    boolean existsByMemberIdAndTitle(Long memberId, String title);
}
