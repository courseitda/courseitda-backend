package courseitda.workspace.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import courseitda.member.domain.Member;
import courseitda.workspace.domain.Workspace;

public interface JpaWorkspaceRepository extends JpaRepository<Workspace, Long> {

    boolean existsByMemberAndTitle(Member member, String title);
}
