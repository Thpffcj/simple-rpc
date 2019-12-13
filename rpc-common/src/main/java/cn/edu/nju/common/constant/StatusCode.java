package cn.edu.nju.common.constant;

/**
 * Created by thpffcj on 2019/12/13.
 */
public enum StatusCode {

    SUCCESS(200, "OK"),
    NO_AVAILABLE_SERVICE_PROVIDER(100001, "no available service provider");

    private Integer code;
    private String description;

    StatusCode(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
}
