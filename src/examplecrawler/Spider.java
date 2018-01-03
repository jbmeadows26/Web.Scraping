package examplecrawler;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Spider {

    // Fields
    private static final int MAX_PAGES_TO_SEARCH = 10;
    private Set<String> pagesVisited = new HashSet<String>();
    private List<String> pagesToVisit = new LinkedList<String>();

    public Spider() {

    }

    private String nextURL() {

        for (int i = 0; i < pagesToVisit.size() ; i++ ) {
            String next = pagesToVisit.get(i);

            if (!pagesVisited.contains(next)) {
                pagesToVisit.remove(i);
                pagesVisited.add(next);

                return next;

            }
        }
        return "";

    }


    public void search(String url, String searchWord) {
        while(this.pagesVisited.size() < MAX_PAGES_TO_SEARCH) {
            String currentUrl;
            SpiderLeg leg = new SpiderLeg();

            if(pagesToVisit.isEmpty()) { //if you haven't visited any pages yet
                currentUrl = url;
                pagesVisited.add(url);
            }
            else {
                currentUrl = nextURL();
            }

            leg.crawl(currentUrl); // Lots of stuff happening here. Look at the crawl method in examplecrawler.SpiderLeg
            boolean success = leg.searchForWord(searchWord); // able to find word?
            if(success) { //if able to find word
                System.out.println(String.format("**Success** Word %s found at %s", searchWord, currentUrl));
                break;
            }
            this.pagesToVisit.addAll(leg.getLinks());
        }
        System.out.println(String.format("**Done** Visited %s web page(s)", this.pagesVisited.size()));

    }
}
