package courseitda.place.infrastructure;

import courseitda.place.domain.Place;
import courseitda.place.domain.PlaceRepository;
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
}
