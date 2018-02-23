import java.util.ArrayList;

public class ClassroomDBObject {

    private int classNumber;
    private int buildingNumber;
    private ArrayList<ArrayList<Integer>> times;
    private String day;
    private char semester;


    public ClassroomDBObject() {
        //In case have a break
        times = new ArrayList<>();
    }

    public void SetClassNumber(int num) {
        classNumber = num;
    }
    public int getClassNumber () {
        return classNumber;
    }

    public void SetBuildingNumber(int num) {
        buildingNumber = num;
    }
    public int getBuildingNumber() {
        return buildingNumber;
    }

    public void AddTime(ArrayList<Integer> time) {
        times.add(time);
    }

    public void setSemester(char sem) {
        semester = sem;
    }
    public char getSemester() {
        return semester;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDay() {
        return day;
    }
}
