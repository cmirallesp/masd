import com.google.gson.JsonArray;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by salva on 28/05/17.
 */
class Logger {

    List<EventLog> log = new LinkedList<>();

    public void addLog(EventLog _log) {
        log.add(_log);
    }

    public void addLog(String _message) {
        log.add(new EventLog(_message));
    }


    public JsonArray getJSONLog() {
        return getJSONLog(0);
    }

    public JsonArray getJSONLog(long from) {
        JsonArray array = new JsonArray();
        for (EventLog event: log) {
            if (event.timestamp >= from) {
                array.add(event.toJSON());
            }
        }
        return array;
    }

    public void clear() {
        log.clear();
    }
}