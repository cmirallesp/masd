import com.google.gson.JsonObject;

/**
 * Created by salva on 28/05/17.
 */
class Agent extends Logger {
    String name;


    public Agent(String _name) {
        name = _name;
    }

    String getName() {
        return name;
    }

    JsonObject toJSON() {
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.add("log", this.getJSONLog());
        return object;
    }

}