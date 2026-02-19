package courseitda.mystorage.application;

import courseitda.member.domain.Member;
import courseitda.mystorage.domain.SavedCategory;
import courseitda.mystorage.domain.SavedCategoryPlace;
import courseitda.mystorage.domain.SavedCategoryPlaceRepository;
import courseitda.mystorage.domain.SavedCategoryRepository;
import courseitda.mystorage.ui.dto.request.SavedCategoryCreateRequest;
import courseitda.mystorage.ui.dto.request.SavedCategoryCreateRequest.SavedCategoryPlaceRequest;
import courseitda.mystorage.ui.dto.response.SavedCategoryCreateResponse;
import courseitda.place.domain.Place;
import courseitda.place.domain.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SavedCategoryService {

    private final SavedCategoryRepository savedCategoryRepository;
    private final SavedCategoryPlaceRepository savedCategoryPlaceRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public SavedCategoryCreateResponse createSavedCategory(
            final SavedCategoryCreateRequest request,
            final Member member
    ) {
        final var newSavedCategory = SavedCategory.createNew(member, request.name());
        final var persistedSavedCategory = savedCategoryRepository.save(newSavedCategory);

        for (final SavedCategoryPlaceRequest placeRequest : request.savedCategoryPlaces()) {
            final var newPlace = Place.createNew(
                    placeRequest.name(),
                    placeRequest.placeUrl(),
                    placeRequest.roadAddressName(),
                    placeRequest.addressName(),
                    placeRequest.latitude(),
                    placeRequest.longitude()
            );
            final var persistedPlace = placeRepository.save(newPlace);
            savedCategoryPlaceRepository.save(SavedCategoryPlace.createNew(persistedSavedCategory, persistedPlace));
        }

        return SavedCategoryCreateResponse.from(persistedSavedCategory);
    }
}
