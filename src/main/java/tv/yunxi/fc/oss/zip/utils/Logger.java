package tv.yunxi.fc.oss.zip.utils;

import com.aliyun.fc.runtime.FunctionComputeLogger;

/**
 * @author moyo
 */
public class Logger {
    private FunctionComputeLogger output;

    public Logger(FunctionComputeLogger to) {
        this.output = to;
    }

    public void debug(String msg) {
        this.output.debug(msg);
    }

    public void info(String msg) {
        this.output.info(msg);
    }

    public void warn(String msg) {
        this.output.warn(msg);
    }

    public void error(String msg) {
        this.output.error(msg);
    }
}
