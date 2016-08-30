package com.i3cnam.gofast.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nestor on 18/07/2016.
 */
@Entity
public class User implements Serializable{
	@Id
    private String nickname; // it is the id
    private String phoneNumber;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public static User getMe(){
        User me = new User();
        me.nickname = "titi42";
        me.phoneNumber = "+33629386194";
        return me;
    }

    public JSONObject getJsonObject() {
    	JSONObject json = new JSONObject();
    	try {
			json.put("nickname", this.nickname);
			json.put("phone_number", this.phoneNumber);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return json;

    }
    

    private void readObject(final ObjectInputStream ois) throws IOException,
            ClassNotFoundException {
        this.nickname = (String) ois.readObject();
        this.phoneNumber = (String) ois.readObject();
    }

    private void writeObject(final ObjectOutputStream oos) throws IOException {
        oos.writeObject(this.nickname);
        oos.writeObject(this.phoneNumber);
    }

	@Override
	public String toString() {
		return "User [nickname=" + nickname + ", phoneNumber=" + phoneNumber
				+ "]";
	}
    
    
}
