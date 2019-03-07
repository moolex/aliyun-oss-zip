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
    private Context context;

    private String bucket;
    private String region;

    public Preparing(Context context, String bucket, String region) {
        this.context = context;
        this.bucket = bucket;
        this.region = region;
    }

    public List<ConfirmedFile> confirmFiles(String dir, List<String> files) throws Exception {
        OSSClient oss = new OSSClient(String.format("%s.aliyuncs.com", region), context.getExecutionCredentials());
        OSS client = oss.create();

        List<OSSObjectSummary> target = Collections.synchronizedList(new ArrayList<>());

        if (dir != null && !dir.isEmpty()) {
            String mew = "/";
            if (!dir.endsWith(mew)) {
                dir = String.format("%s%s", dir, mew);
            }
            ObjectListing dirListing = client.listObjects(bucket, dir);
            List<OSSObjectSummary> dirFiles = dirListing.getObjectSummaries();
            target.addAll(dirFiles);
        }

        if (files != null && !files.isEmpty()) {
            getFilesMeta(client, bucket, files, target);
        }

        if (target.isEmpty()) {
            throw new Exception("Non files found");
        }

        Set<OSSObjectSummary> confirmed = new TreeSet<>((f1, f2) -> f1.getKey().compareTo(f2.getKey()));
        confirmed.addAll(target);

        List<ConfirmedFile> gFiles = new ArrayList<>();

        for (OSSObjectSummary file : confirmed) {
            gFiles.add(new ConfirmedFile(file.getBucketName(), file.getKey(), file.getETag(), file.getSize()));
        }

        return gFiles;
    }

    private void getFilesMeta(OSS client, String bucket, List<String> files, List<OSSObjectSummary> target) throws Exception {
        ExecutorService getter = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 32);

        CountDownLatch latch = new CountDownLatch(files.size());

        for (String file : files) {
            getter.execute(new FMGetter(latch, client, bucket, file, target));
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
