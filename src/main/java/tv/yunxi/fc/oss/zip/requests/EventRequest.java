package tv.yunxi.fc.oss.zip.requests;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author moyo [auto-gens]
 */
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
    @SerializedName("grouped-files")
    @Expose
    private Map<String, List<String>> groupedFiles = null;
    @SerializedName("confirmed-files")
    @Expose
    private List<ConfirmedFile> confirmedFiles = null;
    @SerializedName("target-file")
    @Expose
    private String targetFile;
    @SerializedName("notify")
    @Expose
    private String notify;
    @SerializedName("process")
    @Expose
    private String process;

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
     * @param confirmedFiles
     * @param targetFile
     * @param notify
     * @param process
     */
    public EventRequest(String region, String bucket, String sourceDir, List<String> sourceFiles, List<ConfirmedFile> confirmedFiles, String targetFile, String notify, String process) {
        super();
        this.region = region;
        this.bucket = bucket;
        this.sourceDir = sourceDir;
        this.sourceFiles = sourceFiles;
        this.confirmedFiles = confirmedFiles;
        this.targetFile = targetFile;
        this.notify = notify;
        this.process = process;
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

    public Map<String, List<String>> getGroupedFiles() {
        return groupedFiles;
    }

    public void setGroupedFiles(Map<String, List<String>> groupedFiles) {
        this.groupedFiles = groupedFiles;
    }

    public EventRequest withGroupedFiles(Map<String, List<String>> groupedFiles) {
        this.groupedFiles = groupedFiles;
        return this;
    }

    public List<ConfirmedFile> getConfirmedFiles() {
        return confirmedFiles;
    }

    public void setConfirmedFiles(List<ConfirmedFile> confirmedFiles) {
        this.confirmedFiles = confirmedFiles;
    }

    public EventRequest withConfirmedFiles(List<ConfirmedFile> confirmedFiles) {
        this.confirmedFiles = confirmedFiles;
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

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }

    public EventRequest withNotify(String notify) {
        this.notify = notify;
        return this;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public EventRequest withProcess(String process) {
        this.process = process;
        return this;
    }

}