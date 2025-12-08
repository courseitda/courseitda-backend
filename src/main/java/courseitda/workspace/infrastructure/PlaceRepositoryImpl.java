package courseitda.workspace.infrastructure;

import courseitda.workspace.domain.Place;
import courseitda.workspace.domain.PlaceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepository {

    private final JpaPlaceRepository jpaPlaceRepository;

    @Override
    public Place save(final Place place) {
        return jpaPlaceRepository.save(place);
    }

    @Override
    public void deleteById(final Long placeId) {
        jpaPlaceRepository.deleteById(placeId);
    }

    @Override
    public void deleteAllByIds(final List<Long> placeIds) {
        if (!placeIds.isEmpty()) {
            jpaPlaceRepository.deleteAllById(placeIds);
        }
    }
}
