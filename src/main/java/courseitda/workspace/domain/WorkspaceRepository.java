package courseitda.workspace.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import courseitda.member.domain.Member;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

    boolean existsByMemberAndTitle(Member member, String title);
}
