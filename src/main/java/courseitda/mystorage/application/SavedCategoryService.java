package courseitda.mystorage.application;

import courseitda.auth.domain.MemberAuthInfo;
import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.member.domain.Member;
import courseitda.mystorage.domain.SavedCategory;
import courseitda.mystorage.domain.SavedCategoryRepository;
import courseitda.mystorage.ui.dto.request.SavedCategoryCreateRequest;
import courseitda.mystorage.ui.dto.request.SavedCategoryUpdateRequest;
import courseitda.mystorage.ui.dto.response.SavedCategoryCreateResponse;
import courseitda.mystorage.ui.dto.response.SavedCategoryReadResponse;
import courseitda.mystorage.ui.dto.response.SavedCategoryUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SavedCategoryService {

    private final SavedCategoryRepository savedCategoryRepository;

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

        savedCategory.softDelete();
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

    private SavedCategory getSavedCategoryById(final Long savedCategoryId) {
        return savedCategoryRepository.findById(savedCategoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SAVED_CATEGORY_NOT_FOUND));
    }
}
