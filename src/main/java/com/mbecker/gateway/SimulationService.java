package com.mbecker.gateway;

import com.google.gson.Gson;
import org.jboss.logging.Logger;

public class SimulationService implements GatewayService {

    private static final Logger LOG = Logger.getLogger(SimulationService.class);
    private Gson gson = new Gson();

    public SimulationService() {
        LOG.debug("Initializing SimulationService");
    }

    @Override
    public void send(Notification notification, String routingKey) {
        LOG.warn(String.format("***** SIMULATION MODE ***** Would send SMS to %s with text: %s",
                notification.getReceiver(), notification.getMessage()));

    }

    @Override
    public void send(String notification, String routingKey) {
        LOG.warn(String.format("***** SIMULATION MODE ***** %s", notification));
    }

    @Override
    public void send(Object obj, String routingKey) {
        String json = this.gson.toJson(obj);
        this.send(json, routingKey);
    }

}
