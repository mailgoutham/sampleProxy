package com.company.sampleproxy;

import org.springframework.stereotype.Component;

public class ResponseApi {

    private int code;
    private String message;

    public ResponseApi(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResponseApi{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
