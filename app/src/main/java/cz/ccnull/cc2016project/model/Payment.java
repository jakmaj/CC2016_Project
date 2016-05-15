package cz.ccnull.cc2016project.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Payment implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.amount);
        dest.writeByte(this.paymentDone ? (byte) 1 : (byte) 0);
        dest.writeString(this.senderName);
        dest.writeString(this.senderId);
        dest.writeString(this.code);
    }

    public Payment() {
    }

    protected Payment(Parcel in) {
        this.amount = in.readInt();
        this.paymentDone = in.readByte() != 0;
        this.senderName = in.readString();
        this.senderId = in.readString();
        this.code = in.readString();
    }

    public static final Parcelable.Creator<Payment> CREATOR = new Parcelable.Creator<Payment>() {
        @Override
        public Payment createFromParcel(Parcel source) {
            return new Payment(source);
        }

        @Override
        public Payment[] newArray(int size) {
            return new Payment[size];
        }
    };
}
