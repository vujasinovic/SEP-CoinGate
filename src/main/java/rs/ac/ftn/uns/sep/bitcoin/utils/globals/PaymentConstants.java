package rs.ac.ftn.uns.sep.bitcoin.utils.globals;

public abstract class PaymentConstants {
    private static final String HOST = "http://localhost:8080/";

    public static class Api {
        public static final String API_ORDERS = "https://api-sandbox.coingate.com/v2/orders";
    }

    public static class Header {
        public static final String AUTHORIZATION = "Authorization";
        public static final String TOKEN = "Token ";
    }

    public static class BodyParam {
        public static final String PRICE_AMOUNT = "price_amount";
        public static final String PRICE_CURRENCY = "price_currency";
        public static final String RECEIVE_CURRENCY = "receive_currency";
        public static final String TITLE = "title";
        public static final String SUCCESS_URL = "success_url";
        public static final String CANCEL_URL = "cancel_url";
    }

    public static class Url {
        public static final String SUCCESS_URL = HOST + "paymentSuccessful/";
        public static final String CANCEL_URL = HOST + "paymentCanceled/";
    }

    public static class Info {
        public static final String TITLE = "Test order";
        public static final String CURRENCY = "BTC";
    }
}
