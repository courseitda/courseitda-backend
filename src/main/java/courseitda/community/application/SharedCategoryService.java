package courseitda.community.application;

import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.community.domain.SharedCategory;
import courseitda.community.domain.SharedCategoryPlace;
import courseitda.community.domain.SharedCategoryPlaceRepository;
import courseitda.community.domain.SharedCategoryRepository;
import courseitda.community.ui.dto.request.SharedCategoryCreateRequest;
import courseitda.community.ui.dto.response.SharedCategoryCreateResponse;
import courseitda.member.domain.Member;
import courseitda.mystorage.domain.SavedCategory;
import courseitda.mystorage.domain.SavedCategoryRepository;
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
    public SharedCategoryCreateResponse createSharedCategory(final SharedCategoryCreateRequest request,
                                                             final Member member) {

        final var savedCategory = getSavedCategoryById(request.savedCategoryId());

        final var newSharedCategory = SharedCategory.createNew(savedCategory.getName(), member);
        final var sharedCategory = sharedCategoryRepository.save(newSharedCategory);

        for (var savedCategoryPlace : savedCategory.getSavedCategoryPlaces()) {
            final var newSharedCategoryPlace = SharedCategoryPlace.createNew(sharedCategory,
                    savedCategoryPlace.getPlace());
            sharedCategoryPlaceRepository.save(newSharedCategoryPlace);
        }

        // 주의: sharedCategoryPlaces 응답에 포함 시 재조회 필요 (JPA 1차 캐시 불일치)
        return SharedCategoryCreateResponse.from(sharedCategory);
    }

    private SavedCategory getSavedCategoryById(final Long savedCategoryId) {
        return savedCategoryRepository.findById(savedCategoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SAVED_CATEGORY_NOT_FOUND));
    }
}
