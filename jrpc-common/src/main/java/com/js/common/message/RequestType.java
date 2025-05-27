package com.js.common.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RequestType {
    NORMAL(0), HEARTBEAT(1);
    @Getter
    private final int code;
}
