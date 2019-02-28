package tv.yunxi.fc.oss.zip;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.StreamRequestHandler;
import com.aliyun.oss.OSS;
import com.google.gson.Gson;
import tv.yunxi.fc.oss.zip.errors.Exception;
import tv.yunxi.fc.oss.zip.requests.ConfirmedFile;
import tv.yunxi.fc.oss.zip.requests.EventRequest;
import tv.yunxi.fc.oss.zip.requests.EventResponse;
import tv.yunxi.fc.oss.zip.requests.ResponseData;
import tv.yunxi.fc.oss.zip.sync.Buffer;
import tv.yunxi.fc.oss.zip.sync.Status;
import tv.yunxi.fc.oss.zip.types.OSSClient;
import tv.yunxi.fc.oss.zip.types.Uploading;
import tv.yunxi.fc.oss.zip.utils.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.zip.GZIPInputStream;

/**
 * @author moyo
 */
public class Ingress implements StreamRequestHandler {
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        EventRequest request = new Gson().fromJson(
                new InputStreamReader(
                    "true".equals(System.getenv("local"))
                        ? input
                        : new GZIPInputStream(input)
                ),
                EventRequest.class
        );

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
        OSSClient oss = new OSSClient(String.format("%s.aliyuncs.com", request.getRegion()), context.getExecutionCredentials());
        OSS client = oss.create();

        String output = request.getTargetFile();
        if (output == null || output.isEmpty()) {
            throw new Exception("Target file name missing");
        } else {
            output = String.format(output, context.getRequestId().replace("-", ""));
        }

        List<ConfirmedFile> files = request.getConfirmedFiles();

        if (files == null || files.isEmpty()) {
            Preparing preparing = new Preparing(context, request.getBucket(), request.getRegion());
            files = preparing.confirmFiles(request.getSourceDir(), request.getSourceFiles());
        }

        Logger logger = new Logger(context.getLogger());
        Buffer buffer = new Buffer(logger);
        Status status = new Status(files.size());

        logger.info(String.format("Start to packing with %d files in %s", files.size(), request.getBucket()));

        Notify notify = Notify.watch(logger, status, request.getNotify());

        CountDownLatch master = new CountDownLatch(2);

        Uploading upload = Uploader.manager(master, logger, oss, request.getBucket(), output);

        Uploader.start(status, buffer, logger, upload);

        Downloader.start(files, status, Packer.start(master, status, buffer, logger), logger, oss, request.getProcess());

        try {
            master.await();
        } catch (InterruptedException e) {
            logger.warn(String.format("Sub threads wait fail %s", e.getMessage()));
        }

        client.shutdown();

        notify.stop(upload.result().getLocation());

        return upload.result().getLocation();
    }
}
