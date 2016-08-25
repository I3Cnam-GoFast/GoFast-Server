package com.google.android.gms.maps.model;

import java.io.Serializable;

public class LatLng implements Serializable {
	public double latitude, longitude;

	public LatLng(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return "LatLng [latitude=" + latitude + ", longitude=" + longitude
				+ "]";
	}
	
}
