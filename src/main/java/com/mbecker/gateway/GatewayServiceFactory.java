package com.mbecker.gateway;

import com.mbecker.Utils;

public class GatewayServiceFactory {

    public static GatewayService get(Utils utils) {
        if (utils.getIsSimulation()) {
            return new SimulationService();
        } else {
            return new AMQPService(utils);
        }
    }

}