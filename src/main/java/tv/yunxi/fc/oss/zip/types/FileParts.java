package tv.yunxi.fc.oss.zip.types;

import com.aliyun.oss.model.PartETag;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author moyo
 */
public class FileParts {
    private List<PartETag> parts = new ArrayList<>();
    private AtomicInteger number = new AtomicInteger(1);

    public synchronized void append(PartETag part) {
        parts.add(part);
    }

    public List<PartETag> sorted() {
        parts.sort((p1, p2) -> p1.getPartNumber() - p2.getPartNumber());
        return parts;
    }

    public int number() {
        return number.getAndIncrement();
    }
}
