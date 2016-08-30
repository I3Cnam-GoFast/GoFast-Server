package com.i3cnam.gofast.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Reresents one travel made by a passenger, 
 * it can be associated to one carpool
 */
@Entity
public class PassengerTravel implements Serializable{

    @Id  @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private User passenger; // passenger doing or asking for a travel
    private Place origin; // initial position of the passenger
    private Place destination; // place where the passenger wants to go to
    private int radius = 500; // distance that the passenger accepts to walk to the pickup and dropoff points

    /*
    GETTERS AND SETTERS ----------------------------------------------------------------------------
     */
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public User getPassenger() {
        return passenger;
    }

    public void setPassenger(User passenger) {
        this.passenger = passenger;
    }

    public Place getOrigin() {
        return origin;
    }

    public void setOrigin(Place origin) {
        this.origin = origin;
    }

    public Place getDestination() {
        return destination;
    }

    public void setDestination(Place destination) {
        this.destination = destination;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     * prepares the parameters string to insert into url like : par1=val1&par2=val2 ...
     * @return parameters string 
     */
    public String getParametersString() {
        String returnString = "passenger=" + getPassenger().getNickname();
        returnString += "&origin=";
        returnString += origin.getCoordinates().latitude + ",";
        returnString += origin.getCoordinates().longitude;
        returnString += "&destination=";
        returnString += destination.getCoordinates().latitude + ",";
        returnString += destination.getCoordinates().longitude;
        returnString += "&radius=";
        returnString += Integer.toString(radius);
        return returnString;
    }

    public JSONObject getJsonObject() {
    	JSONObject json = new JSONObject();
        DateFormat format = new SimpleDateFormat("y/M/d H:m");

    	try {
			json.put("id", this.id);
			json.put("origin", this.origin.getJsonObject());
			json.put("destination", this.destination.getJsonObject());
			json.put("radius", this.radius);
			json.put("passenger", this.passenger);

		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	return json;
    }

    

    @Override
	public String toString() {
		return "PassengerTravel [id=" + id + ", passenger=" + passenger
				+ ", origin=" + origin + ", destination=" + destination
				+ ", radius=" + radius + "]";
	}

	private void readObject(final ObjectInputStream ois) throws IOException,
            ClassNotFoundException {
        this.id = ois.readInt();
        this.passenger = (User) ois.readObject();
        this.origin  = (Place) ois.readObject();
        this.destination = (Place) ois.readObject();
        this.radius = ois.readInt();

    }
	
    private void writeObject(final ObjectOutputStream oos) throws IOException {
        oos.writeInt(this.id);
        oos.writeObject(this.passenger);
        oos.writeObject(this.origin);
        oos.writeObject(this.destination);
        oos.writeInt(this.radius);
    }



}
