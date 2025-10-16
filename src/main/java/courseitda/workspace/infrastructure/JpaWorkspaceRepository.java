package courseitda.workspace.infrastructure;

import courseitda.workspace.domain.Workspace;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaWorkspaceRepository extends JpaRepository<Workspace, Long> {

    boolean existsByOwnerIdAndTitle(Long ownerId, String title);

    List<Workspace> findAllByOwnerId(Long ownerId);
}
