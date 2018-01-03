package examplecrawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SpiderLeg {

    private List<String> links = new LinkedList<String>(); // Just a list of URLs
    private Document htmlDocument; // This is our web page, or in other words, our document
    // We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";


    /**
     * Makes an HTTP request for a web page
     * @param url the url of the webpage we'd like to crawl
     * @return true if the crawl was successful, false otherwise
     */
    public boolean crawl(String url) // Give it a URL and it makes an HTTP request for a web page
    {
        try
        {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;

            if(connection.response().statusCode() == 200)
            // 200 is the HTTP OK status code
            // indicating that everything is great.
            {
                System.out.println("\n**Visiting** Received web page at " + url);
            }
            if(!connection.response().contentType().contains("text/html"))
            {
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }

            Elements linksOnPage = htmlDocument.select("a[href]");
            System.out.println("Found (" + linksOnPage.size() + ") links");
            for(Element link : linksOnPage)
            {
                this.links.add(link.absUrl("href"));
            }
            return true;
        }
        catch(IOException ioe)
        {
            // We were not successful in our HTTP request
            return false;
        }
    }

    /**
     *  Searches for a word in the htmlDocument. Should only be called after a successful crawl
     * @param searchWord the word we'd like to find
     * @return true if we have found the word, false otherwise
     */
    public boolean searchForWord(String searchWord) // Tries to find a word on the page
    {
        if(this.htmlDocument == null)
        {
            System.out.println("ERROR! Call crawl() before performing analysis on the document");
            return false;
        }
        System.out.println("Searching for the word " + searchWord + "...");
        String bodyText = htmlDocument.body().text();
        return bodyText.toLowerCase().contains(searchWord.toLowerCase());
    }

    public List<String> getLinks() // Returns a list of all the URLs on the page
    {
        return this.links;
    }

}
