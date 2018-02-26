import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class receives JSoup objects and converts them into correct format,
 * and then adds it to the correct collection in DataBase
 */
public class MongoDBManager {

    private MongoDatabase db;
    private Map<String, MongoCollection> collections;
    private Document basicDoc;

    public MongoDBManager() {
        collections = new LinkedHashMap<>();
        MongoClientURI uri = new MongoClientURI("mongodb://nirchook:agent777@ds125198.mlab.com:25198/unispace");
        MongoClient mongoClient = new MongoClient(uri);
        System.out.println("Connected to the database successfully");
        this.db = mongoClient.getDatabase("unispace");

        basicDoc = new Document();
        basicDoc.append("8", 0);
        basicDoc.append("9", 0);
        basicDoc.append("10", 0);
        basicDoc.append("11", 0);
        basicDoc.append("12", 0);
        basicDoc.append("13", 0);
        basicDoc.append("14", 0);
        basicDoc.append("15", 0);
        basicDoc.append("16", 0);
        basicDoc.append("17", 0);
        basicDoc.append("18", 0);
        basicDoc.append("19", 0);
        basicDoc.append("20", 0);


        collections.put("Sunday", db.getCollection("Classrooms_Sunday"));
        collections.put("Monday", db.getCollection("Classrooms_Monday"));
        collections.put("Tuesday", db.getCollection("Classrooms_Tuesday"));
        collections.put("Wednesday", db.getCollection("Classrooms_Wednesday"));
        collections.put("Thursday", db.getCollection("Classrooms_Thursday"));
        collections.put("Friday", db.getCollection("Classrooms_Friday"));
    }

    public boolean AddToDB(ClassroomDBObject classroom) {


        //GET CORRECT TABLE FROM DATABASE
        MongoCollection mc = collections.get(classroom.getDay());

        //IF CLASSROOM AND BUILDING ALREADY IN DB, JUST UPDATE TIMES
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("building", classroom.getBuildingNumber());
        whereQuery.put("room", classroom.getClassNumber());
        FindIterable cursor = mc.find(whereQuery);

        boolean exists = false;

        for(Object i: cursor) {
            exists = true;
        }
        if(exists) {
            Document d = new Document("building", classroom.getBuildingNumber());
            Bson filter = d.append("room", classroom.getClassNumber());
            Bson newValue = new Document(Integer.toString(classroom.getHour()), 1);
            Bson updateOperationDocument = new Document("$set", newValue);
            mc.updateOne(filter, updateOperationDocument);
        }

        //ELSE, ADD WHOLE ROW
        Document newClass = new Document(basicDoc);
        newClass.append("building", classroom.getBuildingNumber());
        newClass.append("room", classroom.getClassNumber());
        newClass.remove(Integer.toString(classroom.getHour()));
        newClass.append(Integer.toString(classroom.getHour()), 1);
        mc.insertOne(newClass);

        return true;
    }


}
