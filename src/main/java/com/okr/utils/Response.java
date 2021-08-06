package com.okr.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Response {

    private String type;
    private String code;
    private String message;
    private Object data;

    public Response(String type) {
        this.type = type;
    }

    public Response(String code, String message) {
        this.message = message;
        this.code = code;
    }

    public Response withData(Object data) {
        this.data = data;
        return this;
    }

    public static Response success(String code) {
        return new Response(Constants.RESPONSE_TYPE.SUCCESS, code);
    }

    public static Response success(String code, String message) {
        return new Response(Constants.RESPONSE_TYPE.SUCCESS, code, message, null);
    }

    public static Response error(String data) {
        return new Response(Constants.RESPONSE_TYPE.ERROR, data);
    }

    public static Response warning(String code) {
        return new Response(Constants.RESPONSE_TYPE.WARNING, code);
    }

    public static Response invalidPermission() {
        return new Response(Constants.RESPONSE_TYPE.ERROR, "invalidPermission");
    }

    public static Response confirm(String code, String callback, Object data) {
        return new Response(Constants.RESPONSE_TYPE.CONFIRM, code, callback, data);
    }

    public static Response confirm(String code, String callback) {
        return new Response(Constants.RESPONSE_TYPE.CONFIRM, code, callback, null);
    }

    public static Response custom(String code, String message) {
        return new Response(Constants.RESPONSE_TYPE.ERROR, code, message, null);
    }

}
