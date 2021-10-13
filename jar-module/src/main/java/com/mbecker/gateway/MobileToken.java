package com.mbecker.gateway;

import com.mbecker.gateway.Const.DeviceType;

public class MobileToken {

    public String token;
    public DeviceType deviceType;
    public int createdAt;

    
    public MobileToken(String token, DeviceType deviceType, int createdAt) {
        this.token = token;
        this.deviceType = deviceType;
        this.createdAt = createdAt;
    }
    
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public DeviceType getDeviceType() {
        return deviceType;
    }
    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }
    
    public int getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }
    

    @Override
    public String toString() {
        return "MobileToken [deviceType=" + deviceType + ", token=" + token + "]";
    }
    
}
