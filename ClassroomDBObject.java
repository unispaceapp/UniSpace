import java.util.ArrayList;

public class ClassroomDBObject {

    private int class_number;
    private int building_number;
    private ArrayList<ArrayList<Integer>> times;


    public ClassroomDBObject() {
        //In case have a break
        times = new ArrayList<>();
    }

    public void SetClassNumber(int num) {
        class_number = num;
    }

    public void SetBuildingNumber(int num) {
        building_number = num;
    }

    public void AddTime(ArrayList<Integer> time) {
        times.add(time);
    }
}
