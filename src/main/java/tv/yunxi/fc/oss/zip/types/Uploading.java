package tv.yunxi.fc.oss.zip.types;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.CompleteMultipartUploadResult;
import com.aliyun.oss.model.InitiateMultipartUploadResult;

import java.util.concurrent.CountDownLatch;

/**
 * @author moyo
 */
public class Uploading {
    private OSS client;
    private CountDownLatch worker;
    private InitiateMultipartUploadResult meta;
    private FileParts parts = new FileParts();
    private CompleteMultipartUploadResult completed;

    public Uploading(OSS client, CountDownLatch worker, InitiateMultipartUploadResult meta) {
        this.client = client;
        this.worker = worker;
        this.meta = meta;
    }

    public OSS client() {
        return client;
    }

    public CountDownLatch worker() {
        return worker;
    }

    public InitiateMultipartUploadResult meta() {
        return meta;
    }

    public FileParts parts() {
        return parts;
    }

    public void completed(CompleteMultipartUploadResult result) {
        completed = result;
    }

    public CompleteMultipartUploadResult result() {
        return completed;
    }
}
