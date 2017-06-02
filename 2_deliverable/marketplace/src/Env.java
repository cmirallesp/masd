import apapl.APLMAS;
import apapl.Environment;
import apapl.ExternalActionFailedException;
import apapl.data.*;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.gson.JsonArray;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * === About this file
 * This is an example of a very simple environment that communicates with a single 2APl agent.
 * As you can see, this class extends the Environment class. This will give us several
 * methods that we can use to communicate with the agents, and we can create methods that the
 * agents can use to perform external actions.
 * <p>
 * Below you will find all the basic functionality that an environment offers explained.
 * <p>
 * === How to get this example running
 * First of all, I strongly recommend to use Eclipse as your editor since it has been used to
 * develop 2APL as well. The use of the Eclipse plugin that comes with the full package of 2APL
 * is not recommended, since it is not completely bug-free. In what follows I will assume you are
 * wise and have chosen for Eclipse.
 * <p>
 * 1. Create a new java project in Eclipse and add this file.
 * 2. Add 2apl.jar to the build path by adding it as an external jar.
 * 3. Before this environment can be used, it needs to be compiled into a JAR-file first.
 * In the folder of this example you will also find a pre-compiled version of this JAR
 * already (env.jar), but it is fairly easy to do it yourself. You need to create a runnable jar
 * from this project, with this class (Env.java) as its main class. Therefore this class is REQUIRED
 * to have a main method.
 * 4. Once you have created this JAR, you can refer to it in the .mas file that specifies the
 * components of the multiagent system. The mas file of this example is called config.mas. This file
 * defines what environment to use, and what agents will exist. Agents are specified as .2apl files.
 * In our example we have one agent that is built from agent.2apl.
 * 5. Put all the files (the JAR, config.mas and agent.2apl) in one directory and run 2APL. You can
 * run 2APL simply by running 2apl.jar. If you want debug information as well, you need to run
 * this jar from Eclipse using APAPL as its main class.
 * 6. Open the .mas file, press 'play' and the example should be working.
 *
 * @author Marc van Zee (marcvanzee@gmail.com), Utrecht University
 */



public class Env extends Environment {

    private HttpServer server = null;
    private final Random rng = new Random();

    // All loggers
    private Map<String, Integer> productionPrices = new HashMap<>();
    {
    	productionPrices.put("smartphone1", 70);
    	productionPrices.put("smartphone2", 50);
    	productionPrices.put("smartphone3", 60);
    	productionPrices.put("smartwatch", 20);
    	productionPrices.put("tablet", 70);
    	productionPrices.put("console1", 50);
    	productionPrices.put("console2", 70);
    	productionPrices.put("console3", 50);
    	productionPrices.put("console4", 60);
        
        productionPrices.put("manual_1", 10);
        productionPrices.put("manual_2", 5);
        
        productionPrices.put("forehead_thermometer_1", 15);
        productionPrices.put("forehead_thermometer_2", 20);
        productionPrices.put("forehead_thermometer_3", 25);
        
        productionPrices.put("humidifier_1", 30);
        productionPrices.put("humidifier_2", 35);
        
        productionPrices.put("camera_1", 20);
        productionPrices.put("camera_2", 40);
        productionPrices.put("camera_3", 60);
        
    }
    private Map<Integer, Product> products = new HashMap<>();
    private Map<String, Agent> agents = new HashMap<>();
    private Logger logger = new Logger();
    private int nextId = 0; // products must have unique ids. Maintain a counter

    private static String BASE_PATH;
    private static List<String> RANDOM_IMAGES = new LinkedList<>();
    private static Map<String, String> IMAGES = new HashMap<>();

    static {
        String base = Env.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File baseFile = new File(base);
        if (baseFile.isFile()) {
            // I need the parent directory
            BASE_PATH = baseFile.getParent();
        } else {
            BASE_PATH = baseFile.getPath();
        }

        System.out.println("Base Path: " + BASE_PATH);

        // Read the images
        File imagesFolder = new File(BASE_PATH+"/marketplace/images/products");
        if (imagesFolder.isDirectory()) {
            for (File image : imagesFolder.listFiles()) {
                String name = image.getName();
                name = name.substring(0, name.lastIndexOf('.'));
                if (name.length() >= 7 && name.substring(8).equals("random_")) {
                    RANDOM_IMAGES.add("/marketplace/images/products/"+image.getName());
                } else {
                    IMAGES.put(name, "/marketplace/images/products/"+image.getName());
                }
            }
        }

    }

    static String getImage(String desc) {
        if (IMAGES.containsKey(desc)) {
            return IMAGES.get(desc);
        } else if (RANDOM_IMAGES.isEmpty()) {
            return "http://placehold.it/320x150";
        } else {
            String assigned = RANDOM_IMAGES.remove(0);
            IMAGES.put(desc, assigned);
            return assigned;
        }
    }

    /**
     * We do not use this method, but we need it so that the JAR file that we will create can point
     * to this class as the main class. This is only possible if the class contains  main method.
     *
     * @param args arguments
     */
    public static void main(String[] args) {

        // Just to be able to try it out
        Env env = new Env();
        try {
            env.runServer();
        } catch (IOException e) {
            //Not working?!
            e.printStackTrace();
        }

    }

    public JsonArray getAgents() {
        JsonArray arr = new JsonArray();
        for (Agent agent : agents.values()) {
            arr.add(agent.toJSON());
        }
        return arr;
    }

    public JsonArray getOnSale() {
        JsonArray arr = new JsonArray();
        for (Product product : products.values()) {
        	if (product.isOnSale()) {
        		arr.add(product.toJSON());
        	}
        }
        return arr;
    }

    private void addLog(String message, String agentName, int productId) {
        Agent agent = agents.get(agentName);
        Product product = this.products.get(productId);
        this.addLog(message, agent, product);
    }

//    private Agent getAgent(String name) {
//        if (agents.containsKey(name)) {
//            return agents.get(name);
//        } else {
//            Agent agent = new Agent(name);
//            agents.put(name, agent);
//            return agent;
//        }
//    }

    private void addLog(String message, Agent agent, Product product) {
        EventLog event = new EventLog(message, agent, product);
        this.logger.addLog(event);
        if (agent != null) agent.addLog(event);
        if (product != null) product.addLog(event);
    }
    
    synchronized private int getUniqueId() {
    	int id = nextId;
    	nextId += 1;
    	return id;
    }

    /**
     * This method is automatically called whenever an agent enters the MAS.
     *
     * @param agName the name of the agent that just registered
     */
    protected void addAgent(String agName) {

		/* If we want to send information to a 2APL agent, we need to code this into special
		 * objects. We can then send these objects to the agent so that he can parse them correctly.
		 * All the objects extend the basic class "Term".
		 *
		 * We distinguish between the following objects:
		
		 * APLNum			This is equal to int and is for example instantiated by: new APLNum(0)
		 * APLIdent			Equal to String, instantiated by: new APLIdent("string")
		 * APLList			Can be seen as a LinkedList and will be parsed as a Prolog list in 2APL
		 *					See the constructor comments of this class for information on how to use it
		 * APLFunction		Represents a function, where the arguments of the function again need to be
		 *					Term objects. For example, the function: func(0) should be instantiated as
		 *					new APLFunction("func", new APLNum(0))
		 */
        APLIdent aplagName = new APLIdent(agName);
        APLFunction event = new APLFunction("name", aplagName);

        // If we throw an event, we always need to throw an APLFunction.
        notifyAgents(event, agName);

//        addLog("Agent registered to environment", agents.get(agName), null);

        // note: we can also throw an event to all agents by letting out the last parameter:
        // notifyAgents(event);

        // If the environment was not running, make it run
        if (this.server == null) {
            try {
                this.runServer();
            } catch (IOException e) {
                // The port is occupied, skip
                e.printStackTrace();
            }
        }
    }

//    private Product updateOnSale(String agName, int idProd, int qty)  throws ExternalActionFailedException {
//        Product product = products.get(idProd);
//        if (product == null) {
//            if (qty < 0) {
//                throw new ExternalActionFailedException(
//                        String.format("Product %s not in product list", idProd));
//            }
//            product = new Product(idProd, qty);
//            product.owner = agName;
//            products.put(idProd, product);
//        } else {
//            product.updateQuantity(qty);
//        }
//        return product;
//    }

    /**
     * This method can be called by a 2APL agents as follows: @env(putOnSale(id,"my product",2), X).
     * X will be [id,qty]
     *
     * @param agName The name of the agent that does the external action
     * @param idProd product identifier
     * @param price  Sale price decided by the agent.
     * @return The idProd and qty in a APLList
     */
    public Term putOnSale(String agName, APLNum idProd, APLNum announcedQuality, APLNum price) throws ExternalActionFailedException {
    	System.out.println("Putting on sale " + idProd);

    	if (!products.containsKey(idProd.toInt())) {
    		String msg = "There is not any product with id " + idProd;
    		this.addLog(msg, agents.get(agName), null);
    		throw new ExternalActionFailedException(msg);
    	}
    	
    	Product product = products.get(idProd.toInt());
    	
    	if (!product.getOwner().equals(agName)) {
    		String msg = "Product " + idProd + " belongs to agent " + product.getOwner();
    		this.addLog(msg, agents.get(agName), product);
    		throw new ExternalActionFailedException(msg);
    	}
    	
    	product.setAnnouncedQuality(announcedQuality.toInt());
    	product.setOnSale(true);
    	product.setPrice(price.toInt());
    	
    	this.addLog("Product " + idProd + " is now for sale", agName, product.getId());
    	return null;
    }

    /**
     * This method can be called by a 2APL agents as follows: @env(retireFromSale(id,"my product",2), X).
     * X will be [id,qty]
     *
     * @param agName The name of the agent that does the external action
     * @param idProd product identifier
     * @return The id and qty in a APLList
     */
    public Term retireFromSale(String agName, APLNum idProd) throws ExternalActionFailedException {
    	if (!products.containsKey(idProd.toInt())) {
    		String msg = "There is not any product with id " + idProd;
    		this.addLog(msg, agents.get(agName), null);
    		throw new ExternalActionFailedException(msg);
    	}
    	
    	Product product = products.get(idProd.toInt());
    	
    	if (!product.getOwner().equals(agName)) {
    		String msg = "Product " + idProd + " does not belong to agent " + agName;
    		this.addLog(msg, agents.get(agName), product);
    		throw new ExternalActionFailedException(msg);
    	}
    	
    	if (!product.isOnSale()) {
    		String msg = "Product is " + idProd + " is not for sale";
    		this.addLog(msg, agents.get(agName), product);
    		throw new ExternalActionFailedException(msg);
    	}
    	
    	product.setOnSale(false);
    	
    	this.addLog("Product " + idProd + " is no longer for sale", agName, product.getId());
    	return null;
    }

    //exact search for the moment (fuzzy?)
    public Term searchProduct(String agName, APLIdent prodDesc) throws ExternalActionFailedException {
    	Agent agent = agents.get(agName);
    	String allowedSellerRole = agent.getRole().equals("store")? "producer" : "store";
        addLog(String.format("Search Product %s",prodDesc.toString()), agent, null);
        LinkedList<Term> foundProducts = new LinkedList<>();
        for (Product product : products.values()) {
        	Agent seller = agents.get(product.getOwner());
        	if (product.isOnSale() && product.getType().equals(prodDesc.toString()) && 
        			seller.getRole().equals(allowedSellerRole)) {
        		foundProducts.add(new APLList(new APLNum(product.getId()),
        									  new APLIdent(product.getType()),
        									  new APLNum(product.getAnnouncedQuality()),
        									  new APLNum(product.getPrice()),
        									  new APLIdent(product.getOwner())));
        	}
        }
        // list of lists. Each inner list has the form [ProductId, Type, AnnouncedQuality, Price, Seller]
        return new APLList(foundProducts);
    }

    public Term produceProduct(String agName, APLIdent prodType) throws ExternalActionFailedException {
    	if (productionPrices.containsKey(prodType.toString())) {
    		Integer price = productionPrices.get(prodType.toString());
    		Agent agent = agents.get(agName);
    		if (price != null && agent.getMoney() >= price) {
    			int id = this.getUniqueId();
    			int quality = this.rng.nextInt(11);
    			Product product = new Product(id, prodType.toString(), 1);
    			product.setRealQuality(quality);
    			product.setOwner(agName);
    			products.put(id, product);
                // Even if the agent updates its belief after executing this action, fire an UpdateMoney
                // event just in case
                updateMoney(agName, agent.getMoney() - price);
                return new APLList(new APLNum(id), prodType, new APLNum(quality));
    		} else {
    			String msg = "Agent " + agName + " has not enough money to produce " + prodType;
    			this.addLog(msg, agent, null);
    			throw new ExternalActionFailedException(msg);
    		}
    		
    	} else {
    		String msg = "Unknown product type: " + prodType;
    		this.addLog(msg, null, null);
    		throw new ExternalActionFailedException(msg);
    	}
    	
    }
    
    public Term shipMoney(String agName, APLIdent dst, APLNum concept, APLNum money) throws ExternalActionFailedException {
    	Agent agent = agents.get(agName);
    	Agent receiver = agents.get(dst.toString());
    	if (agent.getMoney() < money.toInt()) {
    		throw new ExternalActionFailedException("Agent has not enough money to transfer");
    	}
    	agent.setMoney(agent.getMoney() - money.toInt());
    	receiver.setMoney(receiver.getMoney() + money.toInt());
    	APLFunction event = new APLFunction("moneyTransfer", money, concept);
    	notifyAgents(event, receiver.getName());
        addLog(String.format("Ship money $%d (%s -> %s)", money.toInt(), agName, dst.toString()), agents.get(agName), null);

        return null;
    }
    
    public Term shipProduct(String agName, APLIdent dst, APLNum product) throws ExternalActionFailedException {
    	Product prod = products.get(product.toInt());
    	if (!prod.getOwner().equals(agName)) {
    		throw new ExternalActionFailedException("Agent does not have product");
    	}
    	prod.setOwner(dst.toString());
    	prod.setOnSale(false);
    	int probabilityRealizing = Math.abs(prod.getAnnouncedQuality() - prod.getRealQuality())*10;
    	boolean bluffUncovered = rng.nextInt(100) < probabilityRealizing;
    	int notifiedQuality = bluffUncovered? prod.getRealQuality() : prod.getAnnouncedQuality();
    	APLFunction event = new APLFunction("productTransfer", product, new APLIdent(prod.getType()), new APLNum(notifiedQuality));
    	notifyAgents(event, dst.toString());

        addLog(String.format("Ship product %d (%s -> %s)", product.toInt(), agName, dst.toString()), agName, product.toInt());
    	return null;
    }
    
    public Term enterMarket(String agName, APLIdent role) throws ExternalActionFailedException {
    	Agent agent = new Agent(agName, role.toString());
    	int startMoney = 0;
    	switch (role.toString()) {
    	case "producer":
    		startMoney = 5000;
    		break;
    	case "store":
    		startMoney = 5000;
    		break;
    	case "enduser":
    		startMoney = 1000;
    		break;
    	default:
    		throw new ExternalActionFailedException("Unknown role: " + role.toString());
    	}
    	agents.put(agName, agent);
    	updateMoney(agName, startMoney);
    	return null;
    }

    public void updateNeeds(String agName, String productType, int minQuality) {
        APLFunction event = new APLFunction("updateNeeds", new APLIdent(productType), new APLNum(minQuality));
        // If we throw an event, we always need to throw an APLFunction.
        notifyAgents(event, agName);
        addLog("Update needs "+productType, agents.get(agName), null);
    }

    public void updateNeeds(String agName, String productType) {
        this.updateNeeds(agName, productType, 0);
    }

    public void updateItems(String agName, int id, String type, int quality) {
        APLFunction event = new APLFunction("updateItems", new APLNum(id), new APLIdent(type), new APLNum(quality));
        // If we throw an event, we always need to throw an APLFunction.
        notifyAgents(event, agName);
        addLog(String.format("Update products %s (%d) [q=%d]",type,id, quality), agents.get(agName), null);

    }

    public void updateMoney(String agName, int amount) {
        APLFunction event = new APLFunction("updateMoney", new APLNum(amount));
        // If we throw an event, we always need to throw an APLFunction.
        agents.get(agName).setMoney(amount);
        this.notifyAgents(event, agName);
        addLog(String.format("Update money $%d", amount), agents.get(agName), null);
    }
    

    private static class HttpGetHandler implements HttpHandler {

        public static void parseQuery(String query, HashMap<String, Object> parameters) throws UnsupportedEncodingException {

            if (query != null) {
                String pairs[] = query.split("[&]");
                for (String pair : pairs) {
                    String param[] = pair.split("[=]");
                    String key = null;
                    String value = null;
                    if (param.length > 0) {
                        key = URLDecoder.decode(param[0],
                                System.getProperty("file.encoding"));
                    }

                    if (param.length > 1) {
                        value = URLDecoder.decode(param[1],
                                System.getProperty("file.encoding"));
                    }

                    if (parameters.containsKey(key)) {
                        Object obj = parameters.get(key);
                        if (obj instanceof List<?>) {
                            @SuppressWarnings("unchecked")
                            List<String> values = (List<String>) obj;
                            values.add(value);

                        } else if (obj instanceof String) {
                            List<String> values = new ArrayList<String>();
                            values.add((String) obj);
                            values.add(value);
                            parameters.put(key, values);
                        }
                    } else {
                        parameters.put(key, value);
                    }
                }
            }
        }

        @Override
        public void handle(HttpExchange arg0) throws IOException {

        }
    }

    public static void openBrowser(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public static String getMimeType(String filename) {
        switch (filename.substring(filename.length() - 4)) {
            case "html":
                return "text/html";
            case "t.js": // We just have one script
                return "application/javascript";
            case "jpeg":
            case ".jpg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            default:
                return "text/plain";
        }
    }

//    private void addPlaceholder() {
//        Agent agent1 = getAgent("seller1");
//        Agent agent2 = getAgent("seller2");
//        Agent agent3 = getAgent("seller3");
//        try {
//            putOnSale(agent1.name, new APLNum(100001), new APLIdent("prod1"), new APLNum(1));
//            putOnSale(agent1.name, new APLNum(100002), new APLIdent("prod2"), new APLNum(1));
//            putOnSale(agent2.name, new APLNum(100003), new APLIdent("prod1"), new APLNum(1));
//            putOnSale(agent3.name, new APLNum(100004), new APLIdent("prod1"), new APLNum(1));
//            putOnSale(agent3.name, new APLNum(100005), new APLIdent("prod3"), new APLNum(1));
//            putOnSale(agent3.name, new APLNum(100006), new APLIdent("prod4"), new APLNum(3));
//        } catch (ExternalActionFailedException e) {
//            e.printStackTrace();
//        }
//    }

    public void runServer() throws IOException {

        int port = 9000;
        server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("server started at " + port);

        // Serves the static files
        server.createContext("/marketplace/", new HttpHandler() {
            @Override
            public void handle(HttpExchange he) throws IOException {

                String file = he.getRequestURI().getPath();
                System.out.println("File Requested: " + file);
                if (file.length() <= 13) { // It is just "/marketplace/"
                    file = "/marketplace/index.html";
                }

                he.getResponseHeaders().add("Content-Type", Env.getMimeType(file));

                if (he.getRequestMethod().equals("HEAD")) {
                    he.close();
                } else {
                    FileInputStream fis;
                    try {
                        fis = new FileInputStream(Env.BASE_PATH + file);
                        he.sendResponseHeaders(200, 0);
                        OutputStream os = he.getResponseBody();

                        // Just copy the file
                        final byte[] buffer = new byte[0x10000];
                        int count;
                        while ((count = fis.read(buffer)) >= 0) {
                            os.write(buffer, 0, count);
                        }
                        fis.close();
                        os.close();
                    } catch (IOException e) {
                        String error = "404 (Not Found)\n";
                        he.sendResponseHeaders(404, error.length());
                        OutputStream os = he.getResponseBody();
                        os.write(error.getBytes());
                        os.close();
                    }
                }

            }
        });

        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange he) throws IOException {
                String response = "<h2>The HTTP Server is working</h2><h1>Yaaay!</h1>";
                he.sendResponseHeaders(200, response.length());
                OutputStream os = he.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        server.createContext("/api", new HttpGetHandler() {

            @Override
            public void handle(HttpExchange he) throws IOException {

                // Parse the request, will ignore by default
                int code = 200;
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                URI uri = he.getRequestURI();
                String query = uri.getRawQuery();
                parseQuery(query, parameters);

                String response;
                switch (uri.getPath().substring(4)) {
                    case "/events":
                        // Retrieves info from the events
                        long from = 0;
                        if (parameters.containsKey("from")) {
                            from = Long.parseLong(parameters.get("from").toString());
                        }
                        response = Env.this.logger.getJSONLog(from).toString();
                        if (parameters.containsKey("clear") &&
                                parameters.get("clear").equals("true")) {
                            Env.this.logger.clear();
                        }
                        break;
                    case "/agents":
                        // Retrieves info from the agents
                        response = Env.this.getAgents().toString();
                        break;
                    case "/update-money":
                        // Updates the money of an agent
                        if (parameters.containsKey("agent") && parameters.containsKey("amount")) {
                            // Everything is ok, send the event
                            Env.this.updateMoney(parameters.get("agent").toString(),
                                    Integer.parseInt(parameters.get("amount").toString()));
                        } else {
                            // Parameters are needed
                            code = 500;
                            response = "{\"error\":true, \"message\": \"Invalid request, 'agent' and 'amount' should be specified\"}";
                            break;
                        }
                    case "/update-necessity":
                        // Updates the money of an agent
                        if (parameters.containsKey("agent") && parameters.containsKey("type")) {
                            int minQuality = 0;
                            // Everything is ok, send the event
                            if (parameters.containsKey("quality")) {
                                minQuality = Integer.parseInt(parameters.get("quality").toString());
                            }
                            Env.this.updateNeeds(parameters.get("agent").toString(), parameters.get("type").toString(), minQuality);
                        } else {
                            // Parameters are needed
                            code = 500;
                            response = "{\"error\":true, \"message\": \"Invalid request, 'agent' and 'type' should be specified\"}";
                            break;
                        }
                    case "/update-item":
                        // Updates the money of an agent
                        if (parameters.containsKey("agent") &&
                                parameters.containsKey("id") &&
                                parameters.containsKey("type") &&
                                parameters.containsKey("quality") ) {
                            // Everything is ok, send the event
                            Env.this.updateItems(parameters.get("agent").toString(),
                                    Integer.parseInt(parameters.get("id").toString()),
                                    parameters.get("type").toString(),
                                    Integer.parseInt(parameters.get("quality").toString()));
                        } else {
                            // Parameters are needed
                            code = 500;
                            response = "{\"error\":true, \"message\": \"Invalid request, 'agent', 'id', 'quality' and 'type' should be specified\"}";
                            break;
                        }
                    case "/onsale":
                        response = Env.this.getOnSale().toString();
                        break;
                    default:
                        code = 500;
                        response = "{\"error\":true, \"message\": \"Invalid API, see documentation for more details\"}";
                        break;
                }

                // send response
                he.getResponseHeaders().add("Content-Type", "application/json");
                he.sendResponseHeaders(code, response.length());
                OutputStream os = he.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        server.setExecutor(null);
        server.start();

        try {
            Env.openBrowser(new URI("http://localhost:" + Integer.toString(port) + "/marketplace/"));
        } catch (Exception e) {
            System.err.print("Could not open a browser window");
        }
    }
}