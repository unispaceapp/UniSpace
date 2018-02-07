import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Test_MongoDB {
    //Test classes
    public static void main(String[] argv) {

        StringBuilder s = new StringBuilder();
        MongoClientURI uri = new MongoClientURI("mongodb://nirchook:agent777@ds125198.mlab.com:25198/unispace");
        MongoClient mongoClient = new MongoClient(uri);
        System.out.println("Connected to the database successfully");
        MongoDatabase db = mongoClient.getDatabase("unispace");
        MongoCollection mc = db.getCollection("Test_Collection");


        Test_DBObject obj = new Test_DBObject();
        obj.setNo(4);
        obj.setName("Nirchookee");
        Document testerD = new Document();
        testerD.append("Name", obj.getName());
        mc.insertOne(testerD);

        FindIterable cursor = mc.find();
        for(Object i : cursor) {
            System.out.println(
                    i);
        }
        mongoClient.close();








//        Test_DBObject obj = new Test_DBObject();
//        obj.setNo(4);
//        obj.setName("Nirchook");
       // mc.insertOne(obj);




    }
}
