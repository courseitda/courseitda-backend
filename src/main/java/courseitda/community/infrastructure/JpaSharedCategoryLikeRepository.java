package courseitda.community.infrastructure;

import courseitda.community.domain.SharedCategoryLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSharedCategoryLikeRepository extends JpaRepository<SharedCategoryLike, Long> {

    Optional<SharedCategoryLike> findByMemberIdAndSharedCategoryId(Long memberId, Long sharedCategoryId);
}
