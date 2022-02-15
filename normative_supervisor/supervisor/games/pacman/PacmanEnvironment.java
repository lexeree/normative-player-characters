package supervisor.games.pacman;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import supervisor.games.Environment;
import supervisor.games.GameObject;
import util.MethodNotImplementedException; 

/**
 * Old, extensive class extending Environment for the Pac-Man game. Could use some TLC.
 * @author emery
 *
 */

public class PacmanEnvironment extends Environment {
	File grid;
	ArrayList<PacmanGameObject> walls = new ArrayList<PacmanGameObject>();
	ArrayList<PacmanGameObject> food = new ArrayList<PacmanGameObject>();
	ArrayList<PacmanGameObject> characters = new ArrayList<PacmanGameObject>();
	ArrayList<PacmanGameObject> ghosts = new ArrayList<PacmanGameObject>();
	PacmanGameObject pacman;
	
	public PacmanEnvironment(File gridfile) throws MethodNotImplementedException {
		super(gridfile);
		grid = gridfile;
		int y = 0;
        int x = 0;
		try {
			//SLOPPY HACKY SOLUTION
			FileReader file1 = new FileReader(gridfile);
			BufferedReader br1 = new BufferedReader(file1);
			int l = 0;
			while (br1.readLine() != null) l++;
			br1.close();
			FileReader file = new FileReader(gridfile);
			BufferedReader br = new BufferedReader(file);
			y = l;
            String line = br.readLine();
            int j = 0;
            while (line != null) { 
            	String [] objs = line.split(" ");
            	if(objs.length > x) {
            		x = objs.length;
            	}
            	for(int i=0; i<objs.length; i++) {
            		String s = objs[i];
            	    	if(s.equals("w")) {
            	    	PacmanGameObject obj = new PacmanGameObject(s, i, y-j-1);
            			walls.add(obj);
            		}
            		else if(s.equals("f")) {
            			PacmanGameObject obj = new PacmanGameObject(s, i, y-j-1);
            			food.add(obj);
            		}
            		else if(s.equals("p")) {
            			PacmanGameObject obj = new PacmanGameObject(s, i, y-j-1);
            			pacman = obj;
            			characters.add(pacman);
            		}
            		else if(s.equals("b")) {
            			PacmanGameObject obj = new PacmanGameObject(s, i, y-j-1);
            			ghosts.add(obj);
            			characters.add(obj);
            		}
            		else if(s.equals("g")) {
            			PacmanGameObject obj = new PacmanGameObject(s, i, y-j-1);
            			ghosts.add(obj);
            			characters.add(obj);
            		}
            		else if(s.equals("o")) {
            			PacmanGameObject obj = new PacmanGameObject(s, i, y-j-1);
            			ghosts.add(obj);
            			characters.add(obj);
            		}
            	}
            	line = br.readLine();
            	j += 1;
            }
            br.close();
        } 
        catch (Exception e) { 
            e.printStackTrace();
        }
		gridX = x;
		gridY = y;
	}
	
	public PacmanEnvironment(int x, int y, ArrayList<PacmanGameObject> w, ArrayList<PacmanGameObject> f, 
			ArrayList<PacmanGameObject> chars) {
		super(x, y, new ArrayList<String>());
		walls = w;
		food = f;
		for(PacmanGameObject ch : chars) {
			if(ch.getType().equals("p")) {
				pacman = ch;
				characters.add(pacman);
			}
			else {
				ghosts.add(ch);
				characters.add(ch);
			}
		}
	}
	
	
	
	public ArrayList<PacmanGameObject> getWalls(){
		return walls;
	}
	
	
	public ArrayList<PacmanGameObject> getFood(){
		return food;
	}
	
	
	public PacmanGameObject getPacman() {
		return pacman;
	}
	
	
	public ArrayList<PacmanGameObject> getGhosts(){
		return ghosts;
	}
	
	public PacmanGameObject getGhost(String colour){
		PacmanGameObject go = new PacmanGameObject();
		for(PacmanGameObject g : ghosts) {
			if(g.getLabel().contains(colour)) {
				go = g;
			}
		}
		return go;
	}
	
	public PacmanGameObject getObject(String name){
		PacmanGameObject go = new PacmanGameObject();
		if(name.contains("pacman")) {
			go = pacman;
		}
		for(PacmanGameObject g : ghosts) {
			if(g.getLabel().contains(name)) {
				go = g;
			}
		}
		return go;
	}
	
	
	public void deleteFood(float x, float y) {
		try {
			PacmanGameObject f = new PacmanGameObject("f", x, y);
			food.remove(f);
		}
		catch (Exception e) {
			System.out.println("No food here.");
		}
	}
	
	public void deleteCharacter(GameObject obj) {
		try {
			characters.remove(obj);
			if(!obj.getType().equals("p")) {
				ghosts.remove(obj);
			}
		}
		catch (Exception e) {
			System.out.println("No character here.");
		}
	}
	
	public void setPacman(float x, float y) {
		pacman = new PacmanGameObject("p", x, y);
	}
	
		
	public void printGame() {
		for(int j = 0; j < gridY; j++) {
			String ln = "|";
			for(int i = 0; i < gridX; i++) {
				boolean done = false;
				if(pacman.getCoordX() == i && pacman.getCoordY() == gridY-j-1) {
					ln = ln + "p";
					done = true;
				} else {
					for(GameObject wall : walls) {
						if(wall.getCoordX() == i && wall.getCoordY() == gridY-j-1) {
							ln = ln + "*";
							done = true;
						}
					}
					for(GameObject f : food) {
						if(f.getCoordX() == i && f.getCoordY() == gridY-j-1) {
							ln = ln + ".";
							done = true;
						}
					}
					for(GameObject ghost : ghosts) {
						if(ghost.getCoordX() == i && ghost.getCoordY() == gridY-j-1) {
							ln = ln + ghost.getType();
							done = true;
						}
					}
				}
				if(!done) {
					ln = ln + " ";
				}
				ln = ln + "|";
			}
			System.out.println(ln);
		}
	}

}
