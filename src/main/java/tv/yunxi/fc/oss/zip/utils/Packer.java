package tv.yunxi.fc.oss.zip.utils;

import tv.yunxi.fc.oss.zip.sync.Buffer;
import tv.yunxi.fc.oss.zip.types.FileObject;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author moyo
 */
public class Packer implements Runnable {
    private final static int QUEUE_SIZE = 2;

    private CountDownLatch master;
    private Logger logger;

    private BlockingQueue<FileObject> files = new LinkedBlockingQueue<>(QUEUE_SIZE);
    private ZipOutputStream zip;

    private volatile boolean running = true;

    public static Packer start(CountDownLatch master, Buffer buffer, Logger logger) {
        Packer packer = new Packer(master, buffer, logger);
        Executors.newSingleThreadExecutor().execute(packer);
        return packer;
    }

    private Packer(CountDownLatch master, Buffer buffer, Logger logger) {
        this.master = master;
        this.logger = logger;
        this.zip = new ZipOutputStream(buffer);
    }

    private FileObject get() throws InterruptedException {
        return files.take();
    }

    public void put(FileObject file) throws InterruptedException {
        files.put(file);
    }

    public void flush() {
        running = false;
    }

    @Override
    public void run() {
        while (running || files.size() > 0) {
            try {
                FileObject file = get();

                zip.putNextEntry(new ZipEntry(file.name()));
                zip.write(file.data());

                logger.debug(String.format("Packer write file %s size = %d", file.name(), file.data().length));
            } catch (InterruptedException | IOException e) {
                logger.info(String.format("Packer thread interrupted %s", e.toString()));
                Thread.currentThread().interrupt();
                System.exit(1);
                return;
            }
        }

        try {
            zip.close();
        } catch (IOException e) {
            logger.error(String.format("Packer zip stream close failed (%s)", e.getMessage()));
        }

        logger.debug(String.format("Packer thread done :%s", Thread.currentThread().getName()));

        master.countDown();
    }
}
