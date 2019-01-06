package tv.yunxi.fc.oss.zip.sync;

import tv.yunxi.fc.oss.zip.utils.Logger;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author moyo
 */
public class Buffer extends OutputStream {
    private static int CHUNK_SIZE = 1024*1024*8;

    private Logger logger;
    private ByteBuffer buffer = ByteBuffer.allocate(Buffer.CHUNK_SIZE);

    private Lock lock = new ReentrantLock();
    private Condition full = lock.newCondition();
    private Condition enough = lock.newCondition();

    private volatile boolean usable = true;

    public Buffer(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void write(int b) {
        if (!usable) {
            logger.warn("Buffer not writable [closed]");
            return;
        }

        lock.lock();

        try {
            if (buffer.position() == buffer.capacity()) {
                full.await();
            }

            if (buffer.put((byte) b).position() == buffer.capacity()) {
                enough.signal();
            }
        } catch (InterruptedException e) {
            logger.error(String.format("Buffer write interrupted %s", e.getMessage()));
        } finally {
            lock.unlock();
        }
    }

    public byte[] spout() throws InterruptedException {
        lock.lock();

        try {
            if (usable && buffer.position() < buffer.capacity()) {
                enough.await();
            }

            buffer.flip();

            ByteArrayOutputStream data = new ByteArrayOutputStream();
            while (buffer.hasRemaining()) {
                data.write(buffer.get());
            }

            buffer.compact();

            full.signal();

            return data.toByteArray();
        } finally {
            lock.unlock();
        }
    }

    public boolean available() {
        return usable || buffer.position() > 0;
    }

    @Override
    public void close() {
        usable = false;
        lock.lock();
        enough.signalAll();
        lock.unlock();
    }
}
