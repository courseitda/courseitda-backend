package courseitda.community.infrastructure;

import courseitda.community.domain.SharedCategoryLike;
import courseitda.community.domain.SharedCategoryLikeRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
    public SharedCategoryLike saveAndFlush(final SharedCategoryLike sharedCategoryLike) {
        return jpaSharedCategoryLikeRepository.saveAndFlush(sharedCategoryLike);
    }

    @Override
    public void delete(final SharedCategoryLike sharedCategoryLike) {
        jpaSharedCategoryLikeRepository.delete(sharedCategoryLike);
    }

    @Override
    public List<Long> findAllSharedCategoryIdsByMemberIdAndSharedCategoryIdIn(
            final Long memberId,
            final List<Long> sharedCategoryIds
    ) {
        return jpaSharedCategoryLikeRepository.findAllSharedCategoryIdsByMemberIdAndSharedCategoryIdIn(memberId,
                sharedCategoryIds);
    }

    @Override
    public Optional<SharedCategoryLike> findByMemberIdAndSharedCategoryId(
            final Long memberId,
            final Long sharedCategoryId
    ) {
        return jpaSharedCategoryLikeRepository.findByMemberIdAndSharedCategoryId(memberId, sharedCategoryId);
    }

    @Override
    public List<SharedCategoryLike> findAllByMemberIdOrderByIdDesc(final Long memberId, final int limit) {
        return jpaSharedCategoryLikeRepository.findAllByMemberIdOrderByIdDesc(memberId, PageRequest.of(0, limit));
    }

    @Override
    public List<SharedCategoryLike> findAllByMemberIdAndIdLessThanOrderByIdDesc(
            final Long memberId,
            final Long cursor,
            final int limit
    ) {
        return jpaSharedCategoryLikeRepository.findAllByMemberIdAndIdLessThanOrderByIdDesc(memberId, cursor,
                PageRequest.of(0, limit));
    }
}
