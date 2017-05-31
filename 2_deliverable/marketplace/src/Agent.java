import com.google.gson.JsonObject;

/**
 * Created by salva on 28/05/17.
 */
class Agent extends Logger {
    String name;
    int money;


    public Agent(String _name) {
        name = _name;
    }

    String getName() {
        return name;
    }
    
    int getMoney() {
    	return money;
    }

    public void setName(String name) {
		this.name = name;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	JsonObject toJSON() {
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.addProperty("money", money);
        object.add("log", this.getJSONLog());
        return object;
    }

}