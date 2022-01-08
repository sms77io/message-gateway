package org.fineract.messagegateway.sms.providers.impl.sms77;

public class Sms77ResponseData {
    private String msg_id;
    private String status;
    private String timestamp;
    private String webhook_event;
    private String webhook_timestamp;

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setWebhook_event(String webhook_event) {
        this.webhook_event = webhook_event;
    }

    public void setWebhook_timestamp(String webhook_timestamp) {
        this.webhook_timestamp = webhook_timestamp;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public String getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getWebhook_event() {
        return webhook_event;
    }

    public String getWebhook_timestamp() {
        return webhook_timestamp;
    }
}
