package tv.yunxi.fc.oss.zip.types;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author moyo
 */
public class Progress {

    @SerializedName("scheduled")
    @Expose
    private Integer scheduled;
    @SerializedName("downloaded")
    @Expose
    private Integer downloaded;
    @SerializedName("packed")
    @Expose
    private Integer packed;
    @SerializedName("uploaded")
    @Expose
    private Long uploaded;
    @SerializedName("finished")
    @Expose
    private Boolean finished;
    @SerializedName("location")
    @Expose
    private String location;

    /**
     * No args constructor for use in serialization
     *
     */
    public Progress() {
    }

    /**
     *
     * @param scheduled
     * @param location
     * @param packed
     * @param uploaded
     * @param finished
     * @param downloaded
     */
    public Progress(Integer scheduled, Integer downloaded, Integer packed, Long uploaded, Boolean finished, String location) {
        super();
        this.scheduled = scheduled;
        this.downloaded = downloaded;
        this.packed = packed;
        this.uploaded = uploaded;
        this.finished = finished;
        this.location = location;
    }

    public Integer getScheduled() {
        return scheduled;
    }

    public void setScheduled(Integer scheduled) {
        this.scheduled = scheduled;
    }

    public Progress withScheduled(Integer scheduled) {
        this.scheduled = scheduled;
        return this;
    }

    public Integer getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(Integer downloaded) {
        this.downloaded = downloaded;
    }

    public Progress withDownloaded(Integer downloaded) {
        this.downloaded = downloaded;
        return this;
    }

    public Integer getPacked() {
        return packed;
    }

    public void setPacked(Integer packed) {
        this.packed = packed;
    }

    public Progress withPacked(Integer packed) {
        this.packed = packed;
        return this;
    }

    public Long getUploaded() {
        return uploaded;
    }

    public void setUploaded(Long uploaded) {
        this.uploaded = uploaded;
    }

    public Progress withUploaded(Long uploaded) {
        this.uploaded = uploaded;
        return this;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public Progress withFinished(Boolean finished) {
        this.finished = finished;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Progress withLocation(String location) {
        this.location = location;
        return this;
    }

}