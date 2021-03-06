package harrypotter.model.tournament;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import harrypotter.exceptions.InCooldownException;
import harrypotter.exceptions.InvalidTargetCellException;
import harrypotter.exceptions.NotEnoughIPException;
import harrypotter.exceptions.NotEnoughResourcesException;
import harrypotter.exceptions.OutOfBordersException;
import harrypotter.exceptions.OutOfRangeException;
import harrypotter.model.character.Champion;
import harrypotter.model.character.Wizard;
import harrypotter.model.character.WizardListener;
import harrypotter.model.magic.DamagingSpell;
import harrypotter.model.magic.HealingSpell;
import harrypotter.model.magic.Potion;
import harrypotter.model.magic.RelocatingSpell;
import harrypotter.model.magic.Spell;
import harrypotter.model.world.Cell;
import harrypotter.model.world.ChampionCell;
import harrypotter.model.world.CollectibleCell;
import harrypotter.model.world.CupCell;
import harrypotter.model.world.Direction;
import harrypotter.model.world.EmptyCell;
import harrypotter.model.world.ObstacleCell;
import harrypotter.model.world.TreasureCell;
import harrypotter.model.world.WallCell;

public abstract class Task implements WizardListener{
	private ArrayList <Champion> champions ;
	private Champion currentChamp ;
	private Cell [][] map ;
    private int allowedMoves ;
    private boolean traitActivated ;
    private ArrayList <Potion> potions ;
    private TaskListener listener;
    private boolean foundCollectible;
    
    public Task(ArrayList<Champion> champions)throws IOException
    {
      this.champions = champions ;
      map = new Cell[10][10];
      allowedMoves = 1 ;
      traitActivated = false ;
      potions = new ArrayList<Potion>();
      this.foundCollectible = false;
      loadPotions("Potions.csv");
      restoreStats();
      setListeners();
    }
    private void loadPotions(String filePath)throws IOException
    {
    	String currentLine = "";
    	FileReader fileReader= new FileReader(filePath);
    	BufferedReader br = new BufferedReader(fileReader);
    	while ((currentLine = br.readLine()) != null)
    	{
    	   String [] result= currentLine.split(",");
    	   Potion p = new Potion(result[0],Tournament.parseInt(result[1]));
    	   //Insert in ArrayList
    	   potions.add(p);
    	}
    }
    public void setListeners()
    {
    	for(int i = 0 ; i < this.champions.size() ; i++)
    	{
    		Wizard c = (Wizard) this.champions.get(i);
    		c.setListener(this);
    	}
    }
    public boolean getFoundCollectible()
    {
    	return this.foundCollectible;
    }
    public void setFoundCollectible(boolean f)
    {
    	this.foundCollectible = f;
    }
    public ArrayList <Champion> getChampions()
    {
    	return champions ;
    }
    public Champion getCurrentChamp()
    {
    	return currentChamp ;
    }
    public void setCurrentChamp(Champion currentChamp)
    {
    	this.currentChamp = currentChamp ;
    }
    public Cell [][] getMap()
    {
    	return map ;
    }
    public int getAllowedMoves()
    {
    	return allowedMoves ;
    }
    public void setAllowedMoves(int allowedMoves)
    {
    	this.allowedMoves = allowedMoves ;
    }
    public boolean isTraitActivated()
    {
    	return traitActivated ;
    }
    public void setTraitActivated(boolean traitActivated)
    {
    	this.traitActivated = traitActivated ;
    }
    public ArrayList<Potion> getPotions()
    {
    	return potions;
    }
    public TaskListener getListener()
    {
    	return listener;
    }
    public void setListener(TaskListener listener)
    {
    	this.listener = listener;
    }
    public void shuffleChampions()
    {
    	Collections.shuffle(champions);
    }
    public void generateMap() throws IOException
    {
    	generateMapWithEmptyCells();
    	addChampionsToMap(champions.size());
    }
    public void generateMapWithEmptyCells()
    {
    	for(int i = 0;i < 10;i++)
    	{
    		for(int j = 0;j < 10;j++){
    			map[i][j] = new EmptyCell();
    		}
    	}
    }
    private void addChampionsToMap(int x)
    {
    	switch(x)
    	{
    	case 1 :
        	map[9][0] = new ChampionCell(champions.get(0));
        	setChampionsLocation(1);
        	break;
    	case 2 :
    		map[9][0] = new ChampionCell(champions.get(0));
        	map[9][9] = new ChampionCell(champions.get(1));
        	setChampionsLocation(2);
        	break;
    	case 3 :
    		map[9][0] = new ChampionCell(champions.get(0));
        	map[9][9] = new ChampionCell(champions.get(1));
        	map[0][9] = new ChampionCell(champions.get(2));
        	setChampionsLocation(3);
        	break;
    	case 4 :
    		map[9][0] = new ChampionCell(champions.get(0));
        	map[9][9] = new ChampionCell(champions.get(1));
        	map[0][9] = new ChampionCell(champions.get(2));
        	map[0][0] = new ChampionCell(champions.get(3));
        	setChampionsLocation(4);
        	break;
        default :
        	break;
    	}
    }
    private void setChampionsLocation(int num)
    {
    	switch(num)
    	{
    	  case 1 : ((Wizard)this.champions.get(0)).setLocation(new Point(9,0));break;
    	  case 2 : ((Wizard)this.champions.get(0)).setLocation(new Point(9,0));
    	  		   ((Wizard)this.champions.get(1)).setLocation(new Point(9,9));break;
    	  case 3 : ((Wizard)this.champions.get(0)).setLocation(new Point(9,0));
    	  		   ((Wizard)this.champions.get(1)).setLocation(new Point(9,9));
    	  		   ((Wizard)this.champions.get(2)).setLocation(new Point(0,9));break;
    	  case 4 : ((Wizard)this.champions.get(0)).setLocation(new Point(9,0));
    	  		   ((Wizard)this.champions.get(1)).setLocation(new Point(9,9));
    	  		   ((Wizard)this.champions.get(2)).setLocation(new Point(0,9));
    	  		   ((Wizard)this.champions.get(3)).setLocation(new Point(0,0));break;
    	}
    }
    public boolean isEmptyCell(Cell c)
    {
    	return c instanceof EmptyCell;
    }
    public void addRandomPotions()
    {
    	for(int i = 0 ;i < 10;i++)
    	{
    		Random n = new Random(); 
    		int a = n.nextInt(getPotions().size());
    		addPotionsToMap(a);
    	}
    }
    private void addPotionsToMap(int a)
    {
    	Random n = new Random();
    	int x = n.nextInt(10);
    	int y = n.nextInt(10);
    	if(isEmptyCell(map[x][y]))
    		map[x][y] = new CollectibleCell(potions.get(a));
    	else
    		addPotionsToMap(a);
    }
    public static boolean isAlive(Champion c){
    	Wizard x = (Wizard) c;
    	return x.getHp() > 0;
    }
    
    public static ArrayList<Point> getAdjacentCells(Point p) throws OutOfBordersException{ // Return {UP, Down, Right, Left, CurrentPoint}
    	int x = (int) p.getX();
    	int y = (int) p.getY();
    	ArrayList<Point> a = new ArrayList<Point>();
    	a.add(new Point(x-1, y));
    	a.add(new Point(x+1, y));
    	a.add(new Point(x, y+1));
    	a.add(new Point(x, y-1));
    	a.add(p);
    	for (int i = 0; i < a.size()-1;i++){
    		if (!(insideBoundary(a.get(i)))){
    			a.set(i, null);
    			
    		}
    	}
    	return a;
    }
    
    public static boolean insideBoundary(Point p) throws OutOfBordersException{ // Checks if point is inside the map boundaries
    	if(p == null)
    		throw new OutOfBordersException();
    	int x = (int) p.getX();
    	int y = (int) p.getY();
    	return !(y < 0 || y > 9 || x < 0 || x > 9);
    }
    
    public void removeWizard(Champion c)
    {
       champions.remove(c);
    }
    
    public static boolean isNull(int a , ArrayList<Point> p)
	{
		return p.get(a) == null;
	}
    
    public abstract void finalizeAction() throws IOException, OutOfBordersException;
    
    private void restoreStats()
    {
    	for(int i = 0 ; i < champions.size() ; i++)
    	{
    		Wizard c = (Wizard) champions.get(i);
    		c.setHp(c.getDefaultHp());
    		c.setIp(c.getDefaultIp());
    		c.setTraitCooldown(0);
    		restoreSpells(c);
    		this.allowedMoves = 1;
    		this.traitActivated = false;
    	}
    }
    private void restoreSpells(Wizard c)
    {
    	for(int j = 0 ; j < c.getSpells().size() ; j++)
    	{
    		Spell a = c.getSpells().get(j);
    		a.setCoolDown(0);
    	}
    }
    public void moveForward() throws IOException, OutOfBordersException, InvalidTargetCellException
    {
    	Wizard c = (Wizard) this.currentChamp;
    	Point p = c.getLocation();
        Point up = getAdjacentCells(p).get(0);
        //Calls a method that checks the target cells and throw the corresponding exceptions
    	checkMoveValidity(up);
    	if(this.allowedMoves > 0)
    		makeMove(up);
    }
    public void moveBackward() throws IOException, OutOfBordersException, InvalidTargetCellException
    {
    	Wizard c = (Wizard) this.currentChamp;
    	Point p = c.getLocation();
    	Point back = getAdjacentCells(p).get(1);
    	//Calls a method that checks the target cells and throw the corresponding exceptions
    	checkMoveValidity(back);
    	if(this.allowedMoves > 0)
    		makeMove(back);
    }
    public void moveRight() throws IOException, OutOfBordersException, InvalidTargetCellException
    {
    	Wizard c = (Wizard) this.currentChamp;
    	Point p = c.getLocation();
    	Point right = getAdjacentCells(p).get(2);
    	//Calls a method that checks the target cells and throw the corresponding exceptions
    	checkMoveValidity(right);
    	if(this.allowedMoves > 0)
    		makeMove(right);
    }
    public void moveLeft() throws IOException, OutOfBordersException, InvalidTargetCellException
    {
    	Wizard c = (Wizard) this.currentChamp;
    	Point p = c.getLocation();
    	Point left = getAdjacentCells(p).get(3);
    	//Calls a method that checks the target cells and throw the corresponding exceptions
    	checkMoveValidity(left);
    	if(this.allowedMoves > 0)
    		makeMove(left);
    }
    private void checkMoveValidity(Point m) throws OutOfBordersException, InvalidTargetCellException
    {
    	if(m == null)
    		throw new OutOfBordersException();
    	
    	if(map[m.x][m.y] instanceof ChampionCell || 
    			map[m.x][m.y] instanceof WallCell || 
    			map[m.x][m.y] instanceof ObstacleCell  )
    		throw new InvalidTargetCellException();
    }
    private void makeMove(Point a) throws OutOfBordersException, InvalidTargetCellException
    {	
    	Wizard c = (Wizard) this.currentChamp;
    	Point b = c.getLocation();
    	int e = (int) b.getX();
    	int f = (int) b.getY();
    	int x = (int) a.getX();
    	int y = (int) a.getY();
    	Cell new1 = this.map[x][y];
    	if(new1 instanceof EmptyCell)
    	{
    		this.map[e][f] = new EmptyCell();
    		c.setLocation(a);
    		this.allowedMoves = this.allowedMoves - 1;
    	}
    	else if(new1 instanceof CollectibleCell)
    	{
    		c.getInventory().add(((CollectibleCell) new1).getCollectible());
    		this.map[e][f] = new EmptyCell();
    		c.setLocation(a);
    		this.allowedMoves = this.allowedMoves - 1;
    		this.foundCollectible = true;
    	}
    	else if(new1 instanceof TreasureCell)
    	{ 
    		if(((TreasureCell) new1).getOwner() == this.currentChamp)
    		{
    			this.map[e][f] = new EmptyCell();
    			c.setLocation(a);
    			this.allowedMoves = this.allowedMoves - 1;
    		}
    		else
    		    throw new InvalidTargetCellException("Cannot move to other champion treasure");
    	}
    	else if(new1 instanceof CupCell)
    	{
    		this.map[e][f] = new EmptyCell();
    		c.setLocation(a);
    		this.allowedMoves = this.allowedMoves - 1;
    	}
    }
    public static Point directionToPoint(Direction d, Champion c) throws OutOfBordersException{
    	Wizard w           = (Wizard) c;
    	ArrayList<Point> a = getAdjacentCells(w.getLocation());
    	Point p            = w.getLocation();
    	switch(d){
    	case FORWARD : p =  a.get(0); break;
    	case BACKWARD: p =  a.get(1); break;
    	case RIGHT   : p =  a.get(2); break;
    	case LEFT    : p =  a.get(3); break;
    	}
    	return p;
    }
    
    public Point getTargetPoint(Direction d) throws OutOfBordersException
    {
    	return  directionToPoint(d, this.getCurrentChamp());
    }
    
    public void useSpell(Spell s) throws NotEnoughIPException
    {
    	s.setCoolDown(s.getDefaultCooldown());
    	Wizard w = (Wizard) this.getCurrentChamp();
    	w.setIp(w.getIp() - s.getCost());
    	this.allowedMoves = this.allowedMoves - 1;
    }
    
   public abstract void castDamagingSpell(DamagingSpell s, Direction d) throws IOException, NotEnoughIPException, InCooldownException, OutOfBordersException, InvalidTargetCellException;
    
    public void castRelocatingSpell(RelocatingSpell s,Direction d,Direction t,int r) throws IOException, NotEnoughIPException, InCooldownException, OutOfRangeException, OutOfBordersException, InvalidTargetCellException
    {
    	//Checks Cooldown
    	if (s.getCoolDown() > 0){
    		throw new InCooldownException(s.getCoolDown());
    	}
    	//Checks enough ip
    	int cost = s.getCost();
    	int remainingip = cost - ((Wizard)currentChamp).getIp();
    	if(remainingip >= 0)
    		throw new NotEnoughIPException(cost, remainingip);
    	//Checks next and current inside the Map
    	Point current = getTargetPoint(d);
    	Point next = getExactPosition(getTargetPoint(t),t,r -1);
    	if(next == null || current == null || !insideBoundary(next) || !insideBoundary(current))
    		throw new OutOfBordersException();
    	//Checks if the target cells are valid
    	if(!(map[(int)next.getX()][(int)next.getY()] instanceof EmptyCell)
    			|| map[(int)current.getX()][(int)current.getY()] instanceof CollectibleCell
    			|| map[(int)current.getX()][(int)current.getY()] instanceof TreasureCell
    		    || map[current.x][current.y] instanceof CupCell
    		    || map[current.x][current.y] instanceof EmptyCell
    		    || map[current.x][current.y] instanceof WallCell)
    		throw new InvalidTargetCellException();
    	//Checks if range requested equal to spell range
    	if (r > s.getRange()){
    		throw new OutOfRangeException(s.getRange());
    	}
    	
    	int x = (int) next.getX();
    	int y = (int) next.getY();
    	int a = (int) current.getX();
    	int b = (int) current.getY();
    	Cell n = this.map[x][y];
    	Cell c = this.map[a][b];
    	this.map[x][y] = c;
        this.map[a][b] = n;
        	

    	useSpell(s);
    }
    public Point getExactPosition(Point p , Direction t ,int range) throws OutOfBordersException
    {   
    	if(p == null)
    		throw new OutOfBordersException();	
    	int x = (int) p.getX();
    	int y = (int) p.getY();
    	switch(t)
    	{
    	 case FORWARD : x = x - range; break;
    	 case BACKWARD: x = x + range; break;
    	 case RIGHT   : y = y + range; break;
    	 case LEFT    : y = y - range; break;
    	}
    	return new Point(x,y);
    }
    
    public void castHealingSpell(HealingSpell s) throws IOException, NotEnoughIPException, InCooldownException, OutOfBordersException{
    	//Checks spell cooldown
    	if (s.getCoolDown() > 0){
    		throw new InCooldownException(s.getCoolDown());
    	}
    	//Checks enough ip to make action
    	Wizard w = (Wizard) this.currentChamp;
       	int cost = s.getCost();
    	int remainingip = cost - (w.getIp());
    	if(remainingip >= 0)
    		throw new NotEnoughIPException(cost, remainingip);
    	
    	w.setHp(w.getHp() + s.getHealingAmount());
    	useSpell(s);
    }
    
    public void endTurn() throws IOException, OutOfBordersException
    {
    	Wizard c = (Wizard) this.currentChamp;
    	decrementTraitCooldown(c);
    	decrementSpellsCooldown(c);
    	setNextChamp();
    	this.allowedMoves = 1;
    	this.traitActivated = false;
    }
    private static void decrementTraitCooldown(Wizard c)
    {
    	if(c.getTraitCooldown() > 0)
    		c.setTraitCooldown(c.getTraitCooldown()-1);
    }
    private static void decrementSpellsCooldown(Wizard c)
    {
    	ArrayList <Spell> spells = c.getSpells();
    	for(int i = 0 ; i < spells.size() ; i++)
    	{
    		if(spells.get(i).getCoolDown() > 0)
    			spells.get(i).setCoolDown(spells.get(i).getCoolDown()-1);
    	}
    }
    private void setNextChamp()
    {
    	int i = champions.indexOf(this.currentChamp)+1;
        if(i == champions.size())
    	{
    		this.currentChamp = champions.get(0);
        }
    	else
    		this.currentChamp = champions.get(i);
    }
    
    public void usePotion(Potion p) throws OutOfBordersException, IOException
    {
    	Wizard c = (Wizard) this.currentChamp;
    	c.setIp(c.getIp()+ p.getAmount());
    	c.getInventory().remove(p);
    }
    
    public void onGryffindorTrait() throws InCooldownException
    {
    	Wizard c = (Wizard) this.currentChamp;
    	if (c.getTraitCooldown() > 0){
    		throw new InCooldownException(c.getTraitCooldown());
    	}
    	this.allowedMoves = 2;
    	this.traitActivated = true;
    	c.setTraitCooldown(4);
    }
    public abstract void onSlytherinTrait(Direction d) throws IOException, InCooldownException, OutOfBordersException, InvalidTargetCellException;
    
    public void onHufflepuffTrait() throws InCooldownException
    {
    	Wizard c = (Wizard) this.currentChamp;
    	if (c.getTraitCooldown() > 0){
    		throw new InCooldownException(c.getTraitCooldown());
    	}
    	this.traitActivated = true;
    }
}
