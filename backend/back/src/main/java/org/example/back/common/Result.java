package org.example.back.common;
import java.lang.*;
import lombok.Data;

@Data
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    public static Result success(Object data) {
        Result r = new Result();
        r.setCode(0);
        r.setMessage("success");
        r.setData(data);
        return r;
    }

    public static Result error(String msg) {
        Result r = new Result();
        r.setCode(1);
        r.setMessage(msg);
        return r;
    }
}