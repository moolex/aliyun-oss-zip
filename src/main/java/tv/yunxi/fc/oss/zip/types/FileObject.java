package tv.yunxi.fc.oss.zip.types;

/**
 * @author moyo
 */
public class FileObject {
    private String name;
    private byte[] data;

    public FileObject(String path, byte[] data) {
        this.name = path.substring(path.lastIndexOf("/") + 1);
        this.data = data;
    }

    public String name() {
        return name;
    }

    public byte[] data() {
        return data;
    }
}
