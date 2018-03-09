import java.util.ArrayList;

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

    public ArrayList<Integer> getHours() {
        return times;
    }

    public void SetHours(String h) {
        String hours = h.replace(":00", "");
        hours = hours.replace(" - ", " ");
        String[] allTimes = hours.split(" ");
        int f = 0;
        int to = 0;
        if(allTimes.length > 2) {
             f = Integer.parseInt(allTimes[2]);
             to = Integer.parseInt(allTimes[3]);
        } else {
             f = Integer.parseInt(allTimes[0]);
             to = Integer.parseInt(allTimes[1]);
        }
        //System.out.println("ALL MY TIMES: ");
        while(f <= to) {
            //System.out.println(f);
            times.add(f);
            f++;

        }
    }
}
