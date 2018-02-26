import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.nio.charset.Charset;

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
        obj.setName("Building");
        Document testerD = new Document();

        testerD.append("building", 1004);
        testerD.append("12", 0);
        testerD.append("13", 1);
        mc.insertOne(testerD);


        //IF CLASSROOM AND BUILDING ALREADY IN DB, JUST UPDATE TIMES
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("building", 1004);
        FindIterable cursor = mc.find(whereQuery);
        boolean exists = false;
        for(Object i : cursor) {
            System.out.println(
                    i);
        }
        for(Object i: cursor) {
            exists = true;
        }
        if(exists) {
            Document d = new Document("building", 1004);
            Bson filter = d;
            Bson newValue = new Document("12", 1);
            Bson updateOperationDocument = new Document("$set", newValue);
            mc.updateOne(filter, updateOperationDocument);

        }
         cursor = mc.find(whereQuery);
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
