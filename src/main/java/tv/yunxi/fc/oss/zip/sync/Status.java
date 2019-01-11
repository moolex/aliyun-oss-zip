package tv.yunxi.fc.oss.zip.sync;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author moyo
 */
public class Status {
    private int scheduled;

    private AtomicInteger downloaded = new AtomicInteger();
    private AtomicInteger packed = new AtomicInteger();
    private AtomicLong uploaded = new AtomicLong();

    private BlockingQueue<Status> changed = new ArrayBlockingQueue<>(1);

    public Status(int scheduled) {
        this.scheduled = scheduled;
        trigger();
    }

    private void trigger() {
        try {
            if (changed.size() < 1) {
                changed.put(this);
            }
        } catch (InterruptedException e) {
            // ignore
        }
    }

    public Status changed() throws InterruptedException {
        return changed.take();
    }

    public int getScheduled() {
        return scheduled;
    }

    public void setDownloaded() {
        downloaded.incrementAndGet();
        trigger();
    }

    public int getDownloaded() {
        return downloaded.get();
    }

    public void setPacked() {
        packed.incrementAndGet();
        trigger();
    }

    public int getPacked() {
        return packed.get();
    }

    public void setUploaded(long length) {
        uploaded.addAndGet(length);
        trigger();
    }

    public long getUploaded() {
        return uploaded.get();
    }
}
