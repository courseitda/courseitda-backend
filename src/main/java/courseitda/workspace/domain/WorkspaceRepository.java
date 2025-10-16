package courseitda.workspace.domain;

import java.util.List;
import java.util.Optional;

public interface WorkspaceRepository {

    Workspace save(Workspace workspace);

    void deleteById(Long workspaceId);

    boolean existsByMemberIdAndTitle(Long memberId, String title);

    Optional<Workspace> findById(Long workspaceId);

    List<Workspace> findAllByMemberId(Long memberId);
}
