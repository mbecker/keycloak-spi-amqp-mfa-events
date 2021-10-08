package com.mbecker.gateway;

import java.util.Map;

import org.keycloak.email.EmailException;
import org.keycloak.models.UserModel;

public interface GatewayService {
	void send(Notification notification, String routingKey) throws Exception;
	void send(String notification, String routingKey) throws Exception;
	void send(Object obj, String routingKey) throws Exception;
	void sendMail(Map<String, String> config, UserModel user, EmailTemplate emailTemplate) throws EmailException;
}

