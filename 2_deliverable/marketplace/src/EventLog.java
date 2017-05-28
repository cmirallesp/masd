import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

/**
 * Created by salva on 28/05/17.
 */
class EventLog {

    String message;
    Agent agent = null;
    Product product = null;
    long timestamp = 0;

    EventLog(String _message) {
        this(_message,null, null);
    }

    EventLog(String _message, Agent _agent, Product _product) {
        agent = _agent;
        product = _product;
        message = _message;
        timestamp = System.currentTimeMillis();
    }

    JsonObject toJSON() {
        JsonObject object = new JsonObject();
        object.addProperty("timestamp", timestamp);
        object.addProperty("message", message);

        if (agent == null) {
            object.add("agent", JsonNull.INSTANCE);
        } else {
            object.addProperty("agent", agent.getName());
        }

        if (product == null) {
            object.add("product", JsonNull.INSTANCE);
        } else {
            object.addProperty("product", product.getId());
        }
        return object;
    }
}