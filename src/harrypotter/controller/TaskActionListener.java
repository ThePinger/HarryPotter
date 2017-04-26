package harrypotter.controller;

import java.io.IOException;

import harrypotter.exceptions.OutOfBordersException;

public interface TaskActionListener {

	public void moveUp();
	public void moveDown();
	public void moveRight();
	public void moveLeft();
	public void updateNEWPanels() throws OutOfBordersException, IOException;
	//public void removeChamp();
	//public void removeObstacle();
	//public void activateTrait();
	//public void damagingSpell();
	//public void healingSpell();
	//public void relocatingSpell();
}
