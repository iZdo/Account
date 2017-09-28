package com.izdo.Bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by iZdo on 2017/4/22.
 */

public class DataBean implements Parcelable {

    private int id;
    private String money;
    private String type;
    private String describe;
    private String account;
    private String fixed_charge;
    private String date;
    private String behavior;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getFixed_charge() {
        return fixed_charge;
    }

    public void setFixed_charge(String fixed_charge) {
        this.fixed_charge = fixed_charge;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(money);
        parcel.writeString(type);
        parcel.writeString(describe);
        parcel.writeString(account);
        parcel.writeString(fixed_charge);
        parcel.writeString(date);
        parcel.writeString(behavior);
    }

    public static final Parcelable.Creator<DataBean> CREATOR = new Parcelable.Creator<DataBean>() {

        @Override
        public DataBean createFromParcel(Parcel parcel) {
            DataBean dataBean = new DataBean();
            dataBean.id = parcel.readInt();
            dataBean.money = parcel.readString();
            dataBean.type = parcel.readString();
            dataBean.describe = parcel.readString();
            dataBean.account = parcel.readString();
            dataBean.fixed_charge = parcel.readString();
            dataBean.date = parcel.readString();
            dataBean.behavior = parcel.readString();
            return dataBean;
        }

        @Override
        public DataBean[] newArray(int i) {
            return new DataBean[0];
        }
    };
}
