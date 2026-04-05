package courseitda.community.domain;

import java.util.List;
import java.util.Optional;

public interface SharedCategoryLikeRepository {

    SharedCategoryLike save(SharedCategoryLike sharedCategoryLike);

    void delete(SharedCategoryLike sharedCategoryLike);

    Optional<SharedCategoryLike> findByMemberIdAndSharedCategoryId(Long memberId, Long sharedCategoryId);

    List<Long> findAllSharedCategoryIdsByMemberIdAndSharedCategoryIdIn(Long memberId, List<Long> sharedCategoryIds);

    List<SharedCategoryLike> findAllByMemberIdOrderByIdDesc(Long memberId, int limit);

    List<SharedCategoryLike> findAllByMemberIdAndIdLessThanOrderByIdDesc(Long memberId, Long cursor, int limit);
}
