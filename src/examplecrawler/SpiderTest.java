package examplecrawler;

public class SpiderTest {

    public static void main(String args[]) {
        Spider s = new Spider();
        s.search("http://arstechnica.com/", "computer");
    }
}
