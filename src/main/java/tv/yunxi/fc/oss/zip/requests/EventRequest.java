package tv.yunxi.fc.oss.zip.requests;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventRequest {

    @SerializedName("region")
    @Expose
    private String region;
    @SerializedName("bucket")
    @Expose
    private String bucket;
    @SerializedName("source-dir")
    @Expose
    private String sourceDir;
    @SerializedName("source-files")
    @Expose
    private List<String> sourceFiles = null;
    @SerializedName("target-file")
    @Expose
    private String targetFile;

    /**
     * No args constructor for use in serialization
     *
     */
    public EventRequest() {
    }

    /**
     *
     * @param region
     * @param sourceDir
     * @param bucket
     * @param sourceFiles
     * @param targetFile
     */
    public EventRequest(String region, String bucket, String sourceDir, List<String> sourceFiles, String targetFile) {
        super();
        this.region = region;
        this.bucket = bucket;
        this.sourceDir = sourceDir;
        this.sourceFiles = sourceFiles;
        this.targetFile = targetFile;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public EventRequest withRegion(String region) {
        this.region = region;
        return this;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public EventRequest withBucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public EventRequest withSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
        return this;
    }

    public List<String> getSourceFiles() {
        return sourceFiles;
    }

    public void setSourceFiles(List<String> sourceFiles) {
        this.sourceFiles = sourceFiles;
    }

    public EventRequest withSourceFiles(List<String> sourceFiles) {
        this.sourceFiles = sourceFiles;
        return this;
    }

    public String getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(String targetFile) {
        this.targetFile = targetFile;
    }

    public EventRequest withTargetFile(String targetFile) {
        this.targetFile = targetFile;
        return this;
    }

}