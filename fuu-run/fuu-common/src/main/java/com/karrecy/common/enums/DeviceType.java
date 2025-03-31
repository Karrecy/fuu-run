package com.karrecy.common.enums;

import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 设备类型
 */
@Getter
@RequiredArgsConstructor
public enum DeviceType {
    /**
     * pc端
     */
    PC(0,"pc"),

    /**
     * 小程序端
     */
    WX(1,"wx");

    private final Integer code;

    private final String device;

    public static DeviceType getByCode(Integer code) {
        for (DeviceType deviceType : DeviceType.values()) {
            if (deviceType.getCode().equals(code)) {
                return deviceType;
            }
        }
        throw new RuntimeException("根据code匹配设备类型失败-"+code);
    }
    public static DeviceType getByDevice(String device) {
        for (DeviceType deviceType : DeviceType.values()) {
            if (deviceType.getDevice().equals(device)) {
                return deviceType;
            }
        }
        throw new RuntimeException("根据string匹配设备类型失败-"+device);
    }
}
