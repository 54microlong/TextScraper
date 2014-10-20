/**
 * 
 */
package textscraper;

/**
 * @author chenlong
 *
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextScraper {
	
	public void TextScraper(){
	}
	
//	public String urlEncode(String para)
//	{
//		try {
//			return URLEncoder.encode(para, "utf-8");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	
	/* @brief:   Given an URL, return the page content.
	 * @param:   String _url:  input URL which you want to access.
	 * @return:  String of the whole content from given URL
	 */
	public String urlRequest(String _url)
	{
		URL url;
		URLConnection urlConnection;
		BufferedReader reader;
		StringBuffer output;
			
		try {
			// Set up URL connection
			url = new URL(_url);
			urlConnection = url.openConnection();
				
			// Read content from server
			reader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			output = new StringBuffer();
			String line = "";
			while ((line = reader.readLine()) != null) {
				// Trim whitespace
				if ( !(line.trim().equals("")) ) {
					output.append(line + "\n");
				}
			}
			reader.close();	
			return output.toString().trim();	
			} catch (MalformedURLException e) {
				e.printStackTrace();
				System.err.println("MalformedURLException!");
				return "";
			} catch(IOException e){
				e.printStackTrace();
				System.err.println("IOException!");
				return "";
			}					
	}
	
	
	/* @brief:   Given an URL, The number N of wanted page and search query, it could get the Nth page of that URL
	 * @Strategy Since different query could produce different parameter and different redirect.
	 *           like{"digital", "digital camera"}. incorrect parameter will cause that the page is redirect the 
	 *           first page of that search. 
	 *           mining "next page" URL from the first page and just changing the page number could get right results.
	 *             
	 * @param:   String _url:      Input URL which you want to access.
	 * @param:   String _pageNum:  The number of Page.
	 * @param:   String _keyWord:  Search word, used to simplify the process of the URL.
	 * 
	 * @return:  String of the whole content from given URL
	 */
	public String getPageByPageNum(String _url, int _pageNum, String _keyWord)
	{
		String ultimateUrl = _url;
		String firstPageCont = urlRequest(ultimateUrl);
		
		Document _doc = Jsoup.parse(firstPageCont); 
		
		//Get the next page URL [id = srchPagination]
		Element _nextUrl = _doc.select("#srchPagination").select("a[href]").get(0);
		
		//Regular Expression used to mine URL of "next page".
		String _urlRegex = "<a href=\"([^\\\"]+)\">";
		Matcher _match = RegularMatch(_urlRegex, _nextUrl.toString());
		if(_match.find()) {
			try {
				ultimateUrl = "http://www.sears.com" + Jsoup.parse(_match.group(1), "UTF-8").text().replace(_keyWord, URLEncoder.encode(_keyWord, "utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}		    
		}
		
		//Change the page number parameter
		ultimateUrl = ultimateUrl.replace("pageNum=2", "pageNum=" +_pageNum);		
		return urlRequest(ultimateUrl);	
	}
	
	/* @brief:   Judge the Whether URL will redirect to other URL, and return new URL if changed.
	 * @Strategy Get the new URL from the JavaScript code of the page.
	 *             
	 * @param:   String _keyWord:  Search query.
	 * @return:  New URL if page redirect to others.
	 */
	public String urlRedirect(String _inputKeyWord)
	{
		String _ultimateUrl="";
		
		try {
			_ultimateUrl = "http://www.sears.com/search=" + URLEncoder.encode(_inputKeyWord, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String _PageCont = urlRequest(_ultimateUrl);
		
		//Regular Expression get "var url = "([^\"]*)\""
		String _urlRegex = "var url = \"\\\\([^\\\"]*)\\\"";
		Matcher _match = RegularMatch(_urlRegex, _PageCont);
		
		if(_match.find()) {
			try {
				_ultimateUrl = "http://www.sears.com" + Jsoup.parse(_match.group(1), "UTF-8").text().replace(_inputKeyWord, URLEncoder.encode(_inputKeyWord, "utf-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		    
		}
		
		return _ultimateUrl;
	}
	
	
	/* @brief:   Simple Regular Tools
	 *             
	 * @param:   String _regex:   the regular expression.
	 * @param:   String _content: string which the regular expression search from.
	 * @return:  result of match
	 */
	public Matcher RegularMatch(String _regex, String _content)
	{
		Pattern p = Pattern.compile(_regex);
		Matcher match = p.matcher(_content);
		 
		return match;	
	}
	
//	/* @brief:   Simple Regex Tools for you can get any pattern from pages by Regular Expression
//	 *             
//	 * @param:   String _regex:   the regular expression.
//	 * @param:   String _content: string which the regular expression search from.
//	 * @return:  result of match
//	 */
//	public String getItemNumByRegex(String _query)
//	{
//		String pageContent = urlRequest("http://www.sears.com/search=" + _query);
//		
//		
//		String regex = "<p>All Products</p><span class=\"tab-filters-count\">\\(([\\d]+[\\+]*)\\)</span></div>";
//		
//		Matcher match = RegularMatch(regex, pageContent);
//		
//		if(match.find()) {
//		    System.out.println("Number: " + match.group(1));
//		    return match.group(1);
//		}
//		else{
//			System.err.print("[ERROR]==> Can not Find Number of Items");
//			return null;
//		}
//			
//	}
	
	/* @brief:   Query 1: Given a keyword, return the total number of results found
	 *             
	 * @param:   String _url:   the URL of the page 
	 * @return:  result the number of the result of query.
	 */
	public String getItemNum(String _url)
	{
		String pageContent = urlRequest(_url);
		
		// Parse the HTML
		Document doc = Jsoup.parse(pageContent);
		
		Element numberElement = doc.getElementById("tab-filters-top");
		String number = null;
		
			
		if (numberElement != null)
		{
			//Get number by Regular Expression
			Matcher _match = RegularMatch("[\\w]+([\\d+]+)",numberElement.text());
			if (_match.find())
			{
				number = _match.group(0);
			}
		    return number;
		}
		else
		{
			number = "0";
			System.err.print("[ERROR]==> Can not Find Number of Items");
			return null;
		}			
	}
	
	
	/* @brief:   Query 2: Given a content of one page, return all the products information.
	 *             
	 * @param:   String _pageContent:  The content of one page.
	 * @return:  arrayList of the products information.
	 */
	public ArrayList<Products> getItems(String _pageContent)
	{		
		
		// Parse the HTML
		Document doc = Jsoup.parse(_pageContent);
		
		ArrayList<Products> products = new ArrayList<Products>();
			
		//Get all items
		Elements _products = doc.getElementsByClass("cardProdTitle");
		
		//Check length of _products and _price
		for (Element _product : _products)
		{
			String _title = _product.text();
			String _price = "";
			String _vendor = "sears";
			
			//Get the price
			Elements _sibling = _product.siblingElements();
			Elements _selectPrice = _sibling.select("span.price");
			Elements _selectVendor = _sibling.select("#mrkplc");
			
			if(_selectPrice.size() > 0)
			{
				_price = _selectPrice.get(0).text();
			}
			if(_selectVendor.size() > 0)
			{
				_vendor = _selectVendor.get(0).text().replace("Sold by ", "").replace(" ShopYourWay Guarantee Seller", "");
			}
			
			Products _temProduct = new Products(_title, _price, _vendor);
			products.add(_temProduct);
		}
		
		return products;
	}
	
	public static void main(String[] args) {			
		TextScraper textScraper = new TextScraper();
		
		if (args.length==0 || args.length>2) 
		{
			System.err.print("usage: java -jar TextScraper.jar <keyword> [page number]");
			return;
		}
	
		//Deal with the redirect URL
		String _url = textScraper.urlRedirect(args[0]);
		
		//Query 1: Given a keyword, return the total number of results
		if (1 == args.length) 
		{
			String num = textScraper.getItemNum(_url);
			
			if ("0" == num){
				System.out.println("No result match the query of " + args[0]);
			}
			else{
				System.out.println("Number of results is => " + num );
			}
		}
		
		//Query 2: Given a keyword (e.g. "digital cameras") and page number (e.g. "1"), 
		else if (2 == args.length)
		{
			try {
				
				int _pageNumber = Integer.parseInt(args[1]);
				if (_pageNumber < 1) {
					System.err.println("usage: java -jar TextScraper.jar <keyword> [page number]");
					System.err.println("page number should be greater than 1");
					return;
				}
				// Print product information
				ArrayList<Products> items = textScraper.getItems(textScraper.getPageByPageNum(_url, _pageNumber,args[0]));
				int _index = 0;
				for(Products item : items)
				{
					System.out.println(++_index);
					item.PrintProducts();
				}
				
				
			} catch (NumberFormatException e) {
				System.err.println("usage: java -jar TextScraper.jar <keyword> [page number]");
				System.err.println("page number should be Integer!");
				e.printStackTrace();
				return;
			}
		}

	}

}
