package tv.yunxi.fc.oss.zip.utils;

import com.aliyun.fc.runtime.Context;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import tv.yunxi.fc.oss.zip.errors.Exception;
import tv.yunxi.fc.oss.zip.requests.ConfirmedFile;
import tv.yunxi.fc.oss.zip.types.OSSClient;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author moyo
 */
public class Preparing {
    private Logger logger;
    private Context context;

    private String bucket;
    private String region;

    public Preparing(Context context, String bucket, String region) {
        this.logger = new Logger(context.getLogger());
        this.context = context;
        this.bucket = bucket;
        this.region = region;
    }

    public List<ConfirmedFile> confirmFiles(String dir, List<String> files, Map<String, List<String>> grouped) throws Exception {
        OSSClient oss = new OSSClient(String.format("%s.aliyuncs.com", region), context.getExecutionCredentials());
        OSS client = oss.create();

        List<ConfirmedFile> target = Collections.synchronizedList(new ArrayList<>());

        if (dir != null && !dir.isEmpty()) {
            String mew = "/";
            if (!dir.endsWith(mew)) {
                dir = String.format("%s%s", dir, mew);
            }
            ObjectListing dirListing = client.listObjects(bucket, dir);
            List<OSSObjectSummary> dirFiles = dirListing.getObjectSummaries();
            for (OSSObjectSummary file : dirFiles) {
                target.add(new ConfirmedFile(file.getBucketName(), null, file.getKey(), null, file.getETag(), file.getSize()));
            }
        }

        if (files != null && !files.isEmpty()) {
            getFilesMeta(client, bucket, null, files, target);
        }

        if (grouped != null && !grouped.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : grouped.entrySet()) {
                getFilesMeta(client, bucket, entry.getKey(), entry.getValue(), target);
            }
        }

        if (target.isEmpty()) {
            throw new Exception("Non files found");
        }

        return new Coordinator(logger).duplicates(target);
    }

    private void getFilesMeta(OSS client, String bucket, String group, List<String> files, List<ConfirmedFile> target) throws Exception {
        ExecutorService getter = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 32);

        Logger logger = new Logger(context.getLogger());
        CountDownLatch latch = new CountDownLatch(files.size());

        for (String file : files) {
            getter.execute(new FMGetter(latch, logger, client, bucket, group, file, target));
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new Exception(String.format("Files meta getter interrupted %s", e.getMessage()));
        } finally {
            getter.shutdown();
        }
    }
}
