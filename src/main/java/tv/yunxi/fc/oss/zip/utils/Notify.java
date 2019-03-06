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
import java.util.UUID;
import java.util.concurrent.Executors;

/**
 * @author moyo
 */
public class Notify implements Runnable {
    private final static String MQTT = "mqtt";
    private final static Integer RETRY = 3;

    private Logger logger;
    private Status status;
    private String wsa;
    private IMqttClient mqtt;
    private String subscribe;
    private MqttTopic topic;
    private MqttConnectOptions options;
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

                wsa = String.format("ws://%s%s/mqtt", url.getHost(), port);

                mqtt = new MqttClient(
                        wsa,
                        params.getOrDefault("client", String.format("tv.yunxi.fc.oss.zip.notify:%s", UUID.randomUUID().toString())),
                        new MemoryPersistence()
                );

                options = new MqttConnectOptions();
                options.setCleanSession(true);
                options.setConnectionTimeout(3);
                options.setAutomaticReconnect(true);

                subscribe = params.get("topic");

                connect();
            } catch (MqttException e) {
                throw new Exception(String.format("MQTT client initialize failed -> %s", e.toString()));
            }
            return;
        }

        throw new Exception(String.format("Unsupported notify protocol [%s]", url.getScheme()));
    }

    private void connect() throws MqttException {
        logger.info(String.format("Start to connect MQTT/ws -> %s", wsa));

        mqtt.connect(options);
        topic = mqtt.getTopic(subscribe);

        logger.info(String.format("MQTT/ws connected and topic is '%s'", topic.toString()));
    }

    public void stop(String location) {
        running = false;

        callback(status, location);

        if (mqtt == null) {
            return;
        }

        try {
            mqtt.close();
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
                logger.warn(String.format("Notify thread exception -> %s", e.toString()));
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

        int retried = 0;

        while (retried < RETRY) {
            try {
                topic.publish(
                        new Gson().toJson(progress).getBytes(),
                        progress.getFinished() ? 1 : 0,
                        progress.getFinished()
                ).waitForCompletion();
                break;
            } catch (MqttException e) {
                logger.warn(String.format("[%d] Notify message send fail -> %s", retried, e.toString()));

                if (e.getReasonCode() == MqttException.REASON_CODE_CLIENT_NOT_CONNECTED) {
                    try {
                        connect();
                    } catch (MqttException e2) {
                        logger.warn(String.format("MQTT/ws reconnect failed while sending message -> %s", e2.toString()));
                    }
                }

                retried ++;
            }
        }
    }
}
