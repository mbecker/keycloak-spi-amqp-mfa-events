package com.mbecker;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;

import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class AMQPEventListenerProvider implements EventListenerProvider {

    private static final Logger LOG = Logger.getLogger(AMQPEventListenerProvider.class);

    private Utils utils;
    private Channel channel;
    
    Gson gson = new Gson();

    public AMQPEventListenerProvider(Utils utils) {

        this.utils = utils;

        ConnectionFactory factory = new ConnectionFactory();
        
        factory.setHost(utils.getAMQPHost());
        factory.setPort(utils.getAMQPPort());
        factory.setUsername(utils.getAMQPUsername());
        factory.setPassword(utils.getAMQPPassword());
        factory.setVirtualHost(utils.getAMQPVhost());
        
        Connection connection;
        try {
            connection = factory.newConnection();
            this.channel = connection.createChannel();
            channel.queueDeclare(utils.getAMQPQueue(), false, false, false, null);
            LOG.debug("AMQP Channel declard");
        } catch (IOException | TimeoutException e) {
            LOG.error("Error AMQP connection / channel declaration", e);
        }
        
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

    private void publish(String json, String queueName) {
        try {
            this.channel.basicPublish("", queueName, null, json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    @Override
    public void onEvent(Event event) {
            String json = this.gson.toJson(event);
            this.publish(json, this.utils.getAMQPEvents());
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        String json = this.gson.toJson(event);
        this.publish(json, this.utils.getAMQPEventsAdmin());
    }
    
}
