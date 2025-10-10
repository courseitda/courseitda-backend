package courseitda.workspace.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import courseitda.workspace.domain.Workspace;

public interface JpaWorkspaceRepository extends JpaRepository<Workspace, Long> {

    boolean existsByMemberIdAndTitle(Long memberId, String title);
}
