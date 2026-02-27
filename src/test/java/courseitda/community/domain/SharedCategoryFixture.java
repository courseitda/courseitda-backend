package courseitda.community.domain;

import java.util.concurrent.atomic.AtomicLong;

public class SharedCategoryFixture {

    private static final AtomicLong sequence = new AtomicLong(0L);

    public static String anyName() {
        return "shared" + sequence.incrementAndGet();
    }

    public static SharedCategory anySharedCategory() {
        return new SharedCategoryBuilder().build();
    }
}
