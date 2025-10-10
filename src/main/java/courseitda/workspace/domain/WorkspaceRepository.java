package courseitda.workspace.domain;

import courseitda.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

    boolean existsByMemberAndTitle(Member member, String title);
}
