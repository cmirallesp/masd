import apapl.ExternalActionFailedException;
import com.google.gson.JsonObject;

/**
 * Created by salva on 28/05/17.
 */

class Product extends Logger {
    int id;
    String desc = "unknown";
    String image;
    int qty;

    int msrp = 10; // Manufacturer recommended retail price
    int price = 10; // Price on sale

    int quality = 5;
    int realQuality = 3;

    String seller = "unknown";
    String fullDescription = "Description is not available";

    // In order to do computations more efficiently in the front
    long lastModified = 0;

    Product(int _id) {
        this(_id,0);
    }

    Product(int _id, int _qty) {
        id = _id;
        qty = _qty;
        updateLastModified();
    }

    Product(int _id, String _desc, int _qty) {
        this(_id, _qty);
        setProductType(_desc);
    }


    String getType() {
        return desc;
    }

    void setProductType(String _desc) {
        desc = _desc;
        // Assign an image
        image = Env.getImage(_desc);
        updateLastModified();
    }

    public int getMsrp() {
		return msrp;
	}

	public void setMsrp(int msrp) {
		this.msrp = msrp;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	int getId() {
        return id;
    }

    /**
     * Updates the lastModified field to current timestamp
     */
    void updateLastModified() {
        lastModified = System.currentTimeMillis();
    }

    /**
     * Updates the quantity on sale
     * @param diff differential amount
     */
    void updateQuantity(int diff) throws ExternalActionFailedException {
        if (qty + diff < 0) {
            throw new ExternalActionFailedException("Cannot put less than 0 units on sale");
        }
        qty+=diff;
        updateLastModified();
    }

    JsonObject toJSON() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", id);

        obj.addProperty("name", desc);
        obj.addProperty("description", fullDescription);
        obj.addProperty("quantity", qty);
        obj.addProperty("seller", seller);

        obj.addProperty("quality", quality);
        obj.addProperty("real_quality", realQuality);
        obj.addProperty("price", price);
        obj.addProperty("msrp", msrp);

        // Send the image URL
        obj.addProperty("image", image);

        // When it was last modified
        obj.addProperty("last_modified", lastModified);

        // Add the log
        obj.add("log", this.getJSONLog());

        return  obj;
    }
}