package com.google.android.gms.maps.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class LatLng implements Serializable {
	public double latitude, longitude;

	public LatLng(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}

    public JSONObject getJsonObject() {
    	JSONObject json = new JSONObject();
    	try {
			json.put("lat", this.latitude);
			json.put("long", this.longitude);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return json;
    }
	
	
	public String toJsonString() {
		System.out.println("toto");
		System.out.println(toString());
		System.out.println("{" +
				"\"lat\":" + this.latitude + "," +
				"\"long\":" + this.longitude +
				"}");
		return "{" +
				"\"lat\":" + latitude + "," +
				"\"long\":" + longitude +
				"}";
				
	}
	
	@Override
	public String toString() {
		return "LatLng [latitude=" + latitude + ", longitude=" + longitude
				+ "]";
	}
	
}
