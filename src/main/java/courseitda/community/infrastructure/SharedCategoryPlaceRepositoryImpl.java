package courseitda.community.infrastructure;

import courseitda.community.domain.SharedCategoryPlace;
import courseitda.community.domain.SharedCategoryPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SharedCategoryPlaceRepositoryImpl implements SharedCategoryPlaceRepository {

    private final JpaSharedCategoryPlaceRepository jpaSharedCategoryPlaceRepository;

    @Override
    public SharedCategoryPlace save(final SharedCategoryPlace sharedCategoryPlace) {
        return jpaSharedCategoryPlaceRepository.save(sharedCategoryPlace);
    }
}
