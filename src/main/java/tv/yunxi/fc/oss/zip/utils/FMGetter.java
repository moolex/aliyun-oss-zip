package tv.yunxi.fc.oss.zip.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.SimplifiedObjectMeta;
import com.google.common.base.Splitter;
import tv.yunxi.fc.oss.zip.requests.ConfirmedFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author moyo
 * file mete getter
 */
public class FMGetter implements Runnable {
    private CountDownLatch latch;
    private Logger logger;
    private OSS client;
    private String bucket;
    private String group;
    private String file;
    private List<ConfirmedFile> target;

    FMGetter(CountDownLatch latch, Logger logger, OSS client, String bucket, String group, String file, List<ConfirmedFile> target) {
        this.latch = latch;
        this.logger = logger;
        this.client = client;
        this.bucket = bucket;
        this.group = group;
        this.file = file;
        this.target = target;
    }

    @Override
    public void run() {
        try {
            String alias = null;
            if (file.contains("?")) {
                int pos = file.indexOf("?");
                final Map<String, String> params = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(file.substring(pos + 1));
                if (params.containsKey("a")) {
                    alias = params.get("a");
                }
                file = file.substring(0, pos);
            }

            SimplifiedObjectMeta meta = client.getSimplifiedObjectMeta(bucket, file);
            ConfirmedFile s = new ConfirmedFile();
            s.setBucket(bucket);
            s.setGroup(group);
            s.setKey(file);
            s.setAlias(alias);
            s.setETag(meta.getETag());
            s.setSize(meta.getSize());
            target.add(s);
        } catch (Exception e) {
            logger.warn(String.format("File meta get failed [%s] -> %s", file, e.toString()));
        } finally {
            latch.countDown();
        }
    }
}
