package courseitda.mystorage.application;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.member.domain.Member;
import courseitda.mystorage.domain.SavedCategory;
import courseitda.mystorage.domain.SavedCategoryPlace;
import courseitda.mystorage.domain.SavedCategoryPlaceRepository;
import courseitda.mystorage.domain.SavedCategoryRepository;
import courseitda.mystorage.ui.dto.request.SavedCategoryCreateRequest;
import courseitda.mystorage.ui.dto.request.SavedCategoryUpdateRequest;
import courseitda.mystorage.ui.dto.response.SavedCategoryCreateResponse;
import courseitda.mystorage.ui.dto.response.SavedCategoryReadResponse;
import courseitda.mystorage.ui.dto.response.SavedCategoryUpdateResponse;
import courseitda.place.domain.Place;
import courseitda.place.domain.PlaceRepository;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
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
            savedCategoryPlaceRepository.save(SavedCategoryPlace.createNew(persistedSavedCategory, persistedPlace));
        }

        // 주의: savedCategoryPlaces 응답에 포함 시 getSavedCategoryById()로 재조회 필요 (JPA 1차 캐시 불일치)
        return SavedCategoryCreateResponse.from(persistedSavedCategory);
    }

    @Transactional
    public SavedCategoryUpdateResponse updateSavedCategory(
            final SavedCategoryUpdateRequest request,
            final MemberAuthInfo memberAuthInfo,
            final Long savedCategoryId
    ) {
        final var savedCategory = getSavedCategoryById(savedCategoryId);
        savedCategory.validateOwnership(memberAuthInfo.id());

        applyName(request.name(), savedCategory);
        syncSavedCategoryPlaces(request.savedCategoryPlaces(), savedCategory);

        // 주의: savedCategoryPlaces 응답에 포함 시 getSavedCategoryById()로 재조회 필요 (JPA 1차 캐시 불일치)
        return SavedCategoryUpdateResponse.from(savedCategory);
    }

    @Transactional
    public void deleteSavedCategory(final MemberAuthInfo memberAuthInfo, final Long savedCategoryId) {
        final var savedCategory = getSavedCategoryById(savedCategoryId);
        savedCategory.validateOwnership(memberAuthInfo.id());

        savedCategoryPlaceRepository.deleteAllBySavedCategoryId(savedCategoryId);
        savedCategoryRepository.delete(savedCategory);
    }

    @Transactional(readOnly = true)
    public SavedCategoryReadResponse findSavedCategory(
            final MemberAuthInfo memberAuthInfo,
            final Long savedCategoryId
    ) {
        final var savedCategory = getSavedCategoryById(savedCategoryId);
        savedCategory.validateOwnership(memberAuthInfo.id());

        return SavedCategoryReadResponse.from(savedCategory);
    }

    private void applyName(final String name, final SavedCategory savedCategory) {
        if (!Objects.equals(savedCategory.getName(), name)) {
            savedCategory.updateName(name);
        }
    }

    private void syncSavedCategoryPlaces(
            final List<SavedCategoryUpdateRequest.SavedCategoryPlaceRequest> placeRequests,
            final SavedCategory savedCategory
    ) {
        removeMissingSavedCategoryPlaces(placeRequests, savedCategory);
        addNewSavedCategoryPlaces(placeRequests, savedCategory);
    }

    private void removeMissingSavedCategoryPlaces(
            final List<SavedCategoryUpdateRequest.SavedCategoryPlaceRequest> placeRequests,
            final SavedCategory savedCategory
    ) {
        final Set<Long> requestPlaceIdSet = placeRequests.stream()
                .map(SavedCategoryUpdateRequest.SavedCategoryPlaceRequest::savedCategoryPlaceId)
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
            final List<SavedCategoryUpdateRequest.SavedCategoryPlaceRequest> placeRequests,
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
                final var newSavedCategoryPlace = SavedCategoryPlace.createNew(savedCategory, persistedPlace);
                savedCategoryPlaceRepository.save(newSavedCategoryPlace);
            }
        }
    }

    private SavedCategory getSavedCategoryById(final Long savedCategoryId) {
        return savedCategoryRepository.findById(savedCategoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SAVED_CATEGORY_NOT_FOUND));
    }
}
