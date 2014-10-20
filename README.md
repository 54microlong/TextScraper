@Brief : http://www.sears.com search results Text Scraper
@author: Long Chen

@Main Function: 
It provide two function for access to search results of the sears.com
Query 1: Total number of results
Given a keyword, such as "digital camera", return the total number of results
Query 2: Result Object
Given a keyword (e.g. "digital cameras") and page number (e.g. "1"), return the 
results in a result object and then print results on screen. For each result,
return the following information: 
Title/Product Name (e.g. "Samsung TL100 Digital Camera")
Price of the product
Vendor

@Usage: 
Query 1: (requires a single argument)
java -jar Assignment.jar <keyword> (e.g. java -jar Assignment.jar "baby strollers")
Query 2: (requires two arguments)
java -jar Assignment.jar <keyword> <page number> (e.g. java -jar Assignment.jar "baby strollers" 2)


Approaches: 
Query 1: First get the content of the web page and using jsoup to mining need
number of the products.
Query 2: First get the content of the web page and then using jsoup to mining
all the products of the page.

@About the page number: 
Since sears.com do not have very clear parameter and url, so different search
query will cause different URL such as ["digital", "digital camera"]. latter
need to redirect to an very long URL while former do not.
So, I use the approach to mine the next page url from the first page and then
modify the pageNum into what I want to.

@About the redirect of the page: 
Sometimes, some query can not get the result but only redirect coding, so we
need to deal with the redirect problem. I use Regular Expression to check
whether redirect coding exist in fetched page, it will be renewed if page was redirected.

