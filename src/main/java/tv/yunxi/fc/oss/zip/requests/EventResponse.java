package tv.yunxi.fc.oss.zip.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author moyo [auto-gens]
 */
public class EventResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ResponseData data;

    /**
     * No args constructor for use in serialization
     *
     */
    public EventResponse() {
    }

    /**
     *
     * @param message
     * @param data
     * @param code
     */
    public EventResponse(Integer code, String message, ResponseData data) {
        super();
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public EventResponse withCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EventResponse withMessage(String message) {
        this.message = message;
        return this;
    }

    public ResponseData getData() {
        return data;
    }

    public void setData(ResponseData data) {
        this.data = data;
    }

    public EventResponse withData(ResponseData data) {
        this.data = data;
        return this;
    }

}