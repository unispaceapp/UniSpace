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
import java.lang.reflect.Array;
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
    Map<Integer, String> hours = new HashMap<>();
    private Document basicDoc;

    /**
     * Constructor initializes hours map and connects to MongoDB
     */
    public MongoDBManager() {
        collections = new LinkedHashMap<>();
        MongoClientURI uri = new MongoClientURI("mongodb://nirchook:agent777@ds125198.mlab.com:25198/unispace");
        MongoClient mongoClient = new MongoClient(uri);
        System.out.println("Connected to the database successfully");
        this.db = mongoClient.getDatabase("unispace");

        hours.put(8,"eight");
        hours.put(9,"nine");
        hours.put(10,"ten");
        hours.put(11,"eleven");
        hours.put(12,"twelve");
        hours.put(13,"one");
        hours.put(14,"two");
        hours.put(15,"three");
        hours.put(16,"four");
        hours.put(17,"five");
        hours.put(18,"six");
        hours.put(19,"seven");

        basicDoc = new Document();
        basicDoc.append("eight", 0);
        basicDoc.append("nine", 0);
        basicDoc.append("ten", 0);
        basicDoc.append("eleven", 0);
        basicDoc.append("twelve", 0);
        basicDoc.append("one", 0);
        basicDoc.append("two", 0);
        basicDoc.append("three", 0);
        basicDoc.append("four", 0);
        basicDoc.append("five", 0);
        basicDoc.append("six", 0);
        basicDoc.append("seven", 0);

        collections.put("Sunday", db.getCollection("Classrooms_Sunday"));
        collections.put("Monday", db.getCollection("Classrooms_Monday"));
        collections.put("Tuesday", db.getCollection("Classrooms_Tuesday"));
        collections.put("Wednesday", db.getCollection("Classrooms_Wednesday"));
        collections.put("Thursday", db.getCollection("Classrooms_Thursday"));
        collections.put("Friday", db.getCollection("Classrooms_Friday"));
    }

    public boolean AddToDB(ClassroomDBObject classroom) {
        if(classroom == null) {
            return false;
        }

        //If first semester, does not add to DB
        if(classroom.getSemester()!='B'){
            return false;
        }

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
            ArrayList<Integer> classHours = classroom.getHours();
            Document nVal = new Document();
            for(Integer hour : classHours ) {
                if(hour <= 19)
                    nVal.append(hours.get(hour), 1);
            }
            Bson newValue = new Document(nVal);
            Bson updateOperationDocument = new Document("$set", newValue);
            mc.updateOne(filter, updateOperationDocument);

            return true;
        }

        //ELSE, ADD WHOLE ENTRY TO DB
        Document newClass = new Document(basicDoc);
        newClass.append("building", classroom.getBuildingNumber());
        newClass.append("room", classroom.getClassNumber());
        ArrayList<Integer> classHours = classroom.getHours();
        for(Integer hour : classHours) {
            if(hour <= 19) {
                newClass.remove(hours.get(hour));
                newClass.append(hours.get(hour), 1);
            }
        }
        System.out.println("INSERTING NEW CLASS: " + newClass.toString());
        mc.insertOne(newClass);

        return true;
    }


}
