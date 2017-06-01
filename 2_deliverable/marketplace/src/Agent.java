import com.google.gson.JsonObject;

/**
 * Created by salva on 28/05/17.
 */
class Agent extends Logger {
    String name;
    String role;
    int money;


    public Agent(String _name, String _role) {
        name = _name;
        role = _role;
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

	public String getRole() {
		return role;
	}

	JsonObject toJSON() {
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.addProperty("role", role);
        object.addProperty("money", money);
        object.add("log", this.getJSONLog());
        return object;
    }

}