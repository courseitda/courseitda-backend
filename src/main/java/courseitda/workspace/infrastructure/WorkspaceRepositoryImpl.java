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
    public boolean existsByOwnerIdAndTitle(final Long ownerId, final String title) {
        return jpaWorkspaceRepository.existsByOwnerIdAndTitle(ownerId, title);
    }

    @Override
    public Optional<Workspace> findById(final Long workspaceId) {
        return jpaWorkspaceRepository.findById(workspaceId);
    }

    @Override
    public Optional<Workspace> findByIdentifier(final String identifier) {
        return jpaWorkspaceRepository.findByIdentifier(identifier);
    }

    @Override
    public List<Workspace> findAllByOwnerId(final Long ownerId) {
        return jpaWorkspaceRepository.findAllByOwnerId(ownerId);
    }
}
