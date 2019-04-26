package tv.yunxi.fc.oss.zip.types;

/**
 * @author moyo
 */
public class FileObject {
    private String group;
    private String name;
    private String alias;
    private byte[] data;

    public FileObject(String path, String group, String alias, byte[] data) {
        this.group = group;
        this.name = path.substring(path.lastIndexOf("/") + 1);
        this.alias = alias;
        this.data = data;
    }

    public String name() {
        String out;

        if (alias != null && !alias.isEmpty()) {
            out = alias;
        } else {
            out = name;
        }

        if (group != null && !group.isEmpty()) {
            out = String.format("%s/%s", group, out);
        }

        return out;
    }

    public byte[] data() {
        return data;
    }
}
