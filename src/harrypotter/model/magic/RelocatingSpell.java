package harrypotter.model.magic;

public class RelocatingSpell extends Spell{
	private int range;
	
	public RelocatingSpell(String name, int cost, int defaultCoolDown, int range){
		super(name, cost, defaultCoolDown);
		this.range = range;
	}

	public int getRange() {
		return range;
	}
	
	@Override
	public String toString()
	{
		return "Name :" + getName() + "\n" + "Type :RelocatingSpell" + "\n" + "Cost :" + getCost() +
				"\n" + "Range :" + this.range + "\n" + "Cooldown :"+ getCoolDown() + "\n" + "DefaultCooldown :"+
				 getDefaultCooldown();
	}	

}
