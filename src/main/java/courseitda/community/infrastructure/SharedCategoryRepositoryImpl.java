package courseitda.community.infrastructure;

import courseitda.community.domain.SharedCategory;
import courseitda.community.domain.SharedCategoryRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SharedCategoryRepositoryImpl implements SharedCategoryRepository {

    private final JpaSharedCategoryRepository jpaSharedCategoryRepository;

    @Override
    public SharedCategory save(final SharedCategory sharedCategory) {
        return jpaSharedCategoryRepository.save(sharedCategory);
    }

    @Override
    public void delete(final SharedCategory sharedCategory) {
        jpaSharedCategoryRepository.delete(sharedCategory);
    }

    @Override
    public Optional<SharedCategory> findById(final Long sharedCategoryId) {
        return jpaSharedCategoryRepository.findByIdAndDeletedAtIsNull(sharedCategoryId);
    }

    @Override
    public List<SharedCategory> findAllByAuthorIdOrderByIdDesc(final Long authorId, final int limit) {
        return jpaSharedCategoryRepository.findAllByAuthorIdAndDeletedAtIsNullOrderByIdDesc(authorId, PageRequest.of(0,
                limit));
    }

    @Override
    public List<SharedCategory> findAllByAuthorIdAndIdLessThanOrderByIdDesc(
            final Long authorId,
            final Long cursor,
            final int limit
    ) {
        return jpaSharedCategoryRepository.findAllByAuthorIdAndIdLessThanAndDeletedAtIsNullOrderByIdDesc(authorId,
                cursor, PageRequest.of(0, limit));
    }

    @Override
    public List<SharedCategory> findAllOrderByIdDesc(final int limit) {
        return jpaSharedCategoryRepository.findAllByDeletedAtIsNullOrderByIdDesc(PageRequest.of(0, limit));
    }

    @Override
    public List<SharedCategory> findAllByIdLessThanOrderByIdDesc(final Long cursor, final int limit) {
        return jpaSharedCategoryRepository.findAllByIdLessThanAndDeletedAtIsNullOrderByIdDesc(cursor,
                PageRequest.of(0, limit));
    }

    @Override
    public List<SharedCategory> findAllByNameContainingOrderByIdDesc(final String keyword, final int limit) {
        return jpaSharedCategoryRepository.findAllByNameContainingAndDeletedAtIsNullOrderByIdDesc(keyword,
                PageRequest.of(0, limit));
    }

    @Override
    public List<SharedCategory> findAllByNameContainingAndIdLessThanOrderByIdDesc(
            final String keyword,
            final Long cursor,
            final int limit
    ) {
        return jpaSharedCategoryRepository.findAllByNameContainingAndIdLessThanAndDeletedAtIsNullOrderByIdDesc(
                keyword, cursor, PageRequest.of(0, limit));
    }
}
