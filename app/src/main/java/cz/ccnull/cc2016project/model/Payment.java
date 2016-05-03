package cz.ccnull.cc2016project.model;

import com.google.gson.annotations.SerializedName;

public class Payment {

    int amount;
    boolean paymentDone;

    @SerializedName("nameSender")
    String senderName;

    @SerializedName("_idSender")
    String senderId;

    @SerializedName("paymentCode")
    String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isPaymentDone() {
        return paymentDone;
    }

    public void setPaymentDone(boolean paymentDone) {
        this.paymentDone = paymentDone;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
