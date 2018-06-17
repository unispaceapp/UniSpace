import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Flips through all the pages on Shoham, and adds all pages to an array, and
 * then scrapes each page of courses to gather information
 */
public class WebScraper {

    static final WebClient browser = new WebClient(BrowserVersion.CHROME);
    private DBObjectAdapter objectAdapter = new DBObjectAdapter();
    private MongoDBManager DBManager = new MongoDBManager();
    private List<HtmlPage> allPages = new ArrayList<>();


    public static void main(String[] args) throws IOException {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
        WebScraper scraper = new WebScraper();
        scraper.ScrapePages();
    }


    public WebScraper() {
        browser.setAjaxController(new NicelyResynchronizingAjaxController());
        browser.getOptions().setRedirectEnabled(true);

    }

    /**
     * Function to scrape all pages
     */
    public void ScrapePages() {
        HtmlPage currentPage = null;
        boolean morePages = true;
        try {
            currentPage = (HtmlPage) browser.getPage("https://shoham.biu.ac.il/BiuCoursesViewer/MainPage.aspx");
        } catch (Exception e) {
            System.out.println("Could not open browser window");
            e.printStackTrace();
        }

        try {
            HtmlButton nextAnchor = (HtmlButton) currentPage.getElementsByTagName("button").get(2);
            currentPage = nextAnchor.click();
            int flipCounter = 0;
            int use = 0;
            while (morePages) {
                for (int pageNum = 2; ; pageNum++) {
                    if (flipCounter > 0) {
                        use = pageNum + 2;
                    } else {
                        use = pageNum;
                    }
                    List<DomNode> pageButton = currentPage.getByXPath("//*[@id=\"ContentPlaceHolder1_gvLessons\"]/tbody/tr[24]/td/table/tbody/tr/td[" + Integer.toString(use) + "]/a");

                    HtmlAnchor clickForNewPage = (HtmlAnchor) pageButton.get(0);
                    if (clickForNewPage.asText().equals("...")) {
                        currentPage = clickForNewPage.click();
                        break;
                    }
                    currentPage = clickForNewPage.click();
                    System.out.println("New Page Number: " + clickForNewPage.asText());

                    allPages.add(currentPage);
                    // Last page
                    if(clickForNewPage.asText().equals("414")) {
                        morePages = false;
                        break;
                    }

                }
                flipCounter++;

            }
            //Adds all courses to DB
            AddPagesToDB(allPages);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Runs through each page of courses, and adds each course to the Mongo DB
     * @param pages all pages of courses
     * @throws IOException
     */
    private void AddPagesToDB(List<HtmlPage> pages) throws IOException {
        HtmlAnchor course = null;
        HtmlPage courseInfo = null;
        for (HtmlPage page : pages) {
            int row = 2;
            course = (HtmlAnchor) page.getElementById("ContentPlaceHolder1_gvLessons_lnkDetails_" + Integer.toString(row - 2));
            while (course != null) {
                courseInfo = course.click();
                DomElement h = courseInfo.getElementById("ContentPlaceHolder1_tdBuilding");
                if (h != null) {
                    if (h.getElementsByTagName("td").size() == 0) {
                        row++;
                        course = (HtmlAnchor) page.getElementById("ContentPlaceHolder1_gvLessons_lnkDetails_" + Integer.toString(row - 2));
                        continue;
                    }
                    String building = courseInfo.getElementById("ContentPlaceHolder1_tdBuilding").getElementsByTagName("td").get(0).asText();
                    String classroom = courseInfo.getElementById("ContentPlaceHolder1_tdRoom").getElementsByTagName("td").get(0).asText();
                    String time = courseInfo.getElementById("ContentPlaceHolder1_tdSessionStartHour").getElementsByTagName("td").get(0).asText();
                    String sem = courseInfo.getElementById("ContentPlaceHolder1_tdHours").asText();
                    String day = courseInfo.getElementById("ContentPlaceHolder1_tdDayOfTheWeek").asText();
                    ClassroomDBObject classObject = objectAdapter.createClassroom(sem, day, time, building, classroom);
                    DBManager.AddToDB(classObject);
                    row++;
                    course = (HtmlAnchor) page.getElementById("ContentPlaceHolder1_gvLessons_lnkDetails_" + Integer.toString(row - 2));
                } else {
                    row++;
                    course = (HtmlAnchor) page.getElementById("ContentPlaceHolder1_gvLessons_lnkDetails_" + Integer.toString(row - 2));
                }
            }
        }
    }

}

