package courseitda.community.application;

import courseitda.common.exception.BusinessException;
import courseitda.common.exception.ErrorCode;
import courseitda.community.domain.SharedCategory;
import courseitda.community.domain.SharedCategoryLike;
import courseitda.community.domain.SharedCategoryLikeRepository;
import courseitda.community.domain.SharedCategoryRepository;
import courseitda.community.ui.dto.response.SharedCategoryLikeCreateResponse;
import courseitda.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SharedCategoryLikeService {

    private final SharedCategoryLikeRepository sharedCategoryLikeRepository;
    private final SharedCategoryRepository sharedCategoryRepository;

    @Transactional
    public SharedCategoryLikeCreateResponse createSharedCategoryLike(
            final Long sharedCategoryId,
            final Member member
    ) {
        final var sharedCategory = getSharedCategoryById(sharedCategoryId);

        if (sharedCategoryLikeRepository.findByMemberIdAndSharedCategoryId(member.getId(), sharedCategoryId)
                .isPresent()) {
            throw new BusinessException(ErrorCode.SHARED_CATEGORY_LIKE_ALREADY_EXISTS);
        }

        final var sharedCategoryLike = SharedCategoryLike.createNew(member, sharedCategory);
        final var persisted = sharedCategoryLikeRepository.save(sharedCategoryLike);

        return SharedCategoryLikeCreateResponse.from(persisted);
    }

    private SharedCategory getSharedCategoryById(final Long sharedCategoryId) {
        return sharedCategoryRepository.findById(sharedCategoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHARED_CATEGORY_NOT_FOUND));
    }
}
