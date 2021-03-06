package harrypotter.model.tournament;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import harrypotter.controller.TaskActionListener;
import harrypotter.exceptions.InCooldownException;
import harrypotter.exceptions.InvalidTargetCellException;
import harrypotter.exceptions.NotEnoughIPException;
import harrypotter.exceptions.OutOfBordersException;
import harrypotter.exceptions.OutOfRangeException;
import harrypotter.model.character.Champion;
import harrypotter.model.character.HufflepuffWizard;
import harrypotter.model.character.Wizard;
import harrypotter.model.magic.DamagingSpell;
import harrypotter.model.magic.HealingSpell;
import harrypotter.model.magic.Potion;
import harrypotter.model.magic.RelocatingSpell;
import harrypotter.model.world.Cell;
import harrypotter.model.world.ChampionCell;
import harrypotter.model.world.CollectibleCell;
import harrypotter.model.world.CupCell;
import harrypotter.model.world.Direction;
import harrypotter.model.world.EmptyCell;
import harrypotter.model.world.Merperson;
import harrypotter.model.world.Obstacle;
import harrypotter.model.world.ObstacleCell;
import harrypotter.model.world.TreasureCell;
import harrypotter.model.world.WallCell;


public class SecondTask extends Task {
	
	private ArrayList<Champion> winners;
	private ArrayList<Point> treasuresLocation;
	private TaskActionListener taskActionListener;
	
	
	public SecondTask(ArrayList<Champion> champions)throws IOException
	{
		super(champions);
		super.shuffleChampions();
		this.treasuresLocation = new ArrayList<Point>();
		this.winners = new ArrayList<Champion>();
		generateMap();
	    super.setCurrentChamp(super.getChampions().get(0));
        
	}
	
	
	public TaskActionListener getTaskActionListener() {
		return taskActionListener;
	}


	public void setTaskActionListener(TaskActionListener taskActionListener) {
		this.taskActionListener = taskActionListener;
	}


	public ArrayList<Champion> getWinners() {
		return winners;
	}
	public void setWinners(ArrayList<Champion> winners) {
		this.winners = winners;
	}
	@Override
	public void generateMap() throws IOException
	{
		super.generateMap();
		super.addRandomPotions();
		addMerpersons();
		addTreasures();
	}
	private void addMerpersons()
	{
		for(int i = 0; i < 40 ;i++)
		{
		   int x = (int)(Math.random()*10);
		   int y = (int)(Math.random()*10);
		   if(!super.isEmptyCell(super.getMap()[x][y]))
			  i--;
		   else
	 	   {   
			 int hp = 200 + (int)(Math.random() * ((300 - 200) + 1));
			 int dmg = 100 + (int)(Math.random() * ((300 - 100) + 1));
			 super.getMap()[x][y] = new ObstacleCell(new Merperson(hp,dmg));
	       }
		 }
	}
	private void addTreasures()
	{
		for(int i = 0; i < super.getChampions().size() ;i++)
		{
		   int x = (int)(Math.random()*10);
		   int y = (int)(Math.random()*10);
		   if(!super.isEmptyCell(super.getMap()[x][y]))
			  i--;
		   else
	 	   {   
			 super.getMap()[x][y] = new TreasureCell(super.getChampions().get(i));
			 addChampsTreasureLocation(x,y);
	       }
		 }
		
	}
	
	public void encounterMerPerson() throws OutOfBordersException
	{ //for the currentChamp
		Wizard c = (Wizard) super.getCurrentChamp();
		Point p  = c.getLocation();
		ArrayList<Point> a = Task.getAdjacentCells(p);
		for (int i = 0;i < a.size()-1;i++)
		{
			if (super.isNull(i, a)){
				continue;
			}
			int x = (int) a.get(i).getX();
			int y = (int) a.get(i).getY();
			if (this.getMap()[x][y] instanceof ObstacleCell )
			{
			  ObstacleCell m =(ObstacleCell)this.getMap()[x][y];
			  Merperson n = (Merperson)m.getObstacle();
			  c.setHp(c.getHp()-n.getDamage());
			  this.taskActionListener.showAttack();
			  if(!super.isAlive(this.getCurrentChamp()))
			  {   
				  this.taskActionListener.removeChamp(c,"Dead");
				  this.getMap()[p.x][p.y] = new EmptyCell();
				  super.removeWizard(super.getCurrentChamp());
			  }
			}
		}
	}
	@Override
	public void finalizeAction() throws IOException, OutOfBordersException
	{
		Wizard c = (Wizard) super.getCurrentChamp();
		Point p = c.getLocation();
		int x = (int) p.getX();
    	int y = (int) p.getY();
    	if(super.getFoundCollectible())
    	{
    		this.taskActionListener.showCollectible();
    		this.setFoundCollectible(false);
    	}
		if(this.getMap()[x][y] instanceof TreasureCell)
		{
			this.taskActionListener.removeChamp(c, "Winner");
			this.winners.add(super.getCurrentChamp());
			super.removeWizard(super.getCurrentChamp());
			this.getMap()[x][y] = new EmptyCell();
			endTurn();
    		return;
		}
	    this.getMap()[x][y] = new ChampionCell(super.getCurrentChamp());
	    if(super.getAllowedMoves() == 0)
	    {
	    	if (c instanceof HufflepuffWizard)
	    	{
	    		if(!(this.isTraitActivated()))
	    			encounterMerPerson();
	    		else
	    			super.setTraitActivated(false);
	    	}
	    	else encounterMerPerson();
	    	endTurn();
	    }
	    else
	    	this.taskActionListener.moveGryffindorOnTrait();
    	
	}
	@Override
	public void moveForward() throws IOException, OutOfBordersException, InvalidTargetCellException
	{
		super.moveForward();
		this.taskActionListener.moveUp();
		finalizeAction();
	}
	@Override
	public void moveBackward() throws IOException, OutOfBordersException, InvalidTargetCellException
	{
		super.moveBackward();
		this.taskActionListener.moveDown();
		finalizeAction();
	}
	@Override
	public void moveRight() throws IOException, OutOfBordersException, InvalidTargetCellException
	{
		super.moveRight();
		this.taskActionListener.moveRight();
		finalizeAction();
	}
	@Override
	public void moveLeft() throws IOException, OutOfBordersException, InvalidTargetCellException
	{
		super.moveLeft();
		this.taskActionListener.moveLeft();
		finalizeAction();
	}
	
	@Override
	 public void castDamagingSpell(DamagingSpell s, Direction d) throws IOException, NotEnoughIPException, InCooldownException, OutOfBordersException, InvalidTargetCellException
	    {
	    	//Checks the spell cooldown
	    	if (s.getCoolDown() > 0){
	    		throw new InCooldownException(s.getCoolDown());
	    	}
	    	//Checks enough ip
	    	int cost = s.getCost();
	    	int remainingip = cost - ((Wizard)getCurrentChamp()).getIp();
	    	if(remainingip > 0)
	    		throw new NotEnoughIPException(cost, remainingip);
	    	//Checks if point not OutOfMap
	    	Point p = getTargetPoint(d);
	    	if(p == null)
	    		throw new OutOfBordersException();
	    	//Checks if the cell is valid to make action
	    	int x = (int) p.getX();
	    	int y = (int) p.getY();
	    	Cell cl = this.getMap()[x][y];
	    	if(getMap()[p.x][p.y] instanceof CollectibleCell || 
	    		    getMap()[p.x][p.y] instanceof EmptyCell ||
	    			getMap()[p.x][p.y] instanceof TreasureCell ||
	    			getMap()[p.x][p.y] instanceof CupCell || 
	    			getMap()[p.x][p.y] instanceof WallCell)
	    		throw new InvalidTargetCellException();
	    	
	    	if (cl instanceof ObstacleCell){
	    		ObstacleCell o = (ObstacleCell) cl;
	    		o.getObstacle().setHp(o.getObstacle().getHp() - s.getDamageAmount());
	    		if(o.getObstacle().getHp() <= 0)
	    		{
	    			this.taskActionListener.castDamaging(p);
	    			this.getMap()[x][y] = new EmptyCell();
	    		}
	    	}
	    	else if (cl instanceof ChampionCell){
	    		ChampionCell c = (ChampionCell) cl;
	    		Wizard w = (Wizard) c.getChamp();
	    		w.setHp(w.getHp() - s.getDamageAmount());
	    		if(!isAlive(c.getChamp()))
	    		{
	    			this.taskActionListener.removeChamp(w, "Dead");
	    			this.getMap()[x][y] = new EmptyCell();
	    			removeWizard(c.getChamp());
	    		}
	        
	    	}
	    	useSpell(s);
	    	finalizeAction();
	    }
	@Override
	public void castHealingSpell(HealingSpell s) throws IOException, NotEnoughIPException, InCooldownException, OutOfBordersException{
		super.castHealingSpell(s);
		taskActionListener.castHealing();
		finalizeAction();
	}
	 
	@Override
	public void castRelocatingSpell(RelocatingSpell s,Direction d,Direction t,int r) throws IOException, NotEnoughIPException, InCooldownException, OutOfRangeException, OutOfBordersException, InvalidTargetCellException
    {
	   super.castRelocatingSpell(s, d, t, r);
       this.taskActionListener.castRelocating(super.directionToPoint(d, super.getCurrentChamp()), super.getExactPosition(super.getTargetPoint(t), t, r-1));
	   finalizeAction();
	}
	@Override
	public void endTurn() throws IOException, OutOfBordersException
	{
	   if(super.getChampions().size() != 0){
	     super.endTurn();
	     this.taskActionListener.updateNEWPanels();
	   }
	   else
	   {
		 if(super.getListener() != null)
			 super.getListener().onFinishingSecondTask(this.winners);
	   }
	}
	@Override
    public void onSlytherinTrait(Direction d) throws IOException, OutOfBordersException, InvalidTargetCellException, InCooldownException{
	    //Check the trait cooldown
		Wizard w = (Wizard) this.getCurrentChamp();
	    if(w.getTraitCooldown() != 0)
	    	throw new InCooldownException(w.getTraitCooldown());
    	//Checks if target point inside map 
	    //Note: getExactPosition method could create point outside map and does not assign it to null
	    //not like getAdjacentCells method
	    Point champpoint  = w.getLocation();
    	Point firstpoint  = super.getExactPosition(w.getLocation(), d, 1);
    	Point secondpoint = super.getExactPosition(w.getLocation(), d, 2);
    	if (!( super.insideBoundary(firstpoint) || super.insideBoundary(secondpoint) ))
    		throw new OutOfBordersException();
    	
    	int cpx = (int) champpoint.getX();
    	int cpy = (int) champpoint.getY();
    	int fpx = (int) firstpoint.getX();
    	int fpy = (int) firstpoint.getY();
    	int spx = (int) secondpoint.getX();
    	int spy = (int) secondpoint.getY();
    	Cell cellofchamp  = this.getMap()[cpx][cpy];
    	Cell firstcell    = this.getMap()[fpx][fpy];
    	Cell secondcell   = this.getMap()[spx][spy];
    	
    	if(! (secondcell instanceof EmptyCell || secondcell instanceof CollectibleCell) )
    		throw new InvalidTargetCellException();
    	
    	if (secondcell instanceof EmptyCell ){
    					
    		w.setLocation(secondpoint);
    		this.getMap()[spx][spy] = new ChampionCell(super.getCurrentChamp());
    		this.getMap()[cpx][cpy] = new EmptyCell();
    	}
    	else if(secondcell instanceof CollectibleCell)
    	{
    		w.setLocation(secondpoint);
    		w.getInventory().add(((CollectibleCell) secondcell).getCollectible());
    		this.taskActionListener.showCollectible();
    		this.getMap()[spx][spy] = new ChampionCell(super.getCurrentChamp());
    		this.getMap()[cpx][cpy] = new EmptyCell();
    	}
    	super.setTraitActivated(true);
		super.setAllowedMoves(0);
		w.setTraitCooldown(4);
		this.taskActionListener.moveSlytherin(d);
		finalizeAction();
    }
	
	public void addChampsTreasureLocation(int x, int y){
		Point p = new Point(x,y);
		this.treasuresLocation.add(p);
	}
	
	public Point getChampTreasureLocation(){
		Point p = null;
		for (int i = 0; i < this.treasuresLocation.size();i++){
			p = this.treasuresLocation.get(i);
			int x = (int) p.getX();
	    	int y = (int) p.getY();
			Cell t = this.getMap()[x][y];
			if(t instanceof TreasureCell)
			{
				TreasureCell n = (TreasureCell) this.getMap()[x][y];
				if (n.getOwner() == this.getCurrentChamp()){
					break;
				}
			}
		}
		return p;
	}
	
	@Override
	public void onHufflepuffTrait() throws InCooldownException
	{
	   Wizard c = (Wizard) super.getCurrentChamp();
	   super.onHufflepuffTrait();
	   c.setTraitCooldown(6);
	}
	
	public Object onRavenclawTrait() throws InCooldownException{
		ArrayList<Direction> hint = new ArrayList<Direction>();
		Wizard w                  = (Wizard) this.getCurrentChamp();
		Point treasurelocation    = this.getChampTreasureLocation();
		Point champ               = w.getLocation(); 
	    int champx                = (int) champ.getX(); 
	    int champy                = (int) champ.getY(); 
	    int tx                    = (int) treasurelocation.getX(); 
	    int ty                    = (int) treasurelocation.getY(); 
	    if (w.getTraitCooldown() > 0){
    		throw new InCooldownException(w.getTraitCooldown());
    	}
	    if(champy > ty) 
	      hint.add(Direction.LEFT); 
	    else if(ty > champy) 
	      hint.add(Direction.RIGHT); 
	    if(champx > tx) 
	      hint.add(Direction.FORWARD); 
	    else if(tx > champx) 
	      hint.add(Direction.BACKWARD);
	    
	    super.setTraitActivated(true);
	    w.setTraitCooldown(7);
	    this.taskActionListener.showHint(hint);
	    return hint;
	}
	   public void usePotion(Potion p) throws OutOfBordersException, IOException
	    {
	    	Wizard w = (Wizard)super.getCurrentChamp();
	    	int j = 0 ;
	    	for(int i = 0 ; i < w.getInventory().size() ; i++)
	    	{
	    		if(w.getInventory().get(i).equals(p))
	    			j = i;
	    	}
	    	super.usePotion(p);
	    	this.taskActionListener.updateAfterPotion(j);
	    }

}
