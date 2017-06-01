
public class ProductTypePrices {
	
	private final static double C = 50.0;
	private final static double lambda = 1.0;
	
	private int productionPrice;
	private int x;
	
	public ProductTypePrices(int productionPrice, int x) {
		super();
		this.productionPrice = productionPrice;
		this.x = x;
	}

	public int getProductionPrice() {
		return productionPrice;
	}

	public int getX() {
		return x;
	}
	
	public int getRecommendedPrice(int quality) {
		return (int)Math.round(x*(5+quality)/10.0);
	}
	
	public int getSellingPrice(int quality, int stock) {
		return getRecommendedPrice(quality) + (int)Math.round(C*Math.round(-lambda*stock));
	}
	
}
