import java.util.ArrayList;


/**
 * Class to create a classroom object
 */
public class ClassroomDBObject {

    private int classNumber;
    private int buildingNumber;
    private ArrayList<Integer> times;
    private String day;
    private char semester;


    public ClassroomDBObject() {
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

    public void setSemester(char sem) {
        semester = sem;
    }
    public char getSemester() {
        return semester;
    }

    /**
     * Sets the day the lesson occurs
     * @param HebDay hebrew day
     */
    public void setDay(String HebDay) {
        char engDay = HebDay.toCharArray()[0];
        switch(engDay) {

            case 1488:
                this.day = "Sunday";
                break;
            case 1489:
                this.day = "Monday";
                break;
            case 1490:
                this.day = "Tuesday";
                break;
            case 1491:
                this.day = "Wednesday";
                break;
            case 1492:
                this.day = "Thursday";
                break;
            case 1493:
                this.day =  "Friday";
                break;
            default:
                System.out.println("*** DAY UNMATCHED! ***");

        }
    }

    public String getDay() {
        return day;
    }

    /**
     * Returns hours
     * @return an array of hours the classroom is occupied
     */
    public ArrayList<Integer> getHours() {
        return times;
    }

    /**
     * Sets the hours the classroom os occupied
     * @param allTimes
     */
    public void SetHours(String[] allTimes) {
        int f = 0;
        int to = 0;
        if(allTimes.length > 2 && allTimes[2]!=null && allTimes[3]!=null) {
             f = Integer.parseInt(allTimes[2]);
             to = Integer.parseInt(allTimes[3]);
        } else {
             f = Integer.parseInt(allTimes[0]);
             to = Integer.parseInt(allTimes[1]);
        }
        // Adds hours to classroom object
        while(f <= to-1) {
            times.add(f);
            f++;
        }
    }
}
