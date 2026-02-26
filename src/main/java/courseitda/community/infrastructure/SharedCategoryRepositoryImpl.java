package courseitda.community.infrastructure;

import courseitda.community.domain.SharedCategory;
import courseitda.community.domain.SharedCategoryRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
    public Optional<SharedCategory> findById(final Long sharedCategoryId) {
        return jpaSharedCategoryRepository.findById(sharedCategoryId);
    }

    @Override
    public void delete(final SharedCategory sharedCategory) {
        jpaSharedCategoryRepository.delete(sharedCategory);
    }
}
