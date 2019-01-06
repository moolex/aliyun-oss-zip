package tv.yunxi.fc.oss.zip;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.StreamRequestHandler;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.SimplifiedObjectMeta;
import com.google.gson.Gson;
import tv.yunxi.fc.oss.zip.errors.Exception;
import tv.yunxi.fc.oss.zip.requests.EventRequest;
import tv.yunxi.fc.oss.zip.requests.EventResponse;
import tv.yunxi.fc.oss.zip.requests.ResponseData;
import tv.yunxi.fc.oss.zip.sync.Buffer;
import tv.yunxi.fc.oss.zip.types.OSSClient;
import tv.yunxi.fc.oss.zip.types.Uploading;
import tv.yunxi.fc.oss.zip.utils.Packer;
import tv.yunxi.fc.oss.zip.utils.Downloader;
import tv.yunxi.fc.oss.zip.utils.Logger;
import tv.yunxi.fc.oss.zip.utils.Uploader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author moyo
 */
public class Ingress implements StreamRequestHandler {
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        EventRequest request = new Gson().fromJson(new InputStreamReader(input), EventRequest.class);

        EventResponse response = new EventResponse();

        try {
            String url = this.process(request, context);
            response.withCode(0).withData(new ResponseData(url));
        } catch (Exception e) {
            response.withCode(e.hashCode()).withMessage(e.toString());
        }

        output.write(new Gson().toJson(response).getBytes());
    }

    private String process(EventRequest request, Context context) throws Exception {
        String bucket = request.getBucket();

        OSSClient oss = new OSSClient(String.format("%s.aliyuncs.com", request.getRegion()), context.getExecutionCredentials());
        OSS client = oss.create();

        String output = request.getTargetFile();
        if (output == null || output.isEmpty()) {
            throw new Exception("Target file name missing");
        } else {
            output = String.format(output, context.getRequestId().replace("-", ""));
        }

        List<OSSObjectSummary> target = new ArrayList<>();

        String pkDir = request.getSourceDir();
        if (pkDir != null && !pkDir.isEmpty()) {
            String mew = "/";
            if (!pkDir.endsWith(mew)) {
                pkDir = String.format("%s%s", pkDir, mew);
            }
            ObjectListing dirListing = client.listObjects(bucket, pkDir);
            List<OSSObjectSummary> dirFiles = dirListing.getObjectSummaries();
            target.addAll(dirFiles);
        }

        List<String> pkFiles = request.getSourceFiles();
        if (pkFiles != null && !pkFiles.isEmpty()) {
            for (String file : pkFiles) {
                SimplifiedObjectMeta meta = client.getSimplifiedObjectMeta(bucket, file);
                OSSObjectSummary s = new OSSObjectSummary();
                s.setBucketName(bucket);
                s.setKey(file);
                s.setETag(meta.getETag());
                s.setSize(meta.getSize());
                target.add(s);
            }
        }

        if (target.isEmpty()) {
            throw new Exception("Non files found");
        }

        Logger logger = new Logger(context.getLogger());
        Buffer buffer = new Buffer(logger);

        CountDownLatch master = new CountDownLatch(2);

        Uploading upload = Uploader.manager(master, logger, oss, bucket, output);

        Uploader.start(buffer, logger, upload);

        Downloader.start(target, Packer.start(master, buffer, logger), logger, oss);

        try {
            master.await();
        } catch (InterruptedException e) {
            logger.warn(String.format("Sub threads wait fail %s", e.getMessage()));
        }

        client.shutdown();

        return upload.result().getLocation();
    }
}
