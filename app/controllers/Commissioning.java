package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller for commissioning.
 */
public class Commissioning extends Controller {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public Result index() {
        return ok();
    }

    /**
     * Check the gateway for configuration items.
     *
     * @param gatewayId The gateway id to check
     * @return A WebSocket configured for the response actions.
     */
    public WebSocket<JsonNode> checkConfig(final String gatewayId) {

        return new WebSocket<JsonNode>() {
            @Override
            public void onReady(In<JsonNode> in, final Out<JsonNode> out) {

                // if we query IROM in a separate thread (such as via Hystrix or Akka) and write to out in that other
                // thread, the web socket works as expected.
                // Using the same thread, the websocket sends all the messages at once, meaning it acts just like a standard Result.
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {

                        writeEvent(out, "Checking Firmware");

                        sleepFor5();

                        writeNode(out, Json.toJson(new ConfigResult("Firmware upgrade",
                                "Gateway has version 3.1.0.1, current is 3.2.1.1.  A firmware upgrade will be scheduled.", "01c")));

                        writeEvent(out, "Checking Gateway Clusters");

                        sleepFor5();

                        writeNode(out, Json.toJson(new ConfigResult("On/Off Clusters", "Need to create Gateway On/Off Clusters", "111")));

                        sleepFor5();

                        writeNode(out, Json.toJson(new ConfigResult("Tstat Cluster", "Need to create Tstat Cluster", "011")));
                        writeEvent(out, "Done Checking System");

                        out.close();
                    }

                });

            }


        };
    }

    /**
     * Performs commissioning on the given gateway.
     *
     * @param gatewayId The gateway id to perform commissioning on.
     * @return A WebSocket object where results will be written.
     */
    public WebSocket<JsonNode> performCommission(final String gatewayId) {
        return new WebSocket<JsonNode>() {
            @Override
            public void onReady(final In<JsonNode> in, final Out<JsonNode> out) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        writeEvent(out, "Scheduling Firmware");

                        sleepFor5();

                        writeNode(out, Json.toJson(new ConfigResult("Firmware upgrade",
                                "A firmware upgrade has been scheduled for ", "01c")));

                        writeEvent(out, "Adding On/Off Clusters");

                        sleepFor5();

                        writeNode(out, Json.toJson(new ConfigResult("On/Off Clusters", "On/Off Cluster added successfully", "111")));
                        writeEvent(out, "Adding Tstat Cluster");
                        sleepFor5();

                        writeNode(out, Json.toJson(new ConfigResult("Tstat Cluster", "Tstat Cluster added successfully", "011")));
                        writeEvent(out, "Done Checking System");

                        out.close();
                    }
                });
            }
        };
    }

    public WebSocket<JsonNode> findDiscoveredDevices(final String gatewayId) {
        return new WebSocket<JsonNode>() {
            @Override
            public void onReady(final In<JsonNode> in, final Out<JsonNode> out) {

                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        sleepFor5();
                        Collection<CommissioningDevice> devices = new ArrayList<>();
                        devices.add(new CommissioningDevice("00:07:A6:00:C5:8A", "00:C5:8A:00:91:AA:D2:90:BC:68", "available"));
                        devices.add(new CommissioningDevice("00:1B:C5:00:B0:00:BD:E4", "1C:60:6C:F0:69:64:F3:30", "available"));

                        out.write(Json.toJson(new DiscoveredDevices(devices)));
                    }
                });

            }
        };
    }

    private void writeEvent(WebSocket.Out<JsonNode> out, String description) {
        writeNode(out, Json.toJson(new EventNotification(description)));
    }

    private void writeNode(WebSocket.Out<JsonNode> out, JsonNode jsonNode) {
        out.write(jsonNode);
    }

    private void sleepFor5() {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class EventNotification {
        private final String eventType = "Notification";
        private final String description;

        EventNotification(String description) {
            this.description = description;
        }

        public String getEventType() {
            return eventType;
        }

        public String getDescription() {
            return description;
        }
    }

    class ConfigResult {

        private final String title;
        private final String description;
        private final String opCode;
        private final String eventType = "ConfigResult";

        public ConfigResult(String title, String description, String opCode) {
            this.title = title;
            this.description = description;
            this.opCode = opCode;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getOpCode() {
            return opCode;
        }

        public String getEventType() {
            return eventType;
        }
    }

    class DiscoveredDevices {
        private final String eventType = "DiscoveredDevices";
        private final Collection<CommissioningDevice> devices;

        DiscoveredDevices(Collection<CommissioningDevice> devices) {
            this.devices = devices;
        }

        public String getEventType() {
            return eventType;
        }

        public Collection<CommissioningDevice> getDevices() {
            return devices;
        }
    }

    class CommissioningDevice {
        private final String mac;
        private final String ic;
        private final String status;

        CommissioningDevice(String mac, String ic, String status) {
            this.mac = mac;
            this.ic = ic;
            this.status = status;
        }

        public String getMac() {
            return mac;
        }

        public String getIc() {
            return ic;
        }

        public String getStatus() {
            return status;
        }
    }
}
