package com.mbecker.mfaresource.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ErrorMessage {
    private String message;
    private Response.Status status;
    private int code;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Response.Status getStatus() {
        return status;
    }
    
    public int getCode() {
        return code;
    }
    
    public void setStatus(Response.Status status) {
        this.status = status;
        this.code = status.getStatusCode();
    }
    public ErrorMessage(String message, Status status) {
        this.message = message;
        this.status = status;
        this.code = status.getStatusCode();
    }

    public Response response() {
        return Response.status(this.status).entity(this).build();
    }
    
}
