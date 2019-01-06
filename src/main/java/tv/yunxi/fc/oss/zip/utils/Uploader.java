package tv.yunxi.fc.oss.zip.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;
import tv.yunxi.fc.oss.zip.sync.Buffer;
import tv.yunxi.fc.oss.zip.types.FileParts;
import tv.yunxi.fc.oss.zip.types.OSSClient;
import tv.yunxi.fc.oss.zip.types.Uploading;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author moyo
 */
public class Uploader implements Runnable {
    private final static int THREADS_SIZE = 4;

    private CountDownLatch worker;
    private Buffer buffer;
    private Logger logger;
    private OSS client;
    private InitiateMultipartUploadResult upload;
    private FileParts parts;

    public static void start(Buffer buffer, Logger logger, Uploading uploading) {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS_SIZE);

        for (int i = 0; i < THREADS_SIZE; i ++ ) {
            executor.execute(new Uploader(
                    uploading.worker(),
                    buffer,
                    logger,
                    uploading.client(),
                    uploading.meta(),
                    uploading.parts()
            ));
        }
    }

    public static Uploading manager(CountDownLatch master, Logger logger, OSSClient oss, String bucket, String file) {
        OSS client = oss.create();

        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucket, file);
        InitiateMultipartUploadResult result = client.initiateMultipartUpload(request);

        Uploading uploading = new Uploading(client, new CountDownLatch(THREADS_SIZE), result);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                uploading.worker().await();

                ListPartsRequest lpr = new ListPartsRequest(bucket, file, uploading.meta().getUploadId());
                PartListing listing = client.listParts(lpr);

                List<PartETag> parts = uploading.parts().sorted();

                for (PartSummary pg : listing.getParts()) {
                    parts.stream()
                        .filter(pe -> pe.getPartNumber() == pg.getPartNumber())
                        .findAny().get()
                        .setETag(pg.getETag())
                    ;
                }

                CompleteMultipartUploadRequest complete = new CompleteMultipartUploadRequest(bucket, file, uploading.meta().getUploadId(), parts);
                CompleteMultipartUploadResult completed = client.completeMultipartUpload(complete);

                logger.info(String.format("Uploading completed -> %s", completed.getLocation()));

                uploading.completed(completed);
            } catch (InterruptedException e) {
                AbortMultipartUploadRequest abort = new AbortMultipartUploadRequest(bucket, file, uploading.meta().getUploadId());
                client.abortMultipartUpload(abort);
                logger.error(String.format("Uploader workers wait fail %s", e.getMessage()));
            } finally {
                master.countDown();
                client.shutdown();
            }
        });

        return uploading;
    }

    private Uploader(CountDownLatch worker, Buffer buffer, Logger logger, OSS client, InitiateMultipartUploadResult upload, FileParts parts) {
        this.worker = worker;
        this.buffer = buffer;
        this.logger = logger;
        this.client = client;
        this.upload = upload;
        this.parts = parts;
    }

    @Override
    public void run() {
        while (buffer.available()) {
            byte[] chunk;

            try {
                chunk = buffer.spout();
            } catch (InterruptedException e) {
                logger.info(String.format("Uploader buffer spout interrupted %s", e.getMessage()));
                Thread.currentThread().interrupt();
                System.exit(1);
                return;
            }

            if (chunk.length == 0) {
                continue;
            }

            UploadPartRequest request = new UploadPartRequest(
                    upload.getBucketName(),
                    upload.getKey(),
                    upload.getUploadId(),
                    parts.number(),
                    new ByteArrayInputStream(chunk),
                    chunk.length
            );

            UploadPartResult result = client.uploadPart(request);

            parts.append(result.getPartETag());

            logger.debug(String.format("Uploading chunk (%s) fin -> #%d [%d]", upload.getKey(), result.getPartNumber(), result.getPartSize()));
        }

        logger.debug(String.format("Uploader thread done :%s", Thread.currentThread().getName()));

        worker.countDown();
    }
}
