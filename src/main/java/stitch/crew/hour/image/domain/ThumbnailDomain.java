package stitch.crew.hour.image.domain;

public enum ThumbnailDomain {
    PRODUCT, CATEGORY;
    public static ThumbnailDomain from(String domain) {
        for (ThumbnailDomain d : ThumbnailDomain.values()) {
            if (d.name().equalsIgnoreCase(domain)) {
                return d;
            }
        }
        return null;
    }
}
