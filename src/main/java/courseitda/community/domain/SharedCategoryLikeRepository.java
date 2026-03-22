package courseitda.community.domain;

import java.util.Optional;

public interface SharedCategoryLikeRepository {

    SharedCategoryLike save(SharedCategoryLike sharedCategoryLike);

    Optional<SharedCategoryLike> findByMemberIdAndSharedCategoryId(Long memberId, Long sharedCategoryId);
}
