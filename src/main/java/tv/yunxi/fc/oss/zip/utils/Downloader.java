package tv.yunxi.fc.oss.zip.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import org.apache.commons.io.IOUtils;
import tv.yunxi.fc.oss.zip.errors.Exception;
import tv.yunxi.fc.oss.zip.requests.ConfirmedFile;
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
    private final static int THREADS_SIZE = Runtime.getRuntime().availableProcessors();

    private Logger logger;
    private Status status;
    private Packer packer;
    private OSS client;
    private ConfirmedFile file;
    private String process;

    private CountDownLatch latch;

    public static void start(List<ConfirmedFile> files, Status status, Packer packer, Logger logger, OSSClient oss, String process) throws Exception {
        OSS client = oss.create();

        CountDownLatch latch = new CountDownLatch(files.size());

        ExecutorService downloader = Executors.newFixedThreadPool(THREADS_SIZE);

        for (ConfirmedFile file : files) {
            downloader.execute(new Downloader(latch, status, packer, logger, client, file, process));
        }

        try {
            latch.await();
            packer.flush();
        } catch (InterruptedException e) {
            throw new Exception(String.format("Files downloader interrupted %s", e.getMessage()));
        } finally {
            downloader.shutdown();
            client.shutdown();
        }
    }

    private Downloader(CountDownLatch latch, Status status, Packer packer, Logger logger, OSS client, ConfirmedFile file, String process) {
        this.latch = latch;
        this.status = status;
        this.packer = packer;
        this.logger = logger;
        this.client = client;
        this.file = file;
        this.process = process;
    }

    @Override
    public void run() {
        logger.debug(String.format("Start to download file %s [%s:%s]", file.getKey(), file.getGroup(), file.getAlias()));

        OSSObject item;

        if (process == null || process.isEmpty()) {
            item = client.getObject(file.getBucket(), file.getKey());
        } else {
            GetObjectRequest req = new GetObjectRequest(file.getBucket(), file.getKey());
            req.setProcess(process);
            item = client.getObject(req);
        }

        try {
            packer.put(new FileObject(item.getKey(), file.getGroup(), file.getAlias(), IOUtils.toByteArray(item.getObjectContent())));
            status.setDownloaded();
        } catch (InterruptedException | IOException e) {
            logger.warn(String.format("OSS object get fail [%s/%s]", item.getBucketName(), item.getKey()));
            System.exit(1);
        } finally {
            latch.countDown();
        }
    }
}
