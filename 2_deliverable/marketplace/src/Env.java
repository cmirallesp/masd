import apapl.Environment;
import apapl.ExternalActionFailedException;
import apapl.data.APLFunction;
import apapl.data.APLIdent;
import apapl.data.APLNum;
import apapl.data.Term;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import apapl.data.APLList;
import java.util.Random;
/**
 * === About this file
 * This is an example of a very simple environment that communicates with a single 2APl agent.
 * As you can see, this class extends the Environment class. This will give us several
 * methods that we can use to communicate with the agents, and we can create methods that the
 * agents can use to perform external actions.
 * 
 * Below you will find all the basic functionality that an environment offers explained.
 * 
 * === How to get this example running
 * First of all, I strongly recommend to use Eclipse as your editor since it has been used to
 * develop 2APL as well. The use of the Eclipse plugin that comes with the full package of 2APL
 * is not recommended, since it is not completely bug-free. In what follows I will assume you are
 * wise and have chosen for Eclipse.
 * 
 * 1. Create a new java project in Eclipse and add this file.
 * 2. Add 2apl.jar to the build path by adding it as an external jar.
 * 3. Before this environment can be used, it needs to be compiled into a JAR-file first.
 *    In the folder of this example you will also find a pre-compiled version of this JAR 
 *    already (env.jar), but it is fairly easy to do it yourself. You need to create a runnable jar 
 *    from this project, with this class (Env.java) as its main class. Therefore this class is REQUIRED 
 *    to have a main method. 
 * 4. Once you have created this JAR, you can refer to it in the .mas file that specifies the
 *    components of the multiagent system. The mas file of this example is called config.mas. This file 
 *    defines what environment to use, and what agents will exist. Agents are specified as .2apl files. 
 *    In our example we have one agent that is built from agent.2apl.
 * 5. Put all the files (the JAR, config.mas and agent.2apl) in one directory and run 2APL. You can
 *    run 2APL simply by running 2apl.jar. If you want debug information as well, you need to run
 *    this jar from Eclipse using APAPL as its main class.
 * 6. Open the .mas file, press 'play' and the example should be working.
 * 
 * @author Marc van Zee (marcvanzee@gmail.com), Utrecht University
 *
 */
public class Env extends Environment {
	private class Product{
		int id;
		String desc;
		int qty;
		
		Product(int _id,String _desc,int _qty){
			this.id = _id;
			this.desc = _desc;
			this.qty = _qty;
		}
	}

	private HttpServer server = null;
	private final boolean log = true;
	private final Random qualGenerator = new Random();
	private Hashtable<Integer, Product> products = new Hashtable<Integer, Product>();

    /**
     * We do not use this method, but we need it so that the JAR file that we will create can point
     * to this class as the main class. This is only possible if the class contains  main method.
     * @param args arguments
     */
	public static void main(String [] args) {

		// Just to be able to try it out
		Env env = new Env();
		try {
			env.runServer();
		} catch (IOException e) {
			//Not working?!
			e.printStackTrace();
		}

	}
	
	/**
	 * This method is automatically called whenever an agent enters the MAS.
	 * @param agName the name of the agent that just registered
	 */
	protected void addAgent(String agName) {
		log("env> agent " + agName + " has registered to the environment.");
		
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
		throwEvent(event, agName);
		
		// note: we can also throw an event to all agents by letting out the last parameter: 
		// throwEvent(event);

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
	
	/**
	 * This method can be called by a 2APL agents as follows: @env(putOnSale(id,"my product",2), X).
	 * X will be [id,qty]
	 * @param agName The name of the agent that does the external action
	 * @param idProd product identifier
	 * @param desc product description
	 * @param qty quantity
	 * 
	 * @return The idProd and qty in a APLList
	 */
	public Term putOnSale(String agName, APLNum idProd, APLIdent desc, APLNum qty) throws ExternalActionFailedException {
		log(String.format("env> agent %s putOnSale(%s,%s,%s)", agName, idProd, desc,qty));
		
		
		Product product = (Product) products.get(idProd.toInt());
		if (product==null){
			product = new Product(idProd.toInt(),desc.toString(),qty.toInt());
			products.put(idProd.toInt(), product);
		}
		else{
			product.qty += qty.toInt();
		}

		try {
			return new APLList(idProd,new APLNum(product.qty));
		} 
		catch (Exception e) {
			//exception handling
			System.err.println(String.format(
					"env> %s putOnSale(%s,..) failed: %s ", agName, idProd,e.getMessage()));
			return null;
		}
	}
	
	/**
	 * This method can be called by a 2APL agents as follows: @env(retireFromSale(id,"my product",2), X).
	 * X will be [id,qty]
	 * @param agName The name of the agent that does the external action
	 * @param idProd product identifier
	 * @param qty quantity to be retired
	 * 
	 * @return The id and qty in a APLList
	 */
	public Term retireFromSale(String agName, APLNum idProd,  APLNum qty) throws ExternalActionFailedException {
		log(String.format("env> agent %s retireFromSale(%s,%s)", agName, idProd, qty));

		Product product = (Product) products.get(idProd.toInt());
		if (product==null){
			log("throwing exception");
			throw new ExternalActionFailedException(
					String.format("Product %s not in product list", idProd));
		}
		
		product.qty = Math.max(0, product.qty - qty.toInt());
		
		try {
			return new APLList(idProd,new APLNum(product.qty));
		} 
		catch (Exception e) {
			//exception handling
			System.err.println(String.format(
					"env> %s retireFromSale(%s,..) failed: %s ", agName, idProd,e.getMessage()));
			return null;
		}
	}
	
	//exact search for the moment (fuzzy?)
	public Term searchProduct(String agName, APLIdent prodDesc) throws ExternalActionFailedException {
		log(String.format("env> agent %s searchProduct(%s)", agName, prodDesc));
		Product product = null;
		boolean found = false;
		for(Object o: products.values()){
			product = ((Product) o);
			log(String.format("DB:%s param:%s", product.desc, prodDesc));
			if (product.desc.equals(prodDesc.toString())){
				found = true;
				break;
			}
		}
		
		if(found){
			return new APLList(
					new APLNum(product.id),
					new APLIdent(product.desc),
					new APLNum(product.qty));
		}
		else{
			return new APLList();
		}
	}
	
	public Term produceProduct(String agName, APLIdent prodId, APLNum QualityCla){
		int rnd = this.qualGenerator.nextInt(5);
		return new APLList(
				prodId,
				new APLNum(rnd)
				);

	}

	private void log(String str) {
		if (log) System.out.println(str);
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

	public void runServer() throws IOException {
		int port = 9000;
		server = HttpServer.create(new InetSocketAddress(port), 0);
		System.out.println("server started at " + port);

		server.createContext("/", new HttpHandler() {
			@Override
			   public void handle(HttpExchange he) throws IOException {
	                String response = "<h1>Server start success if you see this message</h1>" + "<h1>Yaaay!</h1>";
	                he.sendResponseHeaders(200, response.length());
	                OutputStream os = he.getResponseBody();
	                os.write(response.getBytes());
	                os.close();
	        }
		});

		server.createContext("/echoGet", new HttpGetHandler() {

	         @Override
	         public void handle(HttpExchange he) throws IOException {

	                 // parse request
	                 HashMap<String,Object> parameters = new HashMap<String, Object>();
	                 URI requestedUri = he.getRequestURI();
	                 String query = requestedUri.getRawQuery();
	                 parseQuery(query, parameters);

	                 // send response
	                 String response = "Get request recived to "+requestedUri.toString()+" with parameters:\n";
	                 for (String key : parameters.keySet())
	                          response += key + " = " + parameters.get(key) + "\n";
	                 he.sendResponseHeaders(200, response.length());
	                 OutputStream os = he.getResponseBody();
	                 os.write(response.toString().getBytes());

	                 os.close();
	         }
		});

		server.setExecutor(null);
		server.start();
	}
}