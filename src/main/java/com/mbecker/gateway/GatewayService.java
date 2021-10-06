package com.mbecker.gateway;

public interface GatewayService {
	void send(Notification notification, String routingKey);
	void send(String notification, String routingKey);
	void send(Object obj, String routingKey);
}

