package tv.yunxi.fc.oss.zip.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import org.apache.commons.io.IOUtils;
import tv.yunxi.fc.oss.zip.errors.Exception;
import tv.yunxi.fc.oss.zip.sync.Status;
import tv.yunxi.fc.oss.zip.types.FileObject;
import tv.yunxi.fc.oss.zip.types.OSSClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author moyo
 */
public class Downloader implements Runnable {
    private final static int THREADS_SIZE = 4;

    private Logger logger;
    private Status status;
    private Packer packer;
    private OSS client;
    private OSSObjectSummary file;

    private CountDownLatch latch;

    public static void start(List<OSSObjectSummary> files, Status status, Packer packer, Logger logger, OSSClient oss) throws Exception {
        OSS client = oss.create();

        CountDownLatch latch = new CountDownLatch(files.size());

        ExecutorService downloader = Executors.newFixedThreadPool(THREADS_SIZE);

        for (OSSObjectSummary file : files) {
            downloader.execute(new Downloader(latch, status, packer, logger, client, file));
        }

        try {
            latch.await();
            packer.flush();
        } catch (InterruptedException e) {
            throw new Exception(String.format("Files downloader interrupted %s", e.getMessage()));
        } finally {
            client.shutdown();
        }
    }

    private Downloader(CountDownLatch latch, Status status, Packer packer, Logger logger, OSS client, OSSObjectSummary file) {
        this.latch = latch;
        this.status = status;
        this.packer = packer;
        this.logger = logger;
        this.client = client;
        this.file = file;
    }

    @Override
    public void run() {
        logger.debug(String.format("Start to download file %s", file.getKey()));

        OSSObject item = client.getObject(file.getBucketName(), file.getKey());

        try {
            this.packer.put(new FileObject(item.getKey(), IOUtils.toByteArray(item.getObjectContent())));
            this.latch.countDown();
            status.setDownloaded();
        } catch (InterruptedException | IOException e) {
            logger.warn(String.format("OSS object get fail [%s/%s]", item.getBucketName(), item.getKey()));
            System.exit(1);
        }
    }
}
