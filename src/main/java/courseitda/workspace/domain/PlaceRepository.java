package courseitda.workspace.domain;

import java.util.List;

public interface PlaceRepository {

    Place save(Place place);

    void deleteById(Long placeId);

    void deleteAllByIds(List<Long> placeIds);
}
