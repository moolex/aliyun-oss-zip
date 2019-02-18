package tv.yunxi.fc.oss.zip.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.SimplifiedObjectMeta;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author moyo
 * file mete getter
 */
public class FMGetter implements Runnable {
    private CountDownLatch latch;
    private OSS client;
    private String bucket;
    private String file;
    private List<OSSObjectSummary> target;

    FMGetter(CountDownLatch latch, OSS client, String bucket, String file, List<OSSObjectSummary> target) {
        this.latch = latch;
        this.client = client;
        this.bucket = bucket;
        this.file = file;
        this.target = target;
    }

    @Override
    public void run() {
        try {
            SimplifiedObjectMeta meta = client.getSimplifiedObjectMeta(bucket, file);
            OSSObjectSummary s = new OSSObjectSummary();
            s.setBucketName(bucket);
            s.setKey(file);
            s.setETag(meta.getETag());
            s.setSize(meta.getSize());
            target.add(s);
        } finally {
            latch.countDown();
        }
    }
}
