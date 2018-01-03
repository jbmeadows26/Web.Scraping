package util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HeavyLifter {

    protected Document htmlDocument; // This is our web page, or in other words, our document
    // We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    protected Connection connection;

    public HeavyLifter(String url) {
    crawl(url);
    }

    protected boolean crawl(String url) // Give it a URL and it makes an HTTP request for a web page
    {

        try {
            connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;

            if (connection.response().statusCode() == 200)
            // 200 is the HTTP OK status code
            // indicating that everything is great.
            {
                System.out.println("\n**Visiting** Received web page at " + url);
            }
            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }

        //    Elements linksOnPage = htmlDocument.select("a[href]");
        //    System.out.println("Found (" + linksOnPage.size() + ") links");

            return true;
        } catch (IOException ioe) {
            // We were not successful in our HTTP request
            return false;
        }
    }

    public void getBody() { //prints out html body from the htmlDocument stored
        System.out.println(htmlDocument.body());

    }

    protected void enterData(Connection.Method method, String action, String id, String input) {

        try {
            connection.timeout(1000).data("action", action).data(id, input).method(method).execute();
        } catch (IOException ioe) {
            System.out.println("**Failure** Data could not be entered");
        }
    }

    protected void printLinks() { //prints out all links on the htmlDocument page
        Elements linksOnPage = htmlDocument.select("a[href]");
        for (Element each : linksOnPage) {
            String temp = each.text();
           // if(temp.startsWith("/url?q=")){
                //use regex to get domain name
                System.out.println(temp);

           //     }

        }
    }

    /**
     * Prints out the possible parameters for input fields (basically where you can enter data)
     * Uses htmlDocument field as the document
     */
    protected void getPossibleParamsInput() {
        Set<Form> parsedForms = new HashSet<>(); //input
        int formCounter = 1;
        int inputCounter = 1;
        int selectCounter = 1;

        Element htmlElement = (Element) htmlDocument;
        Elements forms = htmlElement.getElementsByTag("form");
        for (Element each : forms) {

            System.out.println(formCounter + ". Form method: " + each.attr("method")
            + "\n   action: " + each.attr("action"));

            Elements inputs = each.getElementsByTag("input");
            Elements selects = each.getElementsByTag("select");

            for (Element input : inputs) {

                System.out.println(inputCounter + ") " + " Input name: " + input.attr("name")
                + "\n    value: " + input.attr("value"));

            //    tempForm.addInput(i);
                inputCounter++;
            }
            inputCounter = 1;

            for (Element select : selects) {
                System.out.println("Selects:");
                System.out.println(selectCounter + ") " + " Input name: " + select.attr("name")
                        + "\n    class: " + select.attr("class") + "\n options: " +
                        select.attr("options"));

                //    tempForm.addInput(i);
                selectCounter++;
            }
            selectCounter = 1;

            formCounter++;
          //  parsedForms.add(tempForm);
        }
      //  System.out.println(parsedForms);
    }

    private class Input {

        private String name;
        private String value;

        public Input(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    private class Form {

        private String method;
        private List<Input> inputs;

        public Form(String method) {
            inputs = new ArrayList<>();
            this.method = method;
        }

        public void addInput(Input i) {
            inputs.add(i);
        }

        public List<Input> getInputs() {
            return inputs;
        }

    }

}
