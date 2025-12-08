package courseitda.workspace.infrastructure;

import courseitda.workspace.domain.Place;
import courseitda.workspace.domain.PlaceRepository;
import java.util.Optional;
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
    public Optional<Place> findPlaceByNameAndAddressName(final String name, final String addressName) {
        return jpaPlaceRepository.findPlaceByNameAndAddressName(name, addressName);
    }
}
