import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import javax.script.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Class in charge of searching through Shoham and extracting HTML
 * of each class, converting to DB objects and sending to DBManager to be added to
 * Database
 */
public class WebScraper {

    private MongoDBManager DBManager;
    private DBObjectAdapter objectAdapter;

    public static void main(String[] args) {
        WebScraper ws = new WebScraper();
        ws.ScrapeAllPages();
    }


    public WebScraper() {
        //TODO uncomment when ready
        //DBManager = new MongoDBManager();
        objectAdapter = new DBObjectAdapter();
    }


    public void ScrapeAllPages() {

        Document currentPage = GetFirstPage();  // GET FIRST PAGE OF COURSES
        ScrapeSinglePage(currentPage);  // SCRAPE FIRST PAGE
        //TODO do all pages, not just 2-5
        for(int index = 2; index < 5; index++) {  // LOOP THROUGH REST OF PAGES
            currentPage = GetNextPage(currentPage, index);
            ScrapeSinglePage(currentPage);
        }
    }

    public void ScrapeSinglePage(Document page) {

        try {

            Element table = page.getElementById("ContentPlaceHolder1_gvLessons");  //GET TABLE WITH COURSES
            ArrayList<Element> t = table.getElementsByTag("tbody");
            Element tt = t.get(0);
            int rowCounter = 2;
            Element row = tt.children().get(rowCounter);

            while (!row.getElementsByTag("td").first().text().equals("&nbsp")) {  //Loop until text in td/row is equal to &nbsp
                Element link = row.getElementById("ContentPlaceHolder1_gvLessons_lnkDetails_" + Integer.toString(rowCounter - 2));  //Gets link to the course details
                if(link == null) {
                    break;
                }
                Attributes id = link.attributes();
                String s = id.asList().get(2).toString();
                s = s.substring(6, s.length() - 1);
                Document courseD = Jsoup.connect("https://shoham.biu.ac.il/BiuCoursesViewer/" + s).get();     //Opens up the link to course details
                ArrayList<Element> tbody = courseD.getElementsByTag("tbody");
                Element cTable = tbody.get(1);
                ClassroomDBObject classroom = objectAdapter.Convert(cTable);
                //TODO uncomment when ready
                // DBManager.AddToDB(classroom);

                rowCounter++;
                row = tt.children().get(rowCounter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Document GetNextPage(Document prevPage, int pageNum) {
        LinkedHashMap<String,String> param = new LinkedHashMap<>();
        Document nextPage = null;
        param.put("__EVENTTARGET","ctl00$ContentPlaceHolder1$gvLessons");
        param.put("__EVENTARGUMENT", "Page$"  + Integer.toString(pageNum) );//
        Elements hiddDivs = prevPage.getElementsByClass("aspNetHidden");
        Elements children =  hiddDivs.get(0).children();
        Node node = children.get(2);
        param.put(node.attr("name"), node.attr("value"));
        children = hiddDivs.get(1).children();
        node = children.get(0);
        param.put(node.attr("name"), node.attr("value"));
        node = children.get(1);
        param.put(node.attr("name"), node.attr("value"));

        try {
             nextPage = Jsoup.connect("https://shoham.biu.ac.il/BiuCoursesViewer/CoursesView.aspx?")
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36")

                    .cookie("ASP.NET_SessionId", "ib1bhxgycggwe15vcfoqpncc")
                    .data(param).post();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nextPage;
    }



    public Document GetFirstPage() {
        Document first = null;
        try {
             first =  Jsoup.connect("https://shoham.biu.ac.il/BiuCoursesViewer/MainPage.aspx?")
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                    .requestBody("__EVENTTARGET=ctl00$ContentPlaceHolder1$btnSearch&__EVENTARGUMENT=&__VIEWSTATE=gWghXrdqdZS1VdpL2FowAtYqWlVIeoKOLzZvPHsdvvKSbKmrJkbn8hR2y%2FoEunq7xMtKs0Z3DFn%2B5Cth47nE7uOjmBVfAg%2B6wIMHYCF8dwNLYSnoUHTPnBz99xUxkdNa%2FhOdmx3Sg88dIB5ITHDr7SvZRzY0SyELgMQYs5JlLoRQLbKnx%2BVvWhsjvRLyYNK6VS%2FgkV90rdIuE2yFbUrdY%2FHdHOYAyPBh3Q%2FniJETlKqybt%2BJQYWwoVardIKoFamwtqLDZLJLf%2Bfu44lD9kZLqFONXKZOD8s1j9SW85%2BWrE6TbxH7BIYws%2FMV1dv81zXdyZnaPgGkNAkMTCCcv5SimK0%2FZsRcr4v7Bq%2Fiv3rlPJ2HfLtvGbJHYlYSqL1pxqqouUUmT%2BJY9omsxx45eTd0zvUmqgQsQIhtl80aMEeL21bQe2sZFJna7reviPNRdwtQ93HoZg4lfaRcsZfMyweKa6Ubx5IM2F%2F2nqkxIawl7%2FNkNwlmN00szQXxYkiWR54xl4x4fAV5ZShDlWghvVxeGI01LYctz3erB3S26UlkJCMPo0emay4LPF5YIEi%2Fc6042Wm8roFMAR044FWlDqGGj72Y%2Bfa69iYZFtzfXeCnSWW56v0k%2FAe%2BKo9JKZvGE1pR44CT%2FboJo2HH6uWYLqx9yR6JhM8Gxy2Ejr7gkupNVUwr705dWVjvMXqjwgYMYCagfzZIbrp3h5z1L3fhwyn7DPHTYulFRdQ2HfWkQLZx3H5MXk%2Bpm0f2Pq5v%2BOl5N72jrj7eJXptRhZfLcl8c9JkT2BzAAGoRoUA293vpZkvZ4ppKxD6X6jrOP3ezONW60Tyv8eroz%2FtThiotGCvq8%2FAqljrPOlUgQNTBHopjnCm9rt8uX8cn0n6VynDiXHJUDn8cFAnKlOEyuCx8df60wPdo%2FvPqgMLeEkIpEfWOqp%2FVYOp9%2BM6pxsmDKrxWewgLHoxfSbVCLSTX66jxfB4nrkVzvsn%2Fywy4s9%2FE7BRfR6y5Fow1BICfQbaKoC9npjeMJz0YyC3LvNHc97rFSzBR8jIZ4%2FvSB1rtxzyeH4813mMsMyNvGJT0%2B9fXZKMAjlmX6ItmxBE4n3TGmQ4fwXh1%2BxzrWaHkPGPFoEa747rCxCf28IFVO8P8edVxoJC8kxvjytX2%2FRX44Q%2BrbOBLA5h73sjg7%2Bn%2BIU5Rq8Gp%2BHrbmy31%2BOeK5GaJ8%2BH43tO0SaYdCzU4cye%2Fi0qRZ7fE%2FEhqoFDIQtDvhf%2B9ClqzY6qz7UaPMg04QD2ffoph%2FPEu2FG%2FOHo6bcDi5F1g%2BgZuxPclyyFZIZW%2BJY3nGFBfQfckx4u33ozgwCgzjsfDgLsJJnoepUSHzhMGg4LZouvsA%2FxuKX6yDGQqsG8Y3llgd0uGO1w1%2BMuIVkCE3hmshcHW720%2BQR%2FMlmrZ%2BmGmTIzw0u5GU%2B9TiGoz%2BCbTB6j062%2BfA4%2Bnl13MmyRJdgJDuGW5MgkOd4%2B4yDuMxHh8hWnIwopaGuS9K%2FcUr67LrPItK1lcRFOXXkHBUaHS8jxZaT%2F093%2B%2BDZG9LXEYX0sXMTkDhmZXNmNUOqKrcHAm9aJj%2BevBZqOaf2HUaZsJFidRA0rZclPqwZXkm2jtO9GO%2FmmDuuvQwvlFkb4ag%2BjjzJp9dkzYFozFf4lldjR3TQofCHRgsB0O92UtOJo6dWGSB10WiTvTeSFT%2FPdVGb5ljpvUyjImwTB1YQg78YOWm4LK2zaEwV8fbE4naJ6ZBISrw7GKaLHr6Fkbs%2FbA7w3wH0GjDa0tGEBsYk%2FgVJXj%2FVO9AoKaxlBmckEgoKAXho5w%2Bs3aQ4jnBIlzICHaxR%2FjZIadX8L4%2Fuf1s7%2FK%2BT%2FeUAhkp2Qu87BZLmu7Q1x%2BDuLtM4LDTXcTuVfE2mo4ubS8UCIc9cxXAJV%2FrzcIPwZyUEx8FfpFdnoU3vTq7mB7ZVpL6%2Fcdy0sEM%2B5U3t95ULDDWGIt5bYgpOiu4suifUpwYbjQxT78LushpAc%2FkhaXu4pKkPNSCraUwoNvCWIz%2F1Yjrk2WJsJOIxmW7vXDM3ClCsDPqpPjLuRQHDqaeCWBkUAWgfrXIxDfFOJIlk61Lan%2FN012Q5l9d8IcDtIhALeCdaMnrP9MJtDA%2F76NUOtPm60xoWE4EQ9dyNGQlnKbuPcg5NiocjffjBNx8I%2BfOfUtRlubMITtrfNmpJrIW3bLwOwoytmpSg2YR%2B1ugoFRB3s3Xhxygiu%2F2LyIq0nFnePaQg0LvpkKkyKeLlWq6jRhkQQbpW9c1yoPCJyJIpeCWiCyTs351S94A%2BUjL4w4AbRlncIisetRlk8sTOlPbkwGv%2BiC8V5TuVffSA%2FFBq2SVqQ1o1RFYAcrxtYSdWdB%2Bglfz806uJ5fpxW7Tj6r%2FIrGPNhpJPPH%2FxsDQR%2BljDImLoy4sKcibGEZEGcQYS9GDeeuaVm406LPqGU%2BMzzkzT9mEPQXKB6MCByy9Y1DONUqfRUtXNd8RPpEL1tDWE6tpYbX4NJh331c71zPFy%2BRS%2BPJGxXtJq5M8cVInkJnTOhNdeQG4X9YZWHiNG%2FGzO0mvcAewkW4pIfvGJa0ekamO193UStGfnCChz4fptH8UrbtOU%2BseQOreybE4FhHONerkwNZYehPeQh5%2BMR83zJQERkcOYhILRURTlXvJG5hDAQi1Xon%2Bf%2BkiK%2FAcQzk7Kjr45p3x8h%2BMg0ezbUKHOxQcOQ9G2jy83hPbEGiTT8lxYjwtQ6R3LUKXxPmqhXvFAspEPOK5cF3AvTMreSbeaUJfMpilEFlXxsv587t8v%2BPSEi0Nswmw5Mwn18SjNS6cChc0m66K%2FKHdYHo9hWlYj8QecfELCVpvF%2F5yAA1AfzWDAoetGrSJZpNm2CxA82odGmuLAB2b%2B0lcqeWz2tVJsW6wXDXHXtbNLfcjKFjNsJB%2FmhUIhkiwra3P5mNkrM%2F%2FbBrunppLO7Iw%2FCFg%2FGUImm4jho30K06LAqP%2F%2FUx6FwnIGXunCtuKHDFRAir64pVT2OuXF6xY2qaL5gqSCufrvI1ztXDqeH8%2FfGWKLvr1oAFL2PLlPofTaC0%2BjLs3HKuQzZWcWDOUkAAm9PGLylhqD8NC6dpUzIcl5hHrIMHGSlvNxKXjoKMDOGdvUmun25w7%2FwLLmeKhq7ZKEm1UBtM4W0rXSy67Wo6QDjF1SCfAT3eNLRkaug2wTJDUQ7kSAPZLXKH31fy3hMTR5KKP6gP%2FqIFv7IkBdDZeFv20Q3ZExzPXg4bta%2FXYXKkw%2FXFLYhLsOt0goeWlZy0Q3NSol09vlIc%2F31ptZHZAVvqHI1TYEJPMRSxXw7IpAPpVeOQ4UujRb2d6zCoUgiLoWe0iAizJ%2BT%2BzwrXnhqxjQBQrQdbhOY%2Ffd1y7X8XixOQTBrkuAaPy7fFhWcL2EjNB3q0hlhRQ%2Fb%2BA%2FuFnTHqrPGGORFLmDDLH4%2FHeJpOqqVHlpfiTV1yLYl0Q6ifdnrbGgGu4BiTaEt51EaczIUZgahe33CMLUc0tB%2BE3JZo0D8T1IJZzo%2BuautabY0CXFyyOvivJFt6L9xLqZLYXPRgKDZWfsdosTYSY47DQ8P4PXlzhlPGtiDTLNEKpe16LH3fhh3CIP0vvPfVXB2kXHtBJVUZJElEolR%2BFLT9gaLIwLL1WXmss%2BpTT7ywZSs9zv45%2BDssgWBZXk3H6T2VsFkIaCZ%2Fbgo1XrYWRiAbpu7ZJF85C9mx90yP0IFfbUw1eFYjohNB8%2FyupmCaBmMo88J4jh24YElxXq1WWU0V7Xa31HCz1Im%2FFsldDRCaonW8GCp8QFV%2B%2BtjsyMDbT96MNtDppzALgoH50g%2FpyloLjxLF%2Bz9m%2FHAYqeox15F1zZql5n0LCUDLjQaGOxLLtkL7Lt%2FR37PDXVfXIeY3rIgUjKPT7k2%2Fe2lRK4srDKX0nmM1ZFxbtAcA0OXDM8UbZU2t6V0jRmvsl%2FEv0yFjg0UzwVKpvlL2w95hcE9GEQB8%2BG5Hw5tDfSYLott0Yd5S09uWKw8CGMOmhDwD33CT01i%2FctqD592Y%2FgITSpJFi4nh%2FVTraA%2FvB3SUglO%2BsTfO1VY0dQzYxe9QFLR6u6smQCR4zASxst1stTj0UM4hTBj56l7KrzzcklpoeGTqE27UmZT75YvmTPgTGWmjzRJSEgleEhATAqV5CbsZz8ZUFBi8kuVXXfrqUqiIvwcwv0Iho6RADWBZRxi%2F3cTdX4AFiKGrKR%2FDgvWxfsJ0pEngNMUM7CmMweqrffHHlvkJzkxWRMc0zv6iE0Ml8V4X0PRZmf9d%2FYMGPwkO6xlOljKo5rmp2HWWJ0nAN19mnyZv4vZoY4Jp9A%2B2nmJsqEHRto0cRifgwyCeBo5Cykr7zO0ARNMtAPXEFmMvTM7Iu13ukLjd14dxsRvZWlXxjQ7d5vK1hJLlY9p761qS0xnPjofoc9XCt2vVPx92dM5XRilQA%2FkVQBzqjTWjaiK2astUq0EQka7drZTY3IPp1T6L3i%2F0u%2Bb65FfCLu%2FTAdin9m5SrjcbPM2o9kuEvmIQzAZ%2Bavhtcni5FRmFmUrVJB18PK5uYKPKtHlBgYdO7PsgH%2BO8iB4MZMrrf2uC5as5OptLjXUrCaziuuSBEzKsE37JPLhBPXdMF9%2FeZdPoj83FBKI%2BpBO6SYqFJOtX8cbRw3F5P0N03%2FFnaTYzrKDx4ETeR6wVxw516OV0ttvV0kxEd79k4gLyOiRqnvMV96kOptPVrHnHg4OadyRFYfc5g6z5%2BxJWn5i%2BHaAu2MNPwDXnnq877pjhG35RxxG5yANf1wUTpt%2Bu27eAkjLrwBtq3KREx80o6j78pR7i%2BaHub%2F8NZwfZ3AI07POkeNkvXO4OesFrZis%2Fgklkmjx2uwlMbmeKLP2TnbRQcgcMWjIsbStQEP3qW5jZ54%2Fg68a%2BTRJUULImPySC5HzfFQh9WKmesmCIvM4XXhPvfpLkOEXEyry08A0STDPREY13uUyYfh77t1oavAYyv6qXtU1ZHa8urnahoiWcMeniEo%2BLXejsFMkRS2pq8DzKLDadRA2stSOLDw%2F8I8%2BHpNGhdmxkxjqtizfcg1a%2B4fxbgbjhpoBTADTvH%2Fc2P%2Bt%2BOEcGdNzA%2FG5%2BPnDou40Z%2F5NExH9y0p%2FnyIUpX8EWn0rsb403VvGscI40hiiJEcWOfjkee%2BaVZxXYmNb6HOhTYFd%2FQOo8Ytuf42Lh%2FhYqhFaNmRkEIoMruTTUzonB7%2B6D2G3toHGCvx33qA%2F68BfJ8kMCazGkIMzovioeusCHFEkbF3yDsIrB4xI%2Fv6WzXdURLV5ak1glY4%2BjEKZU7vMRJ9XURgFYuwcwuTv3bcdqtNsKAKmfrfDoLwxTRx8uAwLuz5TTcPondLVaohXbLMKuD0UbkH4n%2F3FmNw95gAatsxC30XGhIFwz3q%2FuVwzza%2B%2B%2FoZuMBixltfWPPj7hhB5LXz0A1xUeM24lOvjzptTAXCBTzg0chgFKkcR9pbyjZoZx90immTaVwnuP0y2Q7rqRJ9A8oJtlK%2B9LuicnqKGCs91xtu5U8vDOlqkNqAw9uRfcRmh7%2FB%2FZOBctH332nFEJtEsmFtirfVC8oT%2Bfvi6K7GIXIn9%2F4GxT8GhCJY4s7gFPhdoFlRK%2BksRE%2BFXQ2Mw7sS9P50mg1i4ql66KEW85L1IMU07Fi8sQqHXsVXbuhDf8pvAVKmbKX6LWCx%2B5LDc7nDWnnah%2Fo1ylnR8yt1ft0KydRMNKhX0bnmFHSCR0dmVgHcBZ7Mi24u3KISV3EbR4hdYUTFo9f%2FquMknjEGot7jKJOdjB7suIxiNwIhkF67S5VYA7jByePfS0a%2BMuej4Suc2hBkbxOcnjeU9AdD3OIlmrcM4gEeLsocnVQ3iiq2uHBSI51Wu6csSMyHTi0yTkS8Q4K85lw4T%2BtTLrYNHgBJAPd8FI%2BmlIIOKbQCtxxgInt16rbybK3jL7ucXM6ImQqhqdF6fg3Jn4q9EnkP%2FdTqJ7nztU5EMzgWjYLDpYeP7AmvxfcdZTjuFYgCZmeanCFJivVaTUL1bMJ5gNXxEL0R1qvu1t3PF3k%2F4TV5v1TeprJM93dUDfiQTPVncIZzzP7f6c%2FTZiO4fIhAyoJLsGSdLNogiCaoElhGOzZ6n3dBVS5DvXzuODNUo8SpgvZHcVlxt84kFWrAh7ORBTwz0bNYt9B%2B%2Blb3aUB2PfVZzXUTNC9lYmCTKEhaIPkhLQKUGpwtkw3ccXvpONXgR3WL9MmYd0a01s83uv3MAcNNT5Gxil9ZzxRwQe1MSU9VeAZ0M%2BDa7CdKxc8xF%2BKgun6f5EnApJZ6wBZkP42EfCwrec3HppKx7ISXnoTDZT%2BaRn7gKOAbNbq5sTom2Yhq8zIOBpDoCX9Uzi3SqjJYSm1dDXWhe6W1pZZBkZWiiWczEcshhmHb8dm2JrUx9WHfbX6yw%2BobNugQ58ZrXP0jW%2BqOyxTaBNLnzjIVrAKw0rwfnHxGJpcDuhKfnoChnbaf5k%2FSrWAYq%2FWOIBErDkkUwjzvQ02y5Gi8KwJKzSCKNLlC8qDbIq64OJk%2F%2BNJBHMnfcUls74LGq1wYhk6Fdx5FPKVhnK25T3F%2Bjqr1ydnPvQ%2BJGwl5OkwPTlkZOnuEgiNjSefEgWm2qWrDd8%2BNzzG8KOHov2EbRxzTYs0uY7gkNxV8%2FS6NBvSofuJ1Ua6S2PM3UTi6KM6Hi9CNDVvFZo9W8Pp2XsA0umxvJbs91nc6LhQvo%2BjtadohUfX6k3rLPdFfkaeRepi0b9hTr1HgphBb33VOXYTwcMtvqLYvI73GTxM%2FUbY%2BsaDCrzPqDC6MAX4OTNxiD2%2BaVu1U55pbkVCZTD0hLTDe2AgAFZYagotTjF4qO6TfNtRI0pzBJWFzn4%2FrkZqPHkF91QtWSJW%2BSmIiJJ1d2lyjzYvJDPDoF3uGPwvXu8wBgOxZL1EkhlJzNlqJF4sZjT2iDppDhAk56ZSdGiPHbvABZd0SJezokpBRVCa1jB4ysHdH6kgcBvnxBtijVzyO0hmJovfpxLOD7rgZcbrG7BshFKi2fmvTHHRWNdStTC0WVxXW4jCtUkVHgC1l%2Fgku1M1HpLkjwCkTFi%2BaexmFnCVH8ayd%2B15OE5h6Vmufpwgdw%2BDteFuaZQM4PoGPDUGWqnwuSZ0tlIVJrnagyvd0K2fKpPVEMpGoF2XC5Id7T3bGw9QlER18AKATS4HjvOdX3xQnH4ZCJEXiqn%2BOQEf%2FeHLUwgbw3XyTBXqJMloa6O6mx%2FDCaCxnWetRH%2FZzgfHmntM%2BLnCeawWf%2FMEkrEuEmsorHU8%2B6wdTthEx6vLV2VwK4cjhizmL3VFw%2FCZPAasg5%2FvmpRLQAAt3bkJKy2ykP%2BxL1OxOXKUdn5B1zJKeSEdkdxEC6QVKIyx5ODpU2ZXP9f28GFFmuuyss4LuaQg7kiiUMxhRKJkJ5Ux13yJLuDUns4TQfWFipsoOgWztCnpyXDA0KFs86CuS9Qti3O1Gx7tUZkyMlG6LiKGTY19mY9WyO8RgnepplQwusdG4QWuIHSp4o7Hi5Eu%2FwZ%2FSB0C50HasxGrcynlaoGW7S81j2f2ec0PEZpQ19WsJ0xLkrufAeMy2DJRbAhdq2IOK2M9R3HV1KUS%2BK58aN%2Bmapixneu3rI%2FQmxWqE7cebGSumkCIHNSahXUOrJ71edEOD1QF1F9ad6bBfUiP9Wcf3jAWSamIGR9Iox2cV4tTgO8fanFs8Ncy2JypclNNJdSXs7JHZkiiBl%2BMnsJkEpiIcOyn8nDasnqGBvM07588JfbTpLBvtmRzXkLOt8a2pJYONYpxdtF7PK%2FJBMArL1yazvaT%2B9GEw92rltV9KQM9N5BhHqZ0V0Zo%2F40NbEF5uPZiBUffnpRuwGdzXIzt801USJ47Q%2BkV6Mr2XnSzDw59R%2FUcvIEydCGiwfAXy1yOirWBAU93q%2Be3q9VuyU4pREf4%2FflpBzGR8217bvgB4%2FBv9B6emba7P9DH3Z1G4xnvLmj9oMgwvgrvW1DBq5xLUYdIsnIFuiNvzjJuZRdfyCmapaZ7PCtBWmpcDKkQcD4UErHTrCiB9DtiUks4nyAQX6nyC9bqZ5OzQamoOOx0QGf8nTNwWwtbKSvvpS6QaM9crMvUo%2BShzE7IKBRTa6q7vi6w%2FHIGAK%2BApknq0L0OtRKUQHIiJ%2BTr%2FQHKr7Q6pAx99F%2BA5ek62d0MtujHk22EJqES4WE%2FVYtyhC68NbETgVYg5RqrhjxSfDvqoJtPMDZCfzKLW%2Fye9iBPB8begd%2F0SYMxZMoJiOLbQQcd95%2FHVhOehtSqBKvx82Va7QfSXKXM%2BG5AkpAx5nWTlhcKvzYZWIR7F%2FgH9ekx4HbxB4YMPsnm0F2RhSzqMssgeaTNdhwdZGTAMZwEC1LGv1ZixBQGWGrAqV8ZyNcWCnH9PufF1iMaXbrl%2FhIqiEg9ECdP%2Ft987uQjwr8QJVrC%2FLbXxPfA9zp1iFwqK4paa92f2n%2FGfaUw%2FhllwROGPUq4CcZ%2F7d7OqMB0pu9BZOWZGAizxlXtPVg%2BW9%2BhjAQDSG5g07EFbSyzmJdJfhCtYNDTdISOn%2BTFJFXOv1d28Dq3%2FMKH0LK8ZxfPVw61vsWIs9Ef6fBBEey3tC%2Ft9UxqNqOlGULT3oC1Fz77uPq884qkVkOR6XSfGUI%2F5kdOUGj3juJtShVRu3VuE3ID49%2B92b%2FgtdCyjUp0KWGqvUuw48F0cNbNo7n1ZaQ0zI96iyR%2BkXp8%2BBhdSO3a71xW6j3jISoqULPh%2BESuuBRRs%2BFgJdbbdUUgXbj20G8s3I%2B%2BypjkNw8Cm1ZqtylR%2BwCs5VrmVpDwURXaJmuYDn%2FFp%2BXteJJspj2%2B0BGJDBKQz3pi1%2Ffp6gdSMNGg2nlwVihxXXMn5wroFPQu8KtwQidCT%2BM%2FYZN1nfGDu4nmlYCpa7jMsygsRR3fQVrAZhc9byvkENQ8ZlRJIUMSKRTe3ZIVX9LWanw085%2BS8aZ9ECT3179pUo9KsP42any92xocfQE7mNOGICi6CTUjmuxAOjKIhlcalz2aYqrQJno2ibUbqwslldfYilgPG2Ztbfu%2BuvZtyCiHF%2FYdsijlHiktVvTVtNpGJ6ae%2BOZyhX2Omk9pHW7ojqLGK%2Fz8d2%2BtAQeruUuurFay42HpNC5u3Xso1pP8kyuLJItWYpwrcvNfNS2c3N1NfGH3Bb2poucSt0q5BKsUk1M8gCwZIQi%2BaJ31a%2FwYpcVFKv50zwdY%2B2NYtWGgn1Hoc%2FvmLM7IMtesAUPVeYA2zFNdUPF7r14UhybL0IIRWXjmwq4g8%2BgEd99YcaoeEtO69dZkXCBqMgA7L0duhlu%2FKsfk7ohZheXyWtbEel22z0RFHVbsYgkoY6if72RVvKPKh4oS0X9u6NbVu%2BqBntNiFwmpStK%2By5F48GIo0vQbl68UrZft02R%2BVwWaxbu3c1Hot0wZ8nmYpEku0ib4VjWI61Y9tGZ%2BUFRgbmvaZIo%2Foe19VQS5Ya%2Bgr8HHLkJa%2FJG00GYUGWLwPdmlIraDizID5LGaQAogruIa9HgHO636WkhThEQyCNUlmJBHva0c3fvQY8vZ3%2FQaxP9fbSivcuwgvKbqWRcoDPLFeo%2Fx62Z%2FaB3YTnUz0zmZ8WkXIUaQkequol5IM7V441aiETiaayy0802cXq3lReYkSmxgkhNLcYnG%2F4XSVjA7ZYJub%2B3ZP0RlcBpqKA1eIjQuTxe1r7VMgzwqgtMym9LzT2S0ai71hIwRXbAl3SL9uw%2FjkSml0i%2BAG35GHpjQZNCIXTwpmfeb4cULBr25LbaY2nLktww8giLNvcEOQc9Z6X%2Fu9NSjUc5kgB8dYbBHz8IeeDGaa2250ICqcBACucGeoP7CSRDfTnFH%2B281HYgzGvQ1%2FeThuB3mLV3cXQB4VX71TZ59l7cnwAAIbMfrK29q7pgDnxbVmjcSxIJF%2B5K84ud%2BUsWKf7nkizIxV8mW5lUWFUxd%2BAX2p6vpXej6aatZwd%2B%2FhJroBEAFHM8T5GB0%2BuTcUQh0DT3b2EiVT7jttWRxf6tH05GOUo6UODqcO%2Fzmmtgp0yqUcK46reLetSkkaChVdt9hegNq5JUg%2FkI50wiiqUglNqT5D1rpCpTFvxnWwMPunzc83nIp5EE4RQJYN6nuQO5ii%2FqiuO6LVmI%2FppLmrZ86DsqUlSOGhP9JokHV8V3I8YFgGfmgyfQIymIE0zjwpZUjWBRjdsryChq1Ci6gHfPZha4Ne7vAAiBvOyl7kj1nyzN6WunnYI31d4PevVSBY04UYX6iqidv8jzrGWnhejZj%2BGuDH05f0nLcHYPiKX6hreKWQkqeV19KO%2Bc2YI67HIHUdxaLWSvyTdz%2BaCD0ffJWbsQsmRXLXfSk06sY2p7WxRvd9TtY5roAZmZZVNBYFeLUdTeQyzcKDmdaRgqTVTH4NJnh9N9RaYi0aN9Ot2NPGyPro0LupXJaUujEuOH%2FJaKePvMnIdjSu4mqTEQ0XWzG%2FeY6mwOuAKlX9IYhg1vBK5YH2S2UDnsC2XNRr2WWQK3FujDG2AWjY22%2BRFL4Xd60L4Gd9fG2Kb%2FkR8lZGY8yWNmnj%2BbqZjg2s5rGN6JisQjOLQ2jMmIBNusmz9ce32dxq2kOwjjABapJsPOBxcXSWiYEy746aZozt4YVxeXOsXZwbbY2YWaiTZXPvzO7tgmsdsGVhRUzATWexudgOq33IVImo%2BRzY0Z3kqPTOTC4n1LV4cBKmexpYaA7OlCYVBcVOkIPZO1Dr7EulYTm5n%2Flke0xQx%2FOVqo%2BSk%2FWBU558TSkcxHl6p84p8fA6tRysSX2zTmG0Ur9K09i4iAMbpfgNZMW736XQsESG%2F5JEMtMse58u3Zfm81UcyTAzQ1VAJEP28lgi5kbqy0vZAQL286%2BUmlCjId82Gv9ET99sxHoODmq%2BaQqUsBNRhy%2Bdkt%2BVPyINTLv7Yi5ueyC%2F95EutOrA7kxS5Gv5Otz1m2yM2IL2%2Bg0LvDHCY5Bd%2B8zOLzxtwfiqpmq97gG%2Byxumy3nTIjoP%2BefOFXolrpneBoftDg9xPf%2FBcS01%2BNhdm%2FmNqXIjJZEZLtDuJhX3LomDAUDbkYI%2FsIHDgUpxIPCUkheMNu14UdDfHOcnNnl96hxobstMh5jz4L4Cn5Bz%2BQrkjB7YpNRrpb%2B0341JbUZv2kO71%2FIMACo6fYuwKg6peqcfLCJhJLOvIirzzn0vBSwJRBhnKbdVG%2Fhun%2BCxHWSKMkDssbqi7joYz1MpkR4%2FzQIlCKinzPpUqIUFyWzgZh0jE7jmavihHOmI4O9m7qbpv5rFprKVH0movqatSMWd%2B%2BGW%2BW4eOUFfSH7rpyorOThGpfwGTWvwTBe5j7qIesYervH2FtUl94Xi4feS29y1vsxkl0NNygtS56BN%2FnZ2XTQ79Mu1xJnC15OxLSBvfm3Q6r%2BNHcjYW2JreoA3ER0ZPn38daVy8NouVlZYBPc92cijJyxo0vVqGF6d2APNoPQwXEfEUx4nA2D7RmpL9UP%2Fo%2BZ%2BCEqZsPPurXuQvFw0PNAu7YUj1tcMRMtfh9RNiePHp3DCqWhzNdgiGy%2B5g1zOHG6pMNCfDwJIKvCNnu9vt%2FmFE%2F6Z2sokrmyePjOPdorIxQSGGcYGC0tAcKfE5TIYuj6%2FfeTjnClTNucFmkk9v8ZwRMiDCH5iiSgMztQPEYcXlhpjCHD0TQ%2FF6T1it%2FbSQGwIl8bLTDPAlcAld%2BwNTG5%2BmddqiSOXkGGeMZmS8unAKk1lI%2F9MU1vgLC9bHmKFASFVMEFDhcBdbG1FDHrMkDBFOfAmwIno6jKGdVSKV%2BhT4XByrwQwkWC7TolHh1cuwOIKC2LGesRTETY7uBRoLIJn4yeTmiqIhf4Me%2FIx%2FmUGgAQfyGgDtR9qAAvS%2F9eGmZYg%2BiMas5wZ2hYRfn6K%2FY8zD45%2FUzUyN3sSfjpEaBqppyxHFGsSL9jC8n7ZSHRBbqu1cjOu%2BPDt7T0uqZlHAUTHlIC7Zajc3QMgRplWy7rkZVb6hRSpWg%2F2bqT5k39c6lJb6RNgmfdBhFqDZwmI9AxiIyaP%2BEL05WUe%2BwMPGvbHhUMLpN42Y5fW77KedG8zfgFg4ml%2FlXZS1ygvlboAYU84atBAvGDz%2BwPFCr4uAQBPtLtMlLQepq51YpLU4QZdAYZZaYc0D8rl%2BF69zbyrbUl%2BOjUcNCd3tSX6bp2AaMKBuPHy1MXx3enDnYQu4Y30yCGQgTw2ttXbV%2Fj5pjjiF2DKt9gswb3u1OGg1jwXMIh6l8Yc8Plxc4AFH0WhEcuFwVcpBAg8mjmRYMW0O2vrOfFH7s1CL83rn%2B%2BuJCVBEDzjXQs31bzkYYXYuVo6x862NDR%2FMyoCJvv6pmmjjlRRuuWnjejt%2BWqq0bcSTUgmFLeDU%2B2EQCYcQiPed2R43%2BDeNnU%2BZCYsFtZdzUWISBEvmv9yTqiG6dmZHD6%2BERictF7Hq8LJ78hs6%2Ff4zuPjmBFpW%2BROfcVDF%2F7idIgOQm8cMbzRvISj2ELX7IOUsEH0ys0J3ypxZptRJpk1kjXgIPOqguzb7mZDNE8n5wctmuwpepxEkV7XvnRBecKZHEaJGSn7mlaTiqVM718LcX0lh0xVPiV7xAqXzY5QkCdyMS%2BOeD7E%2BUjut21eW7aC16oqbL4AMLUnmgOV5GwX%2B%2BYwAZF6zfP0Fr1TU2tWFmR6lRMZ6VuX6%2FewSDr9TJvOkAV9SuUxFHQeQNgJmL4Jl%2BIEnIQujOBnqW5C5GDnwqRLTmQ18fRQlWPmTqxxloc75OTMczJRdoU4BZEfmaC%2FPEcFU9aClUrIbq4WYgWU5qbPGqqMQqWJP1F39D5%2Bqn4tHDJi8ILSsfuGgGhQJsEfFxzjgw%2BQajObYb%2FL5cKIlMoV6dxPwcVJfzQKk5uKWgrPm1bs7Bhfd8Kc4vdkCkEBtPc6om381%2FYcCVCdqeNPuF0d3%2Bb%2F3Ow4jV6yyj3tcnP3hMFo82dtON1opbzsQ8R8OVEWnxmb5mKH%2FLeCOxDKciC45SBv2xBzlBOGxELfWn7ZAn4wv1Xido5EjSewdqnt3ebB5bF9Q3m%2Bqmaagz9Vv44R0N2HysB42AtXvaPIVApzb4rgi8LqE9w3B1bDeaSx%2BpZxRsyTNgUvMJ5AvNEtd2DfpBoqE5DvekS6rSE2rK%2F0euewd69e3h76RgEK2kQSPRed90rjiajbP743fiEThwCsWIzI4eA%2FxsVJ%2BX%2BFs590OSmgp4tt8bgHFutWApojKrS4o9hBgp0eGIeRS49u3fkSR87BOQcp6J6iholPKUyY2e3g8ehuG1B9VmIjEkSUHIwgCtsj8PRXBT7CEu%2FrZ4%2BpkzVAS48yVle76WBewhj7ZSlljAPFyxmz2yxyX5YEn7iXUXgfU897Me%2F4fOIfUOEtUpFQrFQ5dpM0JB0JQorATcmRBlT8ZpFg9hYAdbNLXXV1fMLa%2B6K%2BfdGRUEqRV0rAnfzzhuVaffN%2BIrx4FDYxrTsGb1zTnFMA1QWyZPSfrTXidobybywVYdHhBy49crLXnY5vyw4DULwtb4rDMCu%2FX6lXdcozHsqZmNdXqg%2Bi6rJ7ks%2F0Ym0LFI1GiIokg2xVBiQ9JgqepOvT9GXWBznxsbJX2m8dgA1HUDt5SpBLpnFu5d0xLL6PTTN7%2BsbkAfJY67AgP%2FX98DibaqblQuVjr0ALb2rppCKLEudixokBcN2K7dhrLoY4nTpURn6x3mZcgx4Q8qWt2yWpzw3wTjkGlEiA%2BuohrAXzmbdsjstzGGLmGoTh2c0%2BH8cfxj8ULxsqF4ExWPasHVCPf%2B3MZM14zZmVXcD%2BicuXaNFMODARX8lILg%2Fu%2BjQ6fnynouY5o6zsSbirnHJ18pD8hzmE4k0J0h1Zgay9uwZ413XCAXSF9ORyVLCTRbwzzfMEiEVv8D0GJyIkMFa5pPBmmyCC0YX29b6SJm1QL%2BSpCVqUcQQQ1FsFL2CHxpIw96lPEoPdK09tBUbofQbl7jB%2ByAACElHknc32iO2njTVt1kIC93vvxWVS9fvUEI5z%2FPacWpJKVLs2CA7P6on%2B6V9%2FWCaGDcMMd6kvRDF3%2BZm1Mku33gq0NNvIi16X%2F%2FWAtNh9phmFepQSAmMMDqLIZZYzgUkrc9Ox9gpjxVLXEc4GVolgFOZgW3PYdqU%2Fo2aau5E60TUwBeNnKxNHRX6AVgusdV6BjhPwqLaUpcyqeEvwGYb33Vbvit3cMqnrN%2BOj0Z9gqVESnUrQtyxz8wRnUFki0thZfIaXtBMoVnCa8%2F%2Fnl5yRtD3eJwVYqM8i0atpV77ITdltTnPwWDPN6G5PXM%2BqCPajaIWThTpdLOAHEs77avckgddNq8zkcbQjL69cvDe1EdmIKxR%2Fs8fYc6kw%2FUHhsbJDn%2FoGeAtzf3vPZvBLgrDXopQgYt11CfYjNdTHXtTHHOjs2BqDXiLT0fHlBs4zLDg4dLcpVKdKyyfcGnEw0jKJhBGwXsQ%2BG6sL2dbT5%2FLTxNjFfG3slHBrvZGvqUHsMH%2BfwvOe4rTLRmVHGCxdsZ1RZfr32oA3qes5jAg%3D%3D&__VIEWSTATEGENERATOR=959E085B&__EVENTVALIDATION=TfLg6IbBPUWZyp5ADI8vofGdlwXNPRURtEAueW%2FNXUBYLmVIi04z32Ga3DwCy0wDjUNsVEC8cjqfubvGnIKEOG2xKNFSWbysm%2FGxlZAK5pq%2FyINTGrBM57nNONlO29%2FP05JKDWy8xbJBJ2%2FUveGguLZYM09SOoS%2F381yc%2BwhCm5OxhC4OpA1m%2Fj6Q6nQYx0n%2FQfdw9xT2sd66iWIRjq2tFE5uikKFuGanwYk9aLWvK19sSU76oPunFGSM4QlV8f9AW0krdJndFFFxNFTpxXsWSX0Uze68%2FPRi%2BgdJtFyidXv8DIQvm%2FlrpADVSngDsCCBvqVSLoUcTgCnxvsVX7rfixJogrUpOzsuBzEqMmk7gkG0AAUL165HkzGfklZaaWQiWTtmXGo54q%2FXPh2jMsGsG32To%2B0X3HGmyf%2B4V1CqW%2F4Tv3fNFRDtihn1QjIwa3f&ctl00%24ContentPlaceHolder1%24cmbYear=2018&ctl00%24ContentPlaceHolder1%24txLessonName=&ctl00%24ContentPlaceHolder1%24txLessonCode=&ctl00%24ContentPlaceHolder1%24txTeacherName=&ctl00%24ContentPlaceHolder1%24cmbSessionPeriodFrom=-1&ctl00%24ContentPlaceHolder1%24cmbSessionPeriodTo=-1&ctl00%24ContentPlaceHolder1%24cmbDepartments=-1")
                    .post();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return first;
    }
}
