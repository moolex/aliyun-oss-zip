package tv.yunxi.fc.oss.zip.utils;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import tv.yunxi.fc.oss.zip.errors.Exception;
import tv.yunxi.fc.oss.zip.sync.Status;
import tv.yunxi.fc.oss.zip.types.Progress;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * @author moyo
 */
public class Notify implements Runnable {
    private final static String MQTT = "mqtt";

    private Logger logger;
    private Status status;
    private IMqttClient mqtt;
    private MqttTopic topic;
    private volatile boolean running = true;

    public static Notify watch(Logger logger, Status status, String callback) throws Exception {
        Notify notify = new Notify(logger, status, callback);
        Executors.newSingleThreadExecutor().execute(notify);
        return notify;
    }

    private Notify(Logger logger, Status status, String callback) throws Exception {
        this.logger = logger;
        this.status = status;

        if (callback == null) {
            return;
        }

        URI url;
        try {
            url = new URI(callback);
        } catch (URISyntaxException e) {
            throw new Exception(e.toString());
        }

        final Map<String, String> params = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(url.getQuery());

        if (MQTT.equals(url.getScheme())) {
            try {
                String port = "";
                if (url.getPort() > 0) {
                    port = String.format(":%d", url.getPort());
                }
                String wsa = String.format("ws://%s%s/mqtt", url.getHost(), port);
                this.mqtt = new MqttClient(
                        wsa,
                        params.getOrDefault("client", "tv.yunxi.fc.oss.zip.notify"),
                        new MemoryPersistence()
                );
                MqttConnectOptions options = new MqttConnectOptions();
                options.setConnectionTimeout(3);
                options.setCleanSession(true);
                this.mqtt.connect(options);
                this.topic = mqtt.getTopic(params.get("topic"));
            } catch (MqttException e) {
                throw new Exception(String.format("MQTT client initialize failed %s", e.toString()));
            }
            return;
        }

        throw new Exception(String.format("Unsupported notify protocol %s", url.getScheme()));
    }

    public void stop(String location) {
        running = false;

        callback(status, location);

        if (mqtt == null) {
            return;
        }

        try {
            this.mqtt.close();
        } catch (MqttException e) {
            // ignore
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                callback(status.changed(), null);
            } catch (InterruptedException e) {
                logger.warn(String.format("Notify thread exception %s", e.toString()));
            }
        }
    }

    private void callback(Status c, String loc) {
        if (topic == null) {
            return;
        }

        Progress progress = new Progress(c.getScheduled(), c.getDownloaded(), c.getPacked(), c.getUploaded(), false, "");
        if (loc != null) {
            progress.setFinished(true);
            progress.setLocation(loc);
        }

        try {
            topic.publish(
                new Gson().toJson(progress).getBytes(),
                1,
                progress.getFinished()
            ).waitForCompletion();
        } catch (MqttException e) {
            logger.warn(String.format("Notify message send fail %s", e.toString()));
        }
    }
}
