package net.scriptingninja.smsb;

/**
 * Created by michaelhabashy on 2/2/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Resms {
    @SerializedName("indentity")
    @Expose
    private Double indentity;
    @SerializedName("credit")
    @Expose
    private Integer credit;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("active")
    @Expose
    private Boolean active;


    public Double getIndentity() {
        return indentity;
    }

    public void setIndentity(Double indentity) {
        this.indentity = indentity;
    }


    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }
    public boolean getActive() {
        if(active){
            return true;
        }else{
            return false;
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
