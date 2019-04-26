package tv.yunxi.fc.oss.zip.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * @author moyo [auto-gens]
 */
public class ApiResponse {

    @SerializedName("isBase64Encoded")
    @Expose
    private Boolean isBase64Encoded;
    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("headers")
    @Expose
    private Map<String, String> headers;
    @SerializedName("body")
    @Expose
    private String body;

    /**
     * No args constructor for use in serialization
     *
     */
    public ApiResponse() {
    }

    /**
     *
     * @param headers
     * @param statusCode
     * @param body
     * @param isBase64Encoded
     */
    public ApiResponse(Boolean isBase64Encoded, Integer statusCode, Map<String, String> headers, String body) {
        super();
        this.isBase64Encoded = isBase64Encoded;
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public Boolean getIsBase64Encoded() {
        return isBase64Encoded;
    }

    public void setIsBase64Encoded(Boolean isBase64Encoded) {
        this.isBase64Encoded = isBase64Encoded;
    }

    public ApiResponse withIsBase64Encoded(Boolean isBase64Encoded) {
        this.isBase64Encoded = isBase64Encoded;
        return this;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public ApiResponse withStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public ApiResponse withHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ApiResponse withBody(String body) {
        this.body = body;
        return this;
    }

}