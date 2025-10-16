package courseitda.workspace.infrastructure;

import courseitda.workspace.domain.Workspace;
import courseitda.workspace.domain.WorkspaceRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WorkspaceRepositoryImpl implements WorkspaceRepository {

    private final JpaWorkspaceRepository jpaWorkspaceRepository;

    @Override
    public Workspace save(final Workspace workspace) {
        return jpaWorkspaceRepository.save(workspace);
    }

    @Override
    public void deleteById(final Long workspaceId) {
        jpaWorkspaceRepository.deleteById(workspaceId);
    }

    @Override
    public boolean existsByMemberIdAndTitle(final Long memberId, final String title) {
        return jpaWorkspaceRepository.existsByMemberIdAndTitle(memberId, title);
    }

    @Override
    public Optional<Workspace> findById(final Long workspaceId) {
        return jpaWorkspaceRepository.findById(workspaceId);
    }

    @Override
    public List<Workspace> findAllByMemberId(final Long memberId) {
        return jpaWorkspaceRepository.findAllByMemberId(memberId);
    }
}
