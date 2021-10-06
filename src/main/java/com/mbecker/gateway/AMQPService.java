package com.mbecker.gateway;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mbecker.Utils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import org.jboss.logging.Logger;

public class AMQPService implements GatewayService {

    private static final Logger LOG = Logger.getLogger(AMQPService.class);

    private Channel channel;
    private Gson gson = new Gson();

    public AMQPService(Utils utils) {

        LOG.debug("Initializing AMQPService");

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
    public void send(Notification notification, String routingKey) {
        System.out.println("Send ...");
        try {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'hh:mm:ss.S").create();
            String json = gson.toJson(notification);
            this.channel.basicPublish("", routingKey, null, json.getBytes(StandardCharsets.UTF_8));
            System.out.println("[x] Sent '" + json + "'");
        } catch (IOException e) {
            LOG.error("Publish message", e);
        }
    }

    @Override
    public void send(String notification, String routingKey) {
        try {
            this.channel.basicPublish("", routingKey, null, notification.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    public void send(Object obj, String routingKey) {
        String json = this.gson.toJson(obj);
        this.send(json, routingKey);
    }
    

    
}