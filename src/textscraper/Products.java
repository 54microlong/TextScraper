package textscraper;

/**
 * @author chenlong
 *
 */
public class Products {
	
	private String name;
	private String price;
	private String vendor;
	
	public Products(String _name, String _price, String _vendor)
	{
		this.vendor = _vendor;
		this.name = _name;
		this.price = _price;
	}
	
	public void PrintProducts()
	{
		System.out.println("Name: " + this.name);
		if("" == this.price){
			System.out.println("Price : Not Display!");
		}
		else{
			System.out.println("Price: " + this.price);
		}
		System.out.println("Vendor: " + this.vendor);
	}

}
