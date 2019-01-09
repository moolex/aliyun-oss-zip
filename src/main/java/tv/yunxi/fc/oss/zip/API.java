package tv.yunxi.fc.oss.zip;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.StreamRequestHandler;
import com.google.gson.Gson;
import tv.yunxi.fc.oss.zip.requests.ApiRequest;
import tv.yunxi.fc.oss.zip.requests.ApiResponse;
import tv.yunxi.fc.oss.zip.requests.EventResponse;

import java.io.*;
import java.util.Base64;

/**
 * @author moyo
 */
public class API implements StreamRequestHandler {
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        ApiRequest request = new Gson().fromJson(new InputStreamReader(input), ApiRequest.class);

        ApiResponse response = new ApiResponse();

        response.setIsBase64Encoded(false);

        byte[] data;
        if (request.getIsBase64Encoded()) {
            data = Base64.getDecoder().decode(request.getBody());
        } else {
            data = request.getBody().getBytes();
        }

        Ingress ingress = new Ingress();

        OutputStream buffer = new ByteArrayOutputStream();

        try {
            ingress.handleRequest(new ByteArrayInputStream(data), buffer, context);
            EventResponse processed = new Gson().fromJson(buffer.toString(), EventResponse.class);
            response.withStatusCode(processed.getCode() == 0 ? 200 : 500).setBody(buffer.toString());
        } catch (Exception e) {
            response.withStatusCode(500).setBody(e.toString());
        }

        output.write(new Gson().toJson(response).getBytes());
    }
}
