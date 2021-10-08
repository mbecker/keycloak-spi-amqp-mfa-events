package com.mbecker.gateway;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mbecker.Utils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.jboss.logging.Logger;
import org.keycloak.email.EmailException;
import org.keycloak.models.UserModel;

public class AMQPService implements GatewayService {

    private static final Logger LOG = Logger.getLogger(AMQPService.class);

    private Channel channel;
    private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public AMQPService(Utils utils) {

        LOG.info("Initializing AMQPService");

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
            LOG.info("AMQP Channel declard");
        } catch (IOException | TimeoutException e) {
            LOG.error("Error AMQP connection / channel declaration", e);
        }

    }

    @Override
    public void send(Notification notification, String routingKey) throws IOException {
        String json = this.gson.toJson(notification);
        this.channel.basicPublish("", routingKey, null, json.getBytes(StandardCharsets.UTF_8));
        LOG.info("Sent AMQP Message: '" + json + "'");
    }

    @Override
    public void send(String notification, String routingKey) throws IOException {
        this.channel.basicPublish("", routingKey, null, notification.getBytes(StandardCharsets.UTF_8));
    }

    public void send(Object obj, String routingKey) throws IOException {
        String json = this.gson.toJson(obj);
        this.send(json, routingKey);
    }

    @Override
    public void sendMail(Map<String, String> config, UserModel user, EmailTemplate emailTemplate)
            throws EmailException {
        LOG.error("Sending mail with AMQP Gateway Service");
    }

}