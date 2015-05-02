    package javaanpr.Imageproperties;
    final public class ImageProperties {
    final public int width;
    final public int height;
    final public char[] comment;
    final public String type;

    public ImageProperties(int width, int height, char[] comment, String type) {
        this.width = width;
        this.height = height;
        this.comment = comment;
        this.type = type;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(type).append(" ").append(width).append("x").append(height);
        if (comment != null) {
            sb.append(" (").append(comment).append(")");
        }
        return (sb.toString());
    }
}



