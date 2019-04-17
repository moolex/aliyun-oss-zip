package tv.yunxi.fc.oss.zip.utils;

import tv.yunxi.fc.oss.zip.requests.ConfirmedFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author moyo
 */
class Coordinator {
    private Logger logger;

    Coordinator(Logger logger) {
        this.logger = logger;
    }

    List<ConfirmedFile> duplicates(List<ConfirmedFile> files) {
        Map<String, AtomicInteger> repeated = new HashMap<>(files.size());

        return files.stream().peek(file -> {
            String name = file.getName();
            if (repeated.containsKey(name)) {
                String prefix = name.substring(0, name.lastIndexOf("."));
                String suffix = name.substring(name.lastIndexOf(".") + 1);
                String renamed = String.format(
                        "%s (%d).%s",
                        prefix,
                        repeated.get(name).incrementAndGet(),
                        suffix
                );
                logger.info(String.format("File has been renamed from %s -> %s", name, renamed));
                file.setAlias(renamed);
            } else {
                repeated.putIfAbsent(name, new AtomicInteger(0));
            }
        }).collect(Collectors.toList());
    }
}
