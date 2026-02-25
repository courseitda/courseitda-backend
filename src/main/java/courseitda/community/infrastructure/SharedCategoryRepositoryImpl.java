package courseitda.community.infrastructure;

import courseitda.community.domain.SharedCategory;
import courseitda.community.domain.SharedCategoryRepository;
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
}
