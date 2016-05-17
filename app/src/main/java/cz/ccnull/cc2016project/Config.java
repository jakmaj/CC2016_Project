package cz.ccnull.cc2016project;

public class Config {
    public static final String API_BASE_URL = "http://zkustesito.cz/api/v1/cc/";

    public static final String URL_LOGIN = "login";
    public static final String URL_CREATE_PAYMENT = "payment_create";
    public static final String URL_PAYMENT_HEARD = "payment_heard";
    public static final String URL_PAYMENT_CONFIRM = "payment_confirm";

    public static final String SP_GCM_TOKEN_KEY = "gcm_token";
    public static final String SP_LOGIN = "login";

    public static final String KEY_PAYMENT = "payment";
    public static final String KEY_PAYMENT_CODE = "payment_code";
    public static final String KEY_ROLE = "role";
    public static final String KEY_CONFIRM = "payment_confirm";
    public static final String ROLE_SENDER = "sender";
    public static final String ROLE_RECEIVER = "receiver";

    public static final String BROADCAST_CONFIRM = "cz.ccnull.cc2016project.payment_confirm";
}
