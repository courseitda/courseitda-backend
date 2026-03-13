package courseitda.mystorage.application;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.community.domain.SharedCategory;
import courseitda.community.domain.SharedCategoryRepository;
import courseitda.member.domain.Member;
import courseitda.mystorage.domain.SavedCategory;
import courseitda.mystorage.domain.SavedCategoryPlace;
import courseitda.mystorage.domain.SavedCategoryPlaceRepository;
import courseitda.mystorage.domain.SavedCategoryRepository;
import courseitda.mystorage.ui.dto.request.SavedCategoryCreateRequest;
import courseitda.mystorage.ui.dto.request.SavedCategoryForkRequest;
import courseitda.mystorage.ui.dto.request.SavedCategoryUpdateRequest;
import courseitda.mystorage.ui.dto.response.SavedCategoryCreateResponse;
import courseitda.mystorage.ui.dto.response.SavedCategoryReadResponse;
import courseitda.mystorage.ui.dto.response.SavedCategoryUpdateResponse;
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
    private final SharedCategoryRepository sharedCategoryRepository;

    @Transactional
    public SavedCategoryCreateResponse createSavedCategory(
            final SavedCategoryCreateRequest request,
            final Member member
    ) {
        final var newSavedCategory = SavedCategory.createNew(member, request.name());
        final var persistedSavedCategory = savedCategoryRepository.save(newSavedCategory);

        return SavedCategoryCreateResponse.from(persistedSavedCategory);
    }

    @Transactional
    public SavedCategoryCreateResponse forkSharedCategory(
            final SavedCategoryForkRequest request,
            final Member member
    ) {
        final var sharedCategory = getSharedCategoryById(request.sharedCategoryId());
        final var savedCategory = SavedCategory.createFromShared(member, sharedCategory.getName(),
                sharedCategory.getId());
        final var persistedSavedCategory = savedCategoryRepository.save(savedCategory);

        for (final var sharedCategoryPlace : sharedCategory.getSharedCategoryPlaces()) {
            savedCategoryPlaceRepository.save(
                    SavedCategoryPlace.createNew(persistedSavedCategory, sharedCategoryPlace.getPlace()));
        }

        sharedCategory.incrementForkCount();

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

        savedCategory.updateName(request.name());

        return SavedCategoryUpdateResponse.from(savedCategory);
    }

    @Transactional
    public void deleteSavedCategory(final MemberAuthInfo memberAuthInfo, final Long savedCategoryId) {
        final var savedCategory = getSavedCategoryById(savedCategoryId);
        savedCategory.validateOwnership(memberAuthInfo.id());

        if (savedCategory.hasSource()) {
            sharedCategoryRepository.findByIdIncludingDeleted(savedCategory.getSourceSharedCategoryId())
                    .ifPresent(SharedCategory::decrementForkCount);
        }

        savedCategory.softDelete();
    }

    @Transactional(readOnly = true)
    public SavedCategoryReadResponse findSavedCategory(
            final MemberAuthInfo memberAuthInfo,
            final Long savedCategoryId
    ) {
        final var savedCategory = getSavedCategoryById(savedCategoryId);
        savedCategory.validateOwnership(memberAuthInfo.id());

        final boolean canPublish = hasChangesFromSource(savedCategory);

        return SavedCategoryReadResponse.from(savedCategory, canPublish);
    }

    private boolean hasChangesFromSource(final SavedCategory savedCategory) {
        if (!savedCategory.hasSource()) {
            return true;
        }
        final var sourceSharedCategory = sharedCategoryRepository.findByIdIncludingDeleted(
                savedCategory.getSourceSharedCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SHARED_CATEGORY_NOT_FOUND));

        final Set<Long> sourcePlaceIds = sourceSharedCategory.getSharedCategoryPlaces().stream()
                .map(scp -> scp.getPlace().getId())
                .collect(Collectors.toSet());
        final Set<Long> currentPlaceIds = savedCategory.getSavedCategoryPlaces().stream()
                .map(scp -> scp.getPlace().getId())
                .collect(Collectors.toSet());

        return !sourcePlaceIds.equals(currentPlaceIds);
    }

    private SharedCategory getSharedCategoryById(final Long sharedCategoryId) {
        return sharedCategoryRepository.findById(sharedCategoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHARED_CATEGORY_NOT_FOUND));
    }

    private SavedCategory getSavedCategoryById(final Long savedCategoryId) {
        return savedCategoryRepository.findById(savedCategoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SAVED_CATEGORY_NOT_FOUND));
    }
}
