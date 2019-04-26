package tv.yunxi.fc.oss.zip.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * @author moyo [auto-gens]
 */
public class ApiRequest {

    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("httpMethod")
    @Expose
    private String httpMethod;
    @SerializedName("headers")
    @Expose
    private Map<String, String> headers;
    @SerializedName("queryParameters")
    @Expose
    private Map<String, String> queryParameters;
    @SerializedName("pathParameters")
    @Expose
    private Map<String, String> pathParameters;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("isBase64Encoded")
    @Expose
    private Boolean isBase64Encoded;

    /**
     * No args constructor for use in serialization
     *
     */
    public ApiRequest() {
    }

    /**
     *
     * @param headers
     * @param body
     * @param pathParameters
     * @param httpMethod
     * @param isBase64Encoded
     * @param path
     * @param queryParameters
     */
    public ApiRequest(String path, String httpMethod, Map<String, String> headers, Map<String, String> queryParameters, Map<String, String> pathParameters, String body, Boolean isBase64Encoded) {
        super();
        this.path = path;
        this.httpMethod = httpMethod;
        this.headers = headers;
        this.queryParameters = queryParameters;
        this.pathParameters = pathParameters;
        this.body = body;
        this.isBase64Encoded = isBase64Encoded;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ApiRequest withPath(String path) {
        this.path = path;
        return this;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public ApiRequest withHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public ApiRequest withHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(Map<String, String> queryParameters) {
        this.queryParameters = queryParameters;
    }

    public ApiRequest withQueryParameters(Map<String, String> queryParameters) {
        this.queryParameters = queryParameters;
        return this;
    }

    public Map<String, String> getPathParameters() {
        return pathParameters;
    }

    public void setPathParameters(Map<String, String> pathParameters) {
        this.pathParameters = pathParameters;
    }

    public ApiRequest withPathParameters(Map<String, String> pathParameters) {
        this.pathParameters = pathParameters;
        return this;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ApiRequest withBody(String body) {
        this.body = body;
        return this;
    }

    public Boolean getIsBase64Encoded() {
        return isBase64Encoded;
    }

    public void setIsBase64Encoded(Boolean isBase64Encoded) {
        this.isBase64Encoded = isBase64Encoded;
    }

    public ApiRequest withIsBase64Encoded(Boolean isBase64Encoded) {
        this.isBase64Encoded = isBase64Encoded;
        return this;
    }

}
