package courseitda.community.domain;

import java.util.concurrent.atomic.AtomicLong;

public class RecommendedCategoryFixture {

    private static final AtomicLong sequence = new AtomicLong(0L);

    public static String anyImageUrl() {
        return "https://s3.amazonaws.com/images/recommended" + sequence.incrementAndGet() + ".jpg";
    }

    public static RecommendedCategory anyRecommendedCategory() {
        return new RecommendedCategoryBuilder().build();
    }
}
