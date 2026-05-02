package courseitda.community.infrastructure;

import courseitda.community.domain.SharedCategoryLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaSharedCategoryLikeRepository extends JpaRepository<SharedCategoryLike, Long> {

    Optional<SharedCategoryLike> findByMemberIdAndSharedCategoryId(Long memberId, Long sharedCategoryId);

    @Query("SELECT scl.sharedCategory.id FROM SharedCategoryLike scl WHERE scl.member.id = :memberId AND scl.sharedCategory.id IN :sharedCategoryIds")
    List<Long> findAllSharedCategoryIdsByMemberIdAndSharedCategoryIdIn(@Param("memberId") Long memberId,
            @Param("sharedCategoryIds") List<Long> sharedCategoryIds);

    List<SharedCategoryLike> findAllByMemberIdOrderByIdDesc(Long memberId, Pageable pageable);

    List<SharedCategoryLike> findAllByMemberIdAndIdLessThanOrderByIdDesc(Long memberId, Long cursor, Pageable pageable);
}
