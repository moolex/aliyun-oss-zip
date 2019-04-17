package tv.yunxi.fc.oss.zip.types;

/**
 * @author moyo
 */
public class FileObject {
    private String name;
    private String alias;
    private byte[] data;

    public FileObject(String path, String alias, byte[] data) {
        this.name = path.substring(path.lastIndexOf("/") + 1);
        this.alias = alias;
        this.data = data;
    }

    public String name() {
        if (alias != null && !alias.isEmpty()) {
            return alias;
        }
        return name;
    }

    public byte[] data() {
        return data;
    }
}
