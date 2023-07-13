package com.by.vo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class WebResult<T> {
    private Integer code; //编码：1成功，0和其它数字为失败
    private String msg; //错误信息
    private Map map = new HashMap(); //动态数据
    public static <T> WebResult<T> success(T object) {
        WebResult<T> r = new WebResult<T>();
        r.msg = (String) object;
        r.code = 1;
        return r;
    }
    public static <T> WebResult<T> error(String msg) {
        WebResult r = new WebResult();
        r.msg = msg;
        r.code = 0;
        return r;
    }
    public WebResult<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
    public void removeMap() {
        this.map.clear();
    }
}