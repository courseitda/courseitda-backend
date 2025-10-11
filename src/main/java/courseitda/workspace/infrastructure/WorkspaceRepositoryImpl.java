package courseitda.workspace.infrastructure;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import courseitda.workspace.domain.Workspace;
import courseitda.workspace.domain.WorkspaceRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkspaceRepositoryImpl implements WorkspaceRepository {

    private final JpaWorkspaceRepository jpaWorkspaceRepository;

    @Override
    public Workspace save(final Workspace workspace) {
        return jpaWorkspaceRepository.save(workspace);
    }

    @Override
    public Optional<Workspace> findById(final Long workspaceId) {
        return jpaWorkspaceRepository.findById(workspaceId);
    }

    @Override
    public void deleteById(final Long workspaceId) {
        jpaWorkspaceRepository.deleteById(workspaceId);
    }

    @Override
    public boolean existsByMemberIdAndTitle(final Long memberId, final String title) {
        return jpaWorkspaceRepository.existsByMemberIdAndTitle(memberId, title);
    }
}
