package page;

/*
 * Record Identifier.
 *
 * Identifies a row location.
 */
public class RID {

    private int pageId;

    private int slotId;

    public RID(
            int pageId,
            int slotId) {

        this.pageId =
                pageId;

        this.slotId =
                slotId;
    }

    public int getPageId() {

        return pageId;
    }

    public int getSlotId() {

        return slotId;
    }

    @Override
    public String toString() {

        return "("
                + pageId
                + ", "
                + slotId
                + ")";
    }
}