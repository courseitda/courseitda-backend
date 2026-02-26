package courseitda.community.domain;

public interface SharedCategoryPlaceRepository {

    SharedCategoryPlace save(SharedCategoryPlace sharedCategoryPlace);

    void deleteAllBySharedCategoryId(Long sharedCategoryId);
}
