package courseitda.place.infrastructure;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import courseitda.place.domain.Place;
import courseitda.place.domain.PlaceRepository;
import lombok.RequiredArgsConstructor;

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

