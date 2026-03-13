package courseitda.community.application;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.community.domain.SharedCategory;
import courseitda.community.domain.SharedCategoryPlace;
import courseitda.community.domain.SharedCategoryPlaceRepository;
import courseitda.community.domain.SharedCategoryRepository;
import courseitda.community.ui.dto.request.SharedCategoryCreateRequest;
import courseitda.community.ui.dto.response.SharedCategoriesReadResponse;
import courseitda.community.ui.dto.response.SharedCategoryCreateResponse;
import courseitda.community.ui.dto.response.SharedCategoryReadResponse;
import courseitda.community.ui.dto.response.SharedCategorySearchResponse;
import courseitda.member.domain.Member;
import courseitda.mystorage.domain.SavedCategory;
import courseitda.mystorage.domain.SavedCategoryRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SharedCategoryService {

    private final SavedCategoryRepository savedCategoryRepository;
    private final SharedCategoryRepository sharedCategoryRepository;
    private final SharedCategoryPlaceRepository sharedCategoryPlaceRepository;

    @Transactional
    public SharedCategoryCreateResponse createSharedCategory(
            final SharedCategoryCreateRequest request,
            final Member member
    ) {

        final var savedCategory = getSavedCategoryById(request.savedCategoryId());

        savedCategory.validateOwnership(member.getId());

        if (savedCategory.hasSource()) {
            validatePlacesModified(savedCategory);
        }

        final var newSharedCategory = createSharedCategoryFromSavedCategory(savedCategory, member);
        final var sharedCategory = sharedCategoryRepository.save(newSharedCategory);
        sharedCategory.initializeRoot();

        for (var savedCategoryPlace : savedCategory.getSavedCategoryPlaces()) {
            final var newSharedCategoryPlace = SharedCategoryPlace.createNew(sharedCategory,
                    savedCategoryPlace.getPlace());
            sharedCategoryPlaceRepository.save(newSharedCategoryPlace);
        }

        // 주의: sharedCategoryPlaces 응답에 포함 시 재조회 필요 (JPA 1차 캐시 불일치)
        return SharedCategoryCreateResponse.from(sharedCategory);
    }

    @Transactional
    public void deleteSharedCategory(final MemberAuthInfo memberAuthInfo, final Long sharedCategoryId) {
        final var sharedCategory = getSharedCategoryById(sharedCategoryId);
        sharedCategory.validateOwnership(memberAuthInfo.id());

        sharedCategory.softDelete();
    }

    @Transactional(readOnly = true)
    public SharedCategoryReadResponse findSharedCategory(final Long sharedCategoryId) {
        final var sharedCategory = getSharedCategoryById(sharedCategoryId);

        return SharedCategoryReadResponse.from(sharedCategory);
    }

    @Transactional(readOnly = true)
    public SharedCategoriesReadResponse findAllSharedCategories(final Long cursor, final int size) {
        validatePageSize(size);
        final var sharedCategories = findSharedCategoriesByCursor(cursor, size + 1);
        final boolean hasNext = sharedCategories.size() > size;

        if (hasNext) {
            return SharedCategoriesReadResponse.from(sharedCategories.subList(0, size), hasNext,
                    sharedCategories.get(size - 1).getId());
        }
        return SharedCategoriesReadResponse.from(sharedCategories, hasNext, null);
    }

    @Transactional(readOnly = true)
    public SharedCategorySearchResponse searchSharedCategories(
            final String keyword,
            final Long cursor,
            final int size
    ) {
        validateKeyword(keyword);
        validatePageSize(size);

        final var sharedCategories = findSharedCategoriesByKeywordAndCursor(keyword, cursor, size + 1);
        final boolean hasNext = sharedCategories.size() > size;

        if (hasNext) {
            return SharedCategorySearchResponse.from(sharedCategories.subList(0, size), hasNext,
                    sharedCategories.get(size - 1).getId());
        }
        return SharedCategorySearchResponse.from(sharedCategories, hasNext, null);
    }

    private void validatePlacesModified(final SavedCategory savedCategory) {
        final var source = getSharedCategoryByIdIncludingDeleted(savedCategory.getSourceSharedCategoryId());

        final Set<Long> sourcePlaceIds = source.getSharedCategoryPlaces().stream()
                .map(scp -> scp.getPlace().getId())
                .collect(Collectors.toSet());

        final Set<Long> currentPlaceIds = savedCategory.getSavedCategoryPlaces().stream()
                .map(scp -> scp.getPlace().getId())
                .collect(Collectors.toSet());

        if (sourcePlaceIds.equals(currentPlaceIds)) {
            throw new BusinessException(ErrorCode.SAVED_CATEGORY_PLACES_NOT_MODIFIED);
        }
    }

    private void validatePageSize(final int size) {
        if (size < 1 || size > 100) {
            throw new BusinessException(ErrorCode.INVALID_SHARED_CATEGORY_SIZE);
        }
    }

    private void validateKeyword(final String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new BusinessException(ErrorCode.BLANK_SHARED_CATEGORY_SEARCH_KEYWORD);
        }
    }

    private SharedCategory createSharedCategoryFromSavedCategory(final SavedCategory savedCategory,
            final Member member) {
        if (savedCategory.hasSource()) {
            final var parentSharedCategoryId = savedCategory.getSourceSharedCategoryId();
            final var parentSharedCategory = getSharedCategoryByIdIncludingDeleted(parentSharedCategoryId);
            final var rootSharedCategoryId = parentSharedCategory.getRootSharedCategoryId();

            return SharedCategory.createChild(savedCategory.getName(), member, rootSharedCategoryId,
                    parentSharedCategoryId);
        }

        return SharedCategory.createRoot(savedCategory.getName(), member);
    }

    private List<SharedCategory> findSharedCategoriesByCursor(final Long cursor, final int limit) {
        if (cursor == null) {
            return sharedCategoryRepository.findAllOrderByIdDesc(limit);
        }
        return sharedCategoryRepository.findAllByIdLessThanOrderByIdDesc(cursor, limit);
    }

    private List<SharedCategory> findSharedCategoriesByKeywordAndCursor(
            final String keyword,
            final Long cursor,
            final int limit
    ) {
        if (cursor == null) {
            return sharedCategoryRepository.findAllByNameContainingOrderByIdDesc(keyword, limit);
        }
        return sharedCategoryRepository.findAllByNameContainingAndIdLessThanOrderByIdDesc(keyword, cursor, limit);
    }

    private SharedCategory getSharedCategoryById(final Long sharedCategoryId) {
        return sharedCategoryRepository.findById(sharedCategoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHARED_CATEGORY_NOT_FOUND));
    }

    private SharedCategory getSharedCategoryByIdIncludingDeleted(final Long sharedCategoryId) {
        return sharedCategoryRepository.findByIdIncludingDeleted(sharedCategoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHARED_CATEGORY_NOT_FOUND));
    }

    private SavedCategory getSavedCategoryById(final Long savedCategoryId) {
        return savedCategoryRepository.findById(savedCategoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SAVED_CATEGORY_NOT_FOUND));
    }
}
