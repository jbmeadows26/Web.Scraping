package AssignmentTracker;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MailCrawlerHouse {

    /** INVARIANT: there is exactly one MailCrawler for each website **/

    Set<MailCrawler> mailCrawlers;

    public MailCrawlerHouse() {
        mailCrawlers = new HashSet<>();
    }

    public void addMC(String url, String siteName) {
    if (getMC(url) == null) {
        mailCrawlers.add(new MailCrawler(url, siteName));
    }
    }

    public void removeMC(MailCrawler m) {
        mailCrawlers.remove(m);

    }

    /**
     *  Return the mail crawler with a particular URL
     * @param url the url of the mailcrawler we're searching for
     * @return null if no such mail crawler, the mailcrawler otherwise
     */
    public MailCrawler getMC(String url) {
        for (MailCrawler mc : mailCrawlers) {
            if (mc.getURL() == url) {
                return mc;
            }
        }
        return null;
    }

    public void checkIfLinksChanged() {
        for (MailCrawler m : mailCrawlers) {
            m.checkIfLinksChanged();
        }
    }

    public static void main(String args[]) {
        MailCrawlerHouse mch = new MailCrawlerHouse();

        mch.addMC("http://www.math.ubc.ca/~marcus/Math227/", "MATH227");
        mch.addMC("https://www.ugrad.cs.ubc.ca/~cs221/2017W2/", "CPSC221");
        mch.addMC("https://www.math.ubc.ca/Ugrad/NSERC/ugradUsraProjects.shtml", "math NSERC");
        mch.addMC("https://www.cs.ubc.ca/students/undergrad/research-competitions/getting-involved-research", "cs NSERC");


        Timer t = new Timer();

        t.scheduleAtFixedRate(
                new TimerTask()
                {
                    public void run()
                    {
                        mch.checkIfLinksChanged();
                    }
                },
                0,      // run first occurrence immediately
                1800000); // run every half hour (2000 = 2 sec) (1800000 = 30 min)
    }



}
