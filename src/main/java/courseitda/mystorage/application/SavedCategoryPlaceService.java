package courseitda.mystorage.application;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.mystorage.domain.SavedCategory;
import courseitda.mystorage.domain.SavedCategoryPlace;
import courseitda.mystorage.domain.SavedCategoryPlaceRepository;
import courseitda.mystorage.domain.SavedCategoryRepository;
import courseitda.mystorage.ui.dto.request.SavedCategoryPlaceCreateRequest;
import courseitda.mystorage.ui.dto.request.SavedCategoryPlaceUpdateRequest;
import courseitda.mystorage.ui.dto.response.SavedCategoryPlaceCreateResponse;
import courseitda.mystorage.ui.dto.response.SavedCategoryPlaceUpdateResponse;
import courseitda.place.domain.Place;
import courseitda.place.domain.PlaceRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SavedCategoryPlaceService {

    private final SavedCategoryRepository savedCategoryRepository;
    private final SavedCategoryPlaceRepository savedCategoryPlaceRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public SavedCategoryPlaceCreateResponse createSavedCategoryPlaces(
            final SavedCategoryPlaceCreateRequest request,
            final MemberAuthInfo memberAuthInfo,
            final Long savedCategoryId
    ) {
        final var savedCategory = getSavedCategoryById(savedCategoryId);
        savedCategory.validateOwnership(memberAuthInfo.id());

        final var createdPlaces = new ArrayList<SavedCategoryPlace>();
        for (final var placeRequest : request.savedCategoryPlaces()) {
            final var newPlace = Place.createNew(
                    placeRequest.name(),
                    placeRequest.placeUrl(),
                    placeRequest.roadAddressName(),
                    placeRequest.addressName(),
                    placeRequest.latitude(),
                    placeRequest.longitude()
            );
            final var persistedPlace = placeRepository.save(newPlace);
            createdPlaces.add(
                    savedCategoryPlaceRepository.save(SavedCategoryPlace.createNew(savedCategory, persistedPlace)));
        }

        return SavedCategoryPlaceCreateResponse.from(createdPlaces);
    }

    @Transactional
    public SavedCategoryPlaceUpdateResponse updateSavedCategoryPlaces(
            final SavedCategoryPlaceUpdateRequest request,
            final MemberAuthInfo memberAuthInfo,
            final Long savedCategoryId
    ) {
        final var savedCategory = getSavedCategoryById(savedCategoryId);
        savedCategory.validateOwnership(memberAuthInfo.id());

        removeMissingSavedCategoryPlaces(request.savedCategoryPlaces(), savedCategory);
        addNewSavedCategoryPlaces(request.savedCategoryPlaces(), savedCategory);

        final var syncedPlaces = savedCategoryPlaceRepository.findAllBySavedCategoryId(savedCategoryId);
        return SavedCategoryPlaceUpdateResponse.from(syncedPlaces);
    }

    private void removeMissingSavedCategoryPlaces(
            final List<SavedCategoryPlaceUpdateRequest.SavedCategoryPlaceRequest> placeRequests,
            final SavedCategory savedCategory
    ) {
        final Set<Long> requestPlaceIdSet = placeRequests.stream()
                .map(SavedCategoryPlaceUpdateRequest.SavedCategoryPlaceRequest::savedCategoryPlaceId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        final var existingPlaces = savedCategoryPlaceRepository.findAllBySavedCategoryId(savedCategory.getId());
        final var existingPlaceIds = existingPlaces.stream()
                .map(SavedCategoryPlace::getId)
                .toList();

        final var idsToDelete = existingPlaceIds.stream()
                .filter(id -> !requestPlaceIdSet.contains(id))
                .toList();
        savedCategoryPlaceRepository.deleteAllByIds(idsToDelete);
    }

    private void addNewSavedCategoryPlaces(
            final List<SavedCategoryPlaceUpdateRequest.SavedCategoryPlaceRequest> placeRequests,
            final SavedCategory savedCategory
    ) {
        for (final var placeRequest : placeRequests) {
            if (placeRequest.savedCategoryPlaceId() == null) {
                final var newPlace = Place.createNew(
                        placeRequest.name(),
                        placeRequest.placeUrl(),
                        placeRequest.roadAddressName(),
                        placeRequest.addressName(),
                        placeRequest.latitude(),
                        placeRequest.longitude()
                );
                final var persistedPlace = placeRepository.save(newPlace);
                savedCategoryPlaceRepository.save(SavedCategoryPlace.createNew(savedCategory, persistedPlace));
            }
        }
    }

    private SavedCategory getSavedCategoryById(final Long savedCategoryId) {
        return savedCategoryRepository.findById(savedCategoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SAVED_CATEGORY_NOT_FOUND));
    }
}
