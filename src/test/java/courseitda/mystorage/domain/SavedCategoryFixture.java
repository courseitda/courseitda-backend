package courseitda.mystorage.domain;

import java.util.concurrent.atomic.AtomicLong;

public class SavedCategoryFixture {

    private static final AtomicLong sequenceName = new AtomicLong(0L);

    public static String anyName() {
        return "saved" + sequenceName.incrementAndGet();
    }

    public static SavedCategory anySavedCategory() {
        return new SavedCategoryBuilder().build();
    }
}
