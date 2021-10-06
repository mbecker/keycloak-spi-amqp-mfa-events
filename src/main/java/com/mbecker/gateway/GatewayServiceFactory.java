package com.mbecker.gateway;

import org.jboss.logging.Logger;

import com.mbecker.Utils;

public class GatewayServiceFactory {

    private static final Logger LOG = Logger.getLogger(GatewayServiceFactory.class);

    public static GatewayService get(Utils utils) {
        if (utils.getIsSimulation()) {
            return (notification) -> LOG
                    .warn(String.format("***** SIMULATION MODE ***** Would send SMS to %s with text: %s",
                            notification.getReceiver(), notification.getMessage()));
        } else {
            return new AMQPService(utils);
        }
    }

}