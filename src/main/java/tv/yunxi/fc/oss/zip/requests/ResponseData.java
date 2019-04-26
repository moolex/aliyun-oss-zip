package tv.yunxi.fc.oss.zip.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author moyo [auto-gens]
 */
public class ResponseData {

    @SerializedName("url")
    @Expose
    private String url;

    /**
     * No args constructor for use in serialization
     *
     */
    public ResponseData() {
    }

    /**
     *
     * @param url
     */
    public ResponseData(String url) {
        super();
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ResponseData withUrl(String url) {
        this.url = url;
        return this;
    }

}