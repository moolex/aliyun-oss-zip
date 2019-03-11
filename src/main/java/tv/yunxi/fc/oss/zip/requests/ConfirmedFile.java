package tv.yunxi.fc.oss.zip.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConfirmedFile {

    @SerializedName("bucket")
    @Expose
    private String bucket;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("alias")
    @Expose
    private String alias;
    @SerializedName("etag")
    @Expose
    private String etag;
    @SerializedName("size")
    @Expose
    private Long size;

    /**
     * No args constructor for use in serialization
     *
     */
    public ConfirmedFile() {
    }

    /**
     *
     * @param bucket
     * @param key
     * @param alias
     * @param etag
     * @param size
     */
    public ConfirmedFile(String bucket, String key, String alias, String etag, Long size) {
        super();
        this.bucket = bucket;
        this.key = key;
        this.alias = alias;
        this.etag = etag;
        this.size = size;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public ConfirmedFile withBucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ConfirmedFile withKey(String key) {
        this.key = key;
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public ConfirmedFile withAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public String getETag() {
        return etag;
    }

    public void setETag(String etag) {
        this.etag = etag;
    }

    public ConfirmedFile withETag(String etag) {
        this.etag = etag;
        return this;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public ConfirmedFile withSize(Long size) {
        this.size = size;
        return this;
    }

}