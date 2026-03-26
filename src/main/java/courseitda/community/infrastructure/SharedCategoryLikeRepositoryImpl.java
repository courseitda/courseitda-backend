package courseitda.community.infrastructure;

import courseitda.community.domain.SharedCategoryLike;
import courseitda.community.domain.SharedCategoryLikeRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SharedCategoryLikeRepositoryImpl implements SharedCategoryLikeRepository {

    private final JpaSharedCategoryLikeRepository jpaSharedCategoryLikeRepository;

    @Override
    public SharedCategoryLike save(final SharedCategoryLike sharedCategoryLike) {
        return jpaSharedCategoryLikeRepository.save(sharedCategoryLike);
    }

    @Override
    public void delete(final SharedCategoryLike sharedCategoryLike) {
        jpaSharedCategoryLikeRepository.delete(sharedCategoryLike);
    }

    @Override
    public Optional<SharedCategoryLike> findByMemberIdAndSharedCategoryId(
            final Long memberId,
            final Long sharedCategoryId
    ) {
        return jpaSharedCategoryLikeRepository.findByMemberIdAndSharedCategoryId(memberId, sharedCategoryId);
    }
}
