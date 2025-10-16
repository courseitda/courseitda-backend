package courseitda.workspace.domain;

import java.util.List;
import java.util.Optional;

public interface WorkspaceRepository {

    Workspace save(Workspace workspace);

    void deleteById(Long workspaceId);

    boolean existsByOwnerIdAndTitle(Long ownerId, String title);

    Optional<Workspace> findById(Long workspaceId);

    Optional<Workspace> findByIdentifier(String identifier);

    List<Workspace> findAllByOwnerId(Long ownerId);
}
