package com.mbecker;

import com.mbecker.gateway.AMQPService;
import com.mbecker.gateway.GatewayService;

import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;

public class AMQPEventListenerProvider implements EventListenerProvider {

    private static final Logger LOG = Logger.getLogger(AMQPEventListenerProvider.class);

    private Utils utils;
    private GatewayService gateway;


    public AMQPEventListenerProvider(Utils utils) {
        LOG.debug("Initializing");
        this.utils = utils;
        this.gateway = new AMQPService(utils);
    }

    @Override
    public void close() {
        // TODO: This closes the AMQP connection every time; then the next event can't be published
        // if(this.channel != null) {
        //     try {
        //         this.channel.close();
        //     } catch (IOException | TimeoutException e) {
        //         e.printStackTrace();
        //     }
        // }
        
        // if(this.connection != null) {
        //     try {
        //         this.connection.close();
        //     } catch (IOException e) {
        //         e.printStackTrace();
        //     }
        // }
    }

    @Override
    public void onEvent(Event event) {
            this.gateway.send(event, this.utils.getAMQPEvents());
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        this.gateway.send(event, this.utils.getAMQPEventsAdmin());
    }
    
}
