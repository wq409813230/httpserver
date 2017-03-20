package net.freeapis.io;

/**
 * Created by wuqiang on 2017/3/19.
 */
public enum HttpStatus {
    OK(200),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    MEDIA_NOT_SUPPORTED(405),
    INTERNAL_SERVER_ERROR(500),
    SERVICE_NOT_AVAILABLE(502);

    private int value;

    private HttpStatus(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }
}
