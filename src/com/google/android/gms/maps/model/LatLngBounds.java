package com.google.android.gms.maps.model;

import java.io.Serializable;

public class LatLngBounds implements Serializable {
	LatLng northwest, southeast;

	public LatLngBounds(LatLng northwest, LatLng southeast) {
		super();
		this.northwest = northwest;
		this.southeast = southeast;
	}

	@Override
	public String toString() {
		return "LatLngBounds [northwest=" + northwest + ", southeast="
				+ southeast + "]";
	}
	
}
