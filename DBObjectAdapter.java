import org.jsoup.nodes.Element;

/**
 * Converts HTML elements into JSoup objects
 */
public class DBObjectAdapter {


    public ClassroomDBObject Convert(Element element) {
        return new ClassroomDBObject();
    }
}
