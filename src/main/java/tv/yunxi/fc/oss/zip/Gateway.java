package tv.yunxi.fc.oss.zip;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.StreamRequestHandler;
import com.aliyuncs.fc.client.FunctionComputeClient;
import com.aliyuncs.fc.config.Config;
import com.aliyuncs.fc.constants.Const;
import com.aliyuncs.fc.request.InvokeFunctionRequest;
import com.aliyuncs.fc.response.InvokeFunctionResponse;
import com.google.gson.Gson;
import tv.yunxi.fc.oss.zip.errors.Exception;
import tv.yunxi.fc.oss.zip.requests.*;
import tv.yunxi.fc.oss.zip.utils.Preparing;

import java.io.*;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

/**
 * @author moyo
 */
public class Gateway implements StreamRequestHandler {
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        ApiRequest request = new Gson().fromJson(new InputStreamReader(input), ApiRequest.class);
        if (request.getBody() == null) {
            context.getLogger().warn("Request payload decode failed (is null)");
            return;
        }

        ApiResponse response = new ApiResponse();

        response.setIsBase64Encoded(false);

        // get request payload
        byte[] data;
        if (request.getIsBase64Encoded()) {
            data = Base64.getDecoder().decode(request.getBody());
        } else {
            data = request.getBody().getBytes();
        }

        EventRequest event = new Gson().fromJson(new InputStreamReader(new ByteArrayInputStream(data)), EventRequest.class);

        // create new fc client
        FunctionComputeClient fc = new FunctionComputeClient(new Config(
                System.getenv("loc"),
                System.getenv("uid"),
                context.getExecutionCredentials().getAccessKeyId(),
                context.getExecutionCredentials().getAccessKeySecret(),
                context.getExecutionCredentials().getSecurityToken(),
                false
        ));

        InvokeFunctionRequest fr = new InvokeFunctionRequest(
                System.getenv("svc"),
                System.getenv("func")
        );

        // invoke func async
        fr.setInvocationType(Const.INVOCATION_TYPE_ASYNC);

        try {
            Preparing preparing = new Preparing(context, event.getBucket(), event.getRegion());

            // new event with confirmed files
            event
                .withConfirmedFiles(
                    preparing.confirmFiles(event.getSourceDir(), event.getSourceFiles())
                )
                .withSourceDir(null)
                .withSourceFiles(null)
            ;

            ByteArrayOutputStream gzip = new ByteArrayOutputStream();
            GZIPOutputStream gzs = new GZIPOutputStream(gzip);
            gzs.write(new Gson().toJson(event).getBytes());
            gzs.close();

            fr.setPayload(gzip.toByteArray());

            InvokeFunctionResponse ivr = fc.invokeFunction(fr);

            response.withStatusCode(ivr.getStatus());
        } catch (Exception e) {
            response.withStatusCode(500).setBody(e.toString());
        }

        output.write(new Gson().toJson(response).getBytes());
    }
}
