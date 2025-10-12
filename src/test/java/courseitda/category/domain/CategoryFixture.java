package courseitda.category.domain;

import courseitda.workspace.domain.Workspace;
import courseitda.workspace.domain.WorkspaceFixture;
import java.util.concurrent.atomic.AtomicLong;

public class CategoryFixture {

    public static AtomicLong sequenceName = new AtomicLong(0L);

    public static String anyName() {
        return "category" + sequenceName.incrementAndGet();
    }

    public static String anyColor() {
        return "#FF5733";
    }

    public static Integer anySequence() {
        return 1;
    }

    public static Category anyCategory() {
        return anyCategory(WorkspaceFixture.anyWorkspace());
    }

    public static Category anyCategory(final Workspace workspace) {
        return new CategoryBuilder()
                .workspace(workspace)
                .build();
    }
}
