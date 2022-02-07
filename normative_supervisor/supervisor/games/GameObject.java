package supervisor.games;

import java.util.ArrayList;

/**
 * Base class for entities in a game. Only to be used with games 
 * with custom Environment subclasses.
 * 
 * Unless a bug is found, please do not alter this class.
 * 
 * @author emery
 *
 */


public class GameObject {
	boolean scared = false;
	String objectType;
	float objectX;
	float objectY;
	ArrayList<String> objects = new ArrayList<String>();
	
	
	
	public GameObject() {
		objectType = "";
	}
	
    public GameObject(String objT, float objX, float objY) {
		if (objects.contains(objT)) {
			objectType = objT;
		}
		else {
			objectType = "";
		}
		objectX = objX;
		objectY = objY;
	}
    
    
    protected void buildDict() {
    	
    }
    
    public void setType(String s) {
    	objectType = s;
    }
    
    
    public void setCoordX(float x) {
    	objectX = x;
    }

    
    public void setCoordY(float y) {
 	    objectY = y;
    }
    
    
    public String getType() {
    	return objectType;
    }
    
    
    public float getCoordX() {
    	return objectX;
    }
    
    
    public float getCoordY() {
    	return objectY;
    }
    
    
    public String getLabel() {
    	String lab = objectType;
    	return lab;
    }

}
