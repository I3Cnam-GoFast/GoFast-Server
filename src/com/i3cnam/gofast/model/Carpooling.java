package com.i3cnam.gofast.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * IThis class represents a carpool shared between one driver and one passenger
 */
@Entity
public class Carpooling implements Serializable {
    // carpool states enumerated type
    public enum CarpoolingState{POTENTIAL, IN_DEMAND, IN_PROGRESS, REFUSED, CONFLICT, ACHIEVED};
    
    @Id  @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    private DriverCourse driverCourse; // the driverCourse of the carpool driver
    private PassengerTravel passengerTravel; // the travel of the carpool passenger

    private LatLng pickupPoint; // coordinates of the point the passenger is supposed to be picked up
    private LatLng dropoffPoint; // coordinates of the point the passenger is supposed to be dropped off
    private Date pickupTime; // time when the passenger is supposed to be picked up

    private float fare; // amount of money to be applied to the carpooling

    private CarpoolingState state = CarpoolingState.POTENTIAL; // state of carpool
    
    private boolean validatedByDriver = false; // complement of state
    private boolean validatedByPassenger = false; // complement of state
        

    /* --------------------------- GETTERS AND SETTERS --------------------------- */
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public DriverCourse getDriverCourse() {
        return driverCourse;
    }

    public void setDriverCourse(DriverCourse driverCourse) {
        this.driverCourse = driverCourse;
    }

    public PassengerTravel getPassengerTravel() {
        return passengerTravel;
    }

    public void setPassengerTravel(PassengerTravel passengerTravel) {
        this.passengerTravel = passengerTravel;
    }

    public LatLng getPickupPoint() {
        return pickupPoint;
    }

    public void setPickupPoint(LatLng pickupPoint) {
        this.pickupPoint = pickupPoint;
    }

    public LatLng getDropoffPoint() {
        return dropoffPoint;
    }

    public void setDropoffPoint(LatLng dropoffPoint) {
        this.dropoffPoint = dropoffPoint;
    }

    public Date getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(Date pickupTime) {
        this.pickupTime = pickupTime;
    }

    public float getFare() {
        return fare;
    }

    public void setFare(float fare) {
        this.fare = fare;
    }

    public CarpoolingState getState() {
        return state;
    }

    public void setState(CarpoolingState state) {
        this.state = state;
    }

    public boolean isValidatedByDriver() {
    	return validatedByDriver;
    }

    public void setValidatedByDriver(boolean validatedByDriver) {
    	this.validatedByDriver = validatedByDriver;
    }

    public boolean isValidatedByPassenger() {
    	return validatedByPassenger;
    }

    public void setValidatedByPassenger(boolean validatedByPassenger) {
    	this.validatedByPassenger = validatedByPassenger;
    }

    public User getDriver() {
        return getDriverCourse().getDriver();
    }

    public User getPassenger() {
        return getPassengerTravel().getPassenger();
    }

    public JSONObject getJsonObject() {
    	JSONObject json = new JSONObject();
        DateFormat format = new SimpleDateFormat("y/M/d H:m");

    	try {
			json.put("id", this.id);
			json.put("pickup_point", this.pickupPoint.getJsonObject());
			json.put("dropoff_point", this.dropoffPoint.getJsonObject());
			json.put("pickup_time", (this.pickupTime == null ? null : format.format(this.pickupTime)));
			json.put("fare", this.fare);
			json.put("passenger", this.passengerTravel.getPassenger().getNickname());
			json.put("driver", this.driverCourse.getDriver().getNickname());
			json.put("state", this.state.name());
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	return json;
    }
        
    
    @Override
    public boolean equals(Object obj) {
        boolean isEqual = false;
        if (obj instanceof Carpooling) {
            if (((Carpooling)obj).getId() == id &&
                    ((Carpooling)obj).getPickupPoint().equals(getPickupPoint()) &&
                    ((Carpooling)obj).getDropoffPoint().equals(getDropoffPoint()) &&
                    ((Carpooling)obj).getPickupTime().equals(getPickupTime()) &&
                    ((Carpooling)obj).getFare() == getFare() &&
                    ((Carpooling)obj).getState().equals(getState())) {
                // partial result
                isEqual = true;
                // nullable members :
                if (((Carpooling)obj).getPassengerTravel() == null) {
                    if (getPassengerTravel() != null) isEqual = false;
                }
                else {
                    if (!((Carpooling)obj).getPassengerTravel().equals(getPassengerTravel())) isEqual = false;
                }
                if (((Carpooling)obj).getDriverCourse() == null) {
                    if (getDriverCourse() != null) isEqual = false;
                }
                else {
                    if (!((Carpooling)obj).getDriverCourse().equals(getDriverCourse())) isEqual = false;
                }
            }
        }
        return isEqual;
    }


    private void readObject(final ObjectInputStream ois) throws IOException,
            ClassNotFoundException {

        this.id = ois.readInt();
        this.driverCourse = (DriverCourse) ois.readObject();
        this.passengerTravel = (PassengerTravel) ois.readObject();

        double latitude = ois.readDouble();
        double longitude = ois.readDouble();
        if (latitude != 99.9 && longitude != 99.9) {
            this.pickupPoint = new LatLng(latitude, longitude);
        }
        latitude = ois.readDouble();
        longitude = ois.readDouble();
        if (latitude != 99.9 && longitude != 99.9) {
            this.dropoffPoint = new LatLng(latitude, longitude);
        }

        this.pickupTime = (Date) ois.readObject();
        this.fare = ois.readFloat();
        this.state = CarpoolingState.valueOf((String) ois.readObject());


    }

    private void writeObject(final ObjectOutputStream oos) throws IOException {
        oos.writeInt(this.id);
        oos.writeObject(this.driverCourse);
        oos.writeObject(this.passengerTravel);
        oos.writeDouble(this.pickupPoint == null ? 99.9 : pickupPoint.latitude);
        oos.writeDouble(this.pickupPoint == null ? 99.9 : pickupPoint.longitude);
        oos.writeDouble(this.dropoffPoint == null ? 99.9 : dropoffPoint.latitude);
        oos.writeDouble(this.dropoffPoint == null ? 99.9 : dropoffPoint.longitude);
        oos.writeObject(this.pickupTime);
        oos.writeFloat(this.fare);
        oos.writeObject(this.state.name());
    }

	@Override
	public String toString() {
		return "Carpooling [id=" + id + ", driverCourse=" + driverCourse
				+ ", passengerTravel=" + passengerTravel + ", pickupPoint="
				+ pickupPoint + ", dropoffPoint=" + dropoffPoint
				+ ", pickupTime=" + pickupTime + ", fare=" + fare + ", state="
				+ state + "]";
	}



}
