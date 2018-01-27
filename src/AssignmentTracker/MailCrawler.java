package AssignmentTracker;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.HeavyLifter;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

public class MailCrawler extends HeavyLifter {

    //Only crawls a single site: url. Can check for a change in links and email if there is a change.

    private Set<String> links;
    private String url;
    private String siteName;

    @Override
    public boolean crawl(String url) { //every time you crawl, update link set
        boolean b = super.crawl(url);
        if (b) {
            links = linkStringSet(htmlDocument);
        }
        return b;
    }

    public static void main(String[] args) {
        MailCrawler m =
                new MailCrawler("[siteurl]", "[sitename]");
        Timer t = new Timer();

        t.scheduleAtFixedRate(
                new TimerTask()
                {
                    public void run()
                    {
                        m.checkIfLinksChanged();
                    }
                },
                0,      // run first occurrence immediately
                2000); // run every half hour (2000 = 2 sec)
    }


    public MailCrawler(String url, String siteName) {
        super(url); //does nothing
        crawl(url);
        if (htmlDocument != null) {
        //    Elements elements = htmlDocument.select("a[href]");
            links = linkStringSet(htmlDocument);
            this.siteName = siteName;
            this.url = url;
            }
        }

    public String getURL() {
        return this.url;
    }



    /**
     * Send an email from annemariehemlock to me.
     ** @param subj the subject of the email
     ** @param msg the body of email
     */

    public static void sendMail(String subj, String msg) {
      //  public static void main(String[] args) {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("[youremail]","[yourpassword]");
                        }
                    });

            try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("[youremail]@gmail.com"));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse("[destemail]@gmail.com"));
                message.setSubject(subj);
                message.setText(msg);

                Transport.send(message);

                System.out.println("Done");

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }

    /**
     * Check if the links on MATH 227 site have changed (if more links have been added). Prints response.
     */
    public void checkIfLinksChanged() {

    //  THIS may WORK

/*
        String oldBody = htmlDocument.html();
        crawl(url); //updates links field
        String newBody = htmlDocument.html();

        String diff1 = StringUtils.difference(oldBody, newBody); //remainder of newBody where it starts to differ
        // from oldBody
        String rDiff1 = StringUtils.reverse(diff1);
        String reverseOld = StringUtils.reverse(oldBody);
        String rDiff2 = StringUtils.difference(reverseOld, rDiff1);

        String diff2 = StringUtils.reverse(rDiff2); //interval of difference


        System.out.println(diff2);

            if (oldBody.equals(newBody)) {
                //System.out.println("\n no change");
            }
            else {System.out.println("\n change detected");
                String subj1 = siteName + ": changes noted";
                String intro = "Hi," + "\n\nThis is your bot. There's been some changes on " + url;
                sendMail(subj1, intro);}

*/

            /*
        Elements linksBefore = htmlDocument.select("a[href]");
        crawl(url); //updates links field
        Elements linksAfter = htmlDocument.select("a[href]");

        Set<Element> setLinksBefore = new HashSet<>(linksBefore);
        Set<Element> setLinksAfter = new HashSet<>(linksAfter);

        Set<Element> addedLinks = new HashSet<>();
        Set<Element> removedLinks = new HashSet<>();


        for (Element beforeLink : setLinksBefore) {
            for (Element afterLink : setLinksAfter) {
                if (beforeLink.isEqualNode(afterLink)) {

                }
            }
        }

        for (Element each : setLinksAfter) {
            if (!setLinksBefore.contains(each)) {
                addedLinks.add(each);
            }
        }
        if (addedLinks.isEmpty() && removedLinks.isEmpty()) {
            System.out.println("\n no change");
        }
        else {System.out.println("\n change detected");}*/


//         THIS WORKS



        Set<String> addedLinks = new HashSet<>();
        Set<String> removedLinks = new HashSet<>();

            Set<String> oldLinks = linkStringSet(htmlDocument);
            crawl(url); //updates links field
            Set<String> newLinks = linkStringSet(htmlDocument);

            for (String newLink : newLinks) {
                if (!oldLinks.contains(newLink)) {
                    addedLinks.add(newLink);
                }
            }
            for (String link : oldLinks) {
                if (!newLinks.contains(link)) {
                    removedLinks.add(link);
                }
            }

            if (!removedLinks.isEmpty() || !addedLinks.isEmpty()) {
              //  System.out.println("A change was detected");
                notifyOfLinkChange(removedLinks, addedLinks);
              //  System.out.println("An email was sent");
            }



      /*  System.out.println("removed:");
        System.out.println(removedLinks);
        System.out.println("added:");
        System.out.println(addedLinks); */

     //   System.out.println(links);

    }

    private Set<String> linkStringSet(Document htmlDoc) {
        Set<String> stringSet = new HashSet<>();
        Elements linksOnPage = htmlDocument.select("a[href]");
        for (Element each : linksOnPage) {
            String temp = each.attr("href");
            stringSet.add(temp);
            }
        return stringSet;
    }

    private void notifyOfLinkChange(Set<String> removed, Set<String> added) {
        assert (!(removed.isEmpty() && added.isEmpty()));
        String subj1 = siteName + ": Link";
        String subj2 = "";
        String intro = "Hi," + "\n\nThis is your bot. There's been some link changes on " + url;
        String body = "";
        String addedString = "";
        for (String string : added) {
            addedString += "\n" + string;
        }
        String removedString = "";
        for (String string : removed) {
            removedString += "\n" + string;
        }

            if (!added.isEmpty()) {
                subj2 += " [addition]";
                body += "\n\nThe following links were added: " + addedString;

            }
            if (!removed.isEmpty()) {
                subj2 += " [removal]";
                body += "\n\nThe following links were removed: " + removedString;
            }

        String subj = subj1 + subj2;
        String msg = intro + body;

        sendMail(subj, msg);
    }
    }
