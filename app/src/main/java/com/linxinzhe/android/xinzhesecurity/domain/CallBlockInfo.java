package com.linxinzhe.android.xinzhesecurity.domain;

public class CallBlockInfo {
    private String phone;
    private String mode;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "BlackNumberInfo [number=" + phone + ", mode=" + mode + "]";
    }
}
