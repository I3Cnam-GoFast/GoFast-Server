package com.i3cnam.gofast.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import systr.cartographie.Operations;

/**
 * Represents one course made by a driver, 
 * it can be associated to one or more carpools
 */
@Entity @Access(AccessType.FIELD)
public class DriverCourse implements Serializable{

    @Id  @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private User driver; // the user thar makes the course
//    @ManyToOne(cascade=CascadeType.PERSIST)
    private Place origin; // start position of the driver
//    @ManyToOne(cascade=CascadeType.PERSIST)
    private Place destination; // final position of the driver
    private List<Step> steps; // set of steps
    @Column(columnDefinition = "TEXT")
    private String encodedPoints; // encoded gps points of path
    private LatLng actualPosition; // actual position of the driver
    private Date positioningTime; // last time when the position was updated
    private boolean obsolete; // passes to true when the course is finished
    
    // calculated bounds of the path
    private double northest = 0;
    private double southest = 0;
    private double westest = 0;
    private double eastest = 0;
    
    // optimisation for nearest point computation
    @Transient
    private LatLng lastComputedPoint = null;
    @Transient
    private int lastComputedSegment;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
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

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public String getEncodedPoints() {
        return encodedPoints;
    }

    public void setEncodedPoints(String encodedPoints) {
        this.encodedPoints = encodedPoints;
    }

    public LatLng getActualPosition() {
        return actualPosition;
    }

    public void setActualPosition(LatLng actualPosition) {
        this.actualPosition = actualPosition;
    }

	public boolean isObsolete() {
		return obsolete;
	}
	
	public void setObsolete(boolean obsolete) {
		this.obsolete = obsolete;
	}    
	
    /**
     * deletes the gps points of the path previous to the actual position
     */
    public void updatePath() {
    	List<LatLng> actualPath = getPath();
    	int segment = getSegment(actualPosition);
        // remove previous points
        for (int j = 0 ; j <= segment ; j++) {
            actualPath.remove(0);
        }
        encodedPoints = PolyUtil.encode(actualPath);
    }
    
    /**
     * finds the nearest point of the path to the point given as parameter
     * @param lookupPoint
     * @return the nearest point of the path
     */
    public LatLng getNearestPoint(LatLng lookupPoint) {
    	List<LatLng> actualPath = getPath();
    	int i = 0, nearestSegment = 0;
    	double delta;
        double minDist = 100000;

        LatLng nearestPoint = null;
    	
        while (i < (actualPath.size() - 1)) {

            // calculate nearest point to the segment
            nearestPoint = Operations.nearestPoint(actualPath.get(i), actualPath.get(i + 1),  lookupPoint);

            // calculate the distance to the nearest point
            delta = Operations.dist2PointsEnM(lookupPoint, nearestPoint);
            if (minDist > delta) {
                minDist = delta;
                nearestSegment = i;
            }
            i++;
        }
        lastComputedPoint = nearestPoint;
        lastComputedSegment = nearestSegment;
        return nearestPoint;
    }
    
    /**
     * returns the segment (index) where the point passed as parameter belong 
     * @param point
     * @return
     */
    public int getSegment(LatLng point) {
    	if (lastComputedPoint != null) {
        	if (point.equals(lastComputedPoint)) {
        		return lastComputedSegment;
        	}    		
    	}
    	getNearestPoint(point);
		return lastComputedSegment;
    }

    public Date getPositioningTime() {
        return positioningTime;
    }

    public void setPositioningTime(Date positionningTime) {
        this.positioningTime = positionningTime;
    }

    /**
     * decodes the ecoded points string and returns a list of the gps points
     * @return
     */
    public List<LatLng> getPath() {
    	System.out.println(encodedPoints);
        return PolyUtil.decode(encodedPoints);
    }

    /**
     * returns true if the north south east and west bounds are defined
     * @return 
     */
    public boolean boundsDefined() {
    	return (northest != 0 || southest != 0 || eastest != 0 || westest!= 0);
    }

    /**
     * Updates the nort south east and west limits of the path 
     * @return
     */
    public void defineBounds() {
        // init bounds with origin point
        northest = origin.getCoordinates().latitude;
        southest = origin.getCoordinates().latitude;
        westest = origin.getCoordinates().longitude;
        eastest = origin.getCoordinates().longitude;

        for (LatLng onePoint : getPath()) {
            // for each point in the path, update bounds if larger than current
            northest = Math.max(northest, onePoint.latitude);
            southest = Math.min(southest, onePoint.latitude);
            westest = Math.max(westest, onePoint.longitude);
            eastest = Math.min(eastest, onePoint.longitude);
        }
//        return new LatLngBounds(new LatLng(northest,westest),new LatLng(southest,eastest));
    }    
    
    /**
     * prepares the parameters string to insert into url like : par1=val1&par2=val2 ...
     * @return parameters string 
     */    
    public String getParametersString() {
        String returnString = "driver=" + getDriver().getNickname();
        returnString += "&origin=";
        returnString += origin.getCoordinates().latitude + ",";
        returnString += origin.getCoordinates().longitude;
        returnString += "&destination=";
        returnString += destination.getCoordinates().latitude + ",";
        returnString += destination.getCoordinates().longitude;
        returnString += "&encoded_points=" + getEncodedPoints();
        return returnString;
    }

    public JSONObject getJsonObject() {
    	JSONObject json = new JSONObject();
        DateFormat format = new SimpleDateFormat("y/M/d H:m");

    	try {
			json.put("id", this.id);
			json.put("origin", this.origin.getJsonObject());
			json.put("destination", this.destination.getJsonObject());
			json.put("encoded_points", this.encodedPoints);
			json.put("actual_position", (this.actualPosition == null ? null : this.actualPosition.getJsonObject()));
			json.put("positioning_time", (this.positioningTime == null ? null : format.format(this.positioningTime)));
			json.put("driver", this.driver.getJsonObject());

		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return json;
    }
    

    @Override
	public String toString() {
		return "DriverCourse [id=" + id + ", driver=" + driver + ", origin="
				+ origin + ", destination=" + destination + ", steps=" + steps
				+ ", encodedPoints=" + encodedPoints + ", actualPosition="
				+ actualPosition + ", positioningTime=" + positioningTime
				+ ", northest=" + northest + ", southest=" + southest
				+ ", westest=" + westest + ", eastest=" + eastest + "]";
	}

	private void readObject(final ObjectInputStream ois) throws IOException,
            ClassNotFoundException {

        this.driver = (User) ois.readObject();
        this.origin = (Place) ois.readObject();
        this.destination = (Place) ois.readObject();
        int stepsNr = ois.readInt();
        for (int i = 0 ; i < stepsNr ; i++) {
            steps.add((Step)ois.readObject());
        }
        this.encodedPoints = (String) ois.readObject();

        double latitude = ois.readDouble();
        double longitude = ois.readDouble();
        if (latitude != 99.9 && longitude != 99.9) {
            this.actualPosition = new LatLng(latitude, longitude);
        }
        this.positioningTime = (Date) ois.readObject();
    }

    private void writeObject(final ObjectOutputStream oos) throws IOException {
        oos.writeObject(this.driver);
        oos.writeObject(this.origin);
        oos.writeObject(this.destination);
        if (steps == null) {
            oos.writeInt(0);
        }
        else {
            oos.writeInt(steps.size());
            for (Step s : steps) {
                oos.writeObject(s);
            }
        }
        oos.writeObject(this.encodedPoints);
        oos.writeDouble(this.actualPosition == null ? 99.9 : actualPosition.latitude);
        oos.writeDouble(this.actualPosition == null ? 99.9 : actualPosition.longitude);
        oos.writeObject(this.positioningTime);
    }



}
