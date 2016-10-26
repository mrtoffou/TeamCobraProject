public abstract class Character implements Properties {
	
	protected int id;
	protected int hp;
	protected int atk;
	protected int spd;
	protected int def;
	protected String name;
	protected String description;
	protected boolean dead;
	
	public int getID() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getAtk() {
		return atk;
	}

	public void setAtk(int atk) {
		this.atk = atk;
	}
	
	public void addAtk(int atk){
		this.atk = this.atk + atk;
	}

	public int getSpd() {
		return spd;
	}

	public void setSpd(int spd) {
		this.spd = spd;
	}

	public boolean isDead() {
		return dead;
	}

	public boolean setDead(boolean dead) {
		return this.dead = dead;
	}
	
	public boolean getDead(){
		return this.dead;
	}

	public int getDef() {
		return def;
	}

	public void setDef(int def) {
		this.def = def;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
	
}