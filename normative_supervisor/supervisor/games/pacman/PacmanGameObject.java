package supervisor.games.pacman;

import java.util.ArrayList;
import java.util.HashMap;

import supervisor.games.GameObject;

/**
 * Extension of the GameObject class for entities in the Pac-Man game.
 * 
 * @author emery
 *
 */

public class PacmanGameObject extends GameObject{
	boolean scared = false;
	String objectType;
	float objectX;
	float objectY;
	ArrayList<String> objects = new ArrayList<String>();
	HashMap<String, String> objTypeMap = new HashMap<String, String>();
	
	
	
	public PacmanGameObject() {
		super();
		buildDict();
	}
    
	public PacmanGameObject(String objT, float objX, float objY) {
		super(objT, objX, objY);
		buildDict();
	}
    
    protected void buildDict() {
    	objTypeMap.put("w", "walls");
    	objTypeMap.put("f", "food");
    	objTypeMap.put("p", "pacman");
    	objTypeMap.put("b", "bGhost");
    	objTypeMap.put("g", "gGhost");
    	objTypeMap.put("o", "oGhost");
    	objects.addAll(objTypeMap.keySet());
    }
    
    
    public void scared() {
    	scared = true;
    }
    
    public boolean isScared() {
    	return scared;
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
    	String lab = objTypeMap.get(objectType);
    	return lab;
    }


}
