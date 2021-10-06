package com.mbecker.gateway;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Notification {

    public Type getType() {
        return type;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public int getTtl() {
        return ttl;
    }

    public String getSendAt() {
        return sendAt;
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public Integer getCreatedAt() {
        return createdAt;
    }

    public String getUuid() {
        return uuid;
    }

    public String getRealm() {
        return realm;
    }

    public Action getAction() {
        return action;
    }

    public enum Type {
        SMS, EMAIl, PUSHNOTIFICATION
    }

    public enum Action {
        REQUIREDACTION, AUTHENTICATION
    }

    public Notification(Action action, Type type, String receiver, String message, String code, int ttl, Integer createdAt,
            String uuid, String realm) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        this.action = action;
        this.type = type;
        this.receiver = receiver;
        this.message = message;
        this.code = code;
        this.ttl = ttl;
        this.sendAt = sdf.format(timestamp);
        this.createdAt = createdAt;
        this.uuid = uuid;
        this.realm = realm;
    }

    private final Type type;
    private final Action action;
    private final String receiver;
    private final String message;
    private final String code;
    private final int ttl;
    private final String sendAt;
    private final Integer createdAt; // Timestamp in seconda at which the code was generated
    private final String uuid;
    private final String realm;

}
