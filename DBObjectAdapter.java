import com.sun.deploy.util.StringUtils;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

/**
 * Converts HTML elements into JSoup objects
 */
public class DBObjectAdapter {


    public ClassroomDBObject Convert(Element cTable) {
        ClassroomDBObject room = createClassroom(cTable.getElementById("ContentPlaceHolder1_tdHours").text(),
                cTable.getElementById("ContentPlaceHolder1_tdDayOfTheWeek").text(),
                cTable.getElementById("ContentPlaceHolder1_tdSessionStartHour").getElementsByTag("td").text(),
                cTable.getElementById("ContentPlaceHolder1_tdBuilding").getElementsByTag("td").first().text(),
                cTable.getElementById("ContentPlaceHolder1_tdRoom").getElementsByTag("td").first().text());
        return room;
    }


    /**
     * Creates a classroom object
     * @param semester
     * @param day
     * @param hour
     * @param building
     * @param room
     * @return classroom object
     */
    public ClassroomDBObject createClassroom(String semester, String day, String hour, String building, String room) {

        ClassroomDBObject classroom = new ClassroomDBObject();
        if (!building.equals("") && !room.equals("") && !hour.equals("")) {
            if (semester.length() > 17) {
                classroom.setSemester('B');
            } else {
                String sem = semester.substring(6, 7);
                //Only returns elements from semester B for now
                if (sem.equals("א")) {
                    classroom.setSemester('A');
                    return classroom;
                } else {
                    classroom.setSemester('B');
                }
            }

            String HebDay = day.substring(0, 1);
            classroom.setDay(HebDay);

            ArrayList<String> buildings = handleStrings(building);

            ArrayList<String> rooms = handleRooms(room);

            if (buildings.size()>1) {
                if (buildings.get(1).equals("")) {
                    return null;
                }
                classroom.SetBuildingNumber(Integer.parseInt(buildings.get(1)));
            }
            else {
                if (buildings.get(0).equals("")) {
                    return null;
                }
                classroom.SetBuildingNumber(Integer.parseInt(buildings.get(0)));
            }

           
            if(rooms.size()>1)
                classroom.SetClassNumber(Integer.parseInt(rooms.get(1)));
            else
                classroom.SetClassNumber(Integer.parseInt(rooms.get(0)));

            String[] hours = handleHours(hour);
            classroom.SetHours(hours);

            System.out.println("****   Sem: " + classroom.getSemester() + " day: " + classroom.getDay() + " building: " + classroom.getBuildingNumber() + " room: " + classroom.getClassNumber() + " Start Hour: " + classroom.getHours().get(0) + " ****");
            System.out.println();
        }
        return classroom;

    }

    /**
     * Parses string elements into members
     * @param str
     * @return
     */
    private ArrayList<String> handleStrings(String str){
        String[] newStr = str.split(" ");
        ArrayList<String> parsedStr = new ArrayList<String>();
        int index=0;
        for (int i=0; i<newStr.length; i++){
            StringBuilder sb = new StringBuilder();
            String curr = newStr[i];
                if (curr.contains("-")) {
                    int j = 0;
                    while (curr.charAt(j) != '-') {
                        j++;
                    }
                    j++;
                    for (; j < curr.length(); j++) {
                        sb.append(curr.charAt(j));
                    }
                } else {
                    sb.append("");
                }
            if (sb.toString().contains("<")){
                if (curr.contains("<")){
                    sb.reverse();
                    while (!Character.isDigit(sb.charAt(0))){
                        sb.deleteCharAt(0);
                    }
                    sb.reverse();
                }
                if (!sb.toString().contains("/") && !sb.toString().contains(":")) {
                    parsedStr.add(sb.toString());
                }
            }

                parsedStr.add(sb.toString());
        }
        return parsedStr;
    }


    /**
     * Converts the room into the correct members
     * @param str
     * @return an array of rooms
     */
    private ArrayList<String> handleRooms(String str){
        String[] newStr = str.split(" ");
        ArrayList<String> parsedStr = new ArrayList<String>();
        for (int i=0; i<newStr.length; i++){
            StringBuilder sb = new StringBuilder();
            String curr = newStr[i];
            int k=0;
            while (Character.isDigit(curr.charAt(k))){
                k++;
                if (k==curr.length()){
                    break;
                }
            }
            if (k>0) {
                sb.append(curr);
                if (curr.contains("<")){
                    sb.reverse();
                    while (!Character.isDigit(sb.charAt(0))){
                        sb.deleteCharAt(0);
                    }
                   sb.reverse();
                }
                if (!sb.toString().contains("/") && !sb.toString().contains(":")) {
                    parsedStr.add(sb.toString());
                }
            }
        }
        return parsedStr;
    }

    /**
     * Handles hours elements
     * @param hour
     * @return an array of hours
     */
    private String[] handleHours(String hour){
        if (hour.contains("</td>")){
            hour= hour.replace("</td> ", "");
        }
        if (hour.contains("</tr>")){
            hour= hour.replace("</tr> ", "");
        }
        if (hour.length()>27){
            hour = hour.substring(0,27);
            if (hour.contains("<")){
                hour = hour.substring(0,13);
            }
        }
        char[] newChr = new char[16];
        String[] newStr = new String[4];
        int j=0, k=0;
        for (int i=0; i<hour.length(); i++){
            char currentChar = hour.charAt(i);
            if (Character.isDigit(currentChar)){
                newChr[j]=currentChar;
                if (j%4==3){
                    newStr[k]=Character.toString(newChr[j-3])+Character.toString(newChr[j-2]);
                    k++;
                }
                j++;
            }
        }
       return newStr;
    }
}
