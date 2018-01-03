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
    private String course;

    public static void main(String[] args) {
        MailCrawler m = new MailCrawler("http://www.math.ubc.ca/~marcus/Math227/", "MATH227");
        //   m.crawl("http://www.math.ubc.ca/~marcus/Math227/");
        //    m.checkIfLinksChanged();
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
                2000); // run every two seconds
    }


    public MailCrawler(String url, String course) {
        super(url);
        if (htmlDocument != null) {
        //    Elements elements = htmlDocument.select("a[href]");
            links = linkStringSet(htmlDocument);
            this.course = course;
            this.url = url;
            }
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
                            return new PasswordAuthentication("annemariehemlock","thisaccountisfake");
                        }
                    });

            try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("annemariehemlock@gmail.com"));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse("lena.podina@gmail.com"));
                message.setSubject(subj);
                message.setText(msg);

                Transport.send(message);

                System.out.println("Done");

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }

    /**
     * Check if the links on MATH 227 site have changed (if more links have been added).
     */
    private void checkIfLinksChanged() {
        Set<String> newLinks;
        Set<String> removedLinks = new HashSet<>();
        Set<String> addedLinks = new HashSet<>();

            crawl(url);
            newLinks = linkStringSet(htmlDocument);


            for (String newLink : newLinks) {
                if (!links.contains(newLinks)) {
                    addedLinks.add(newLink);
                }
            }
            for (String link : links) {
                if (!newLinks.contains(link)) {
                    removedLinks.add(link);
                }
            }

            if (!removedLinks.isEmpty() && !addedLinks.isEmpty()) {
                notifyOfLinkChange(removedLinks, addedLinks);
            }

    }

    private Set<String> linkStringSet(Document htmlDoc) {
        Set<String> stringSet = new HashSet<>();
        Elements linksOnPage = htmlDocument.select("a[href]");
        for (Element each : linksOnPage) {
            String temp = each.text();
            stringSet.add(temp);
            }
        return stringSet;
    }

    private void notifyOfLinkChange(Set<String> removed, Set<String> added) {
        assert (!(removed.isEmpty() && added.isEmpty()));
        String subj1 = course + ": Link";
        String subj2 = "";
        String intro = "Hi," + "\n\n This is your bot. There's been some link changes on the " +
         "site" + url;
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
                body += "The following links were added: " + addedString;

            }
            if (!removed.isEmpty()) {
                subj2 += " [removal]";
                body += "The following links were removed: " + removedString;
            }

        String subj = subj1 + subj2;
        String msg = intro + body;

        sendMail(subj, msg);
    }
    }
