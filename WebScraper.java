import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

/**
 * Class in charge of searching through Shoham and extracting HTML
 * of each class, converting to DB objects and sending to DBManager to be added to
 * Database
 */
public class WebScraper {

    private MongoDBManager DBManager;
    private DBObjectAdapter objectAdapter;

    public static void main(String[] args) {
        //main
        WebScraper ws = new WebScraper();
        ws.TestFunction();
    }


    public WebScraper() {
        DBManager = new MongoDBManager();
        objectAdapter = new DBObjectAdapter();
    }

    public void TestFunction() {

        try {
            Document d = Jsoup.connect("https://en.wikipedia.org/wiki/Main_Page").get();
            System.out.println("HTML TITLE: "+ d.title());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
