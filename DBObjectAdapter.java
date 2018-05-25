import com.sun.deploy.util.StringUtils;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

/**
 * Converts HTML elements into JSoup objectss
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



    public ClassroomDBObject createClassroom(String semester, String day, String hour, String building, String room) {
        //System.out.println("Sem: " +semester + " day: "+ day + " hour: "+hour+" building: "+building+" room: "+room);
        //Might be online course, so skip it
        ClassroomDBObject classroom = new ClassroomDBObject();


        /*if (hour.length()>13) {
            hour = hour.substring(0, 13);
        }*/
        //System.out.println(hour);
        /*if (hour.length()>22){
            System.out.println(hour.length());
            System.out.println("cut "+hour.substring(0,13));
        }
*/
        if (!building.equals("") && !room.equals("") && !hour.equals("")) {
            if (semester.length() > 17) {
                classroom.setSemester('B');
            } else {
                //TODO  only add second semester for now!
                String sem = semester.substring(6, 7);
                if (sem.equals("א")) {
                    classroom.setSemester('A');
                    return classroom;
                } else {
                    classroom.setSemester('B');
                }
            }

            String HebDay = day.substring(0, 1);
            classroom.setDay(HebDay);
           /* String[] buildings; //=  new String[2];

            //we have space when there are two buildings
            //if (building.contains(" ")){
                buildings = building.split(" ");
            *//*} else {
                buildings = building.;
            }*/

            //String[] buildings = handleStrings(building,4, true);
            ArrayList<String> buildings = handleStrings(building);
            for (String s: buildings){
                //System.out.println(s);
            }

           /* String[] parsedBuildings =new String[2];
            int i=0;
            for (String b : buildings) {
                parsedBuildings[i]="";
                for (Character c : b.toCharArray()) {
                    if (Character.isDigit(c)) {
                        parsedBuildings[i] += c;
                    }
                }
                i++;
            }*/

            //if (buildings.size()>1)
                //System.out.println("build chosen:" +buildings.get(1));
            //else
               // System.out.println("build chosen:" +buildings.get(0));
            //TODO falls here when there are two classrooms/buildings - put in as two different entries
            /*if (room.contains(" ")) {
                String[] rooms = room.split(" ");
            }*/

            //String[] rooms = handleStrings(room,3, false);
            ArrayList<String> rooms = handleRooms(room);
            for (String s: rooms){
               // System.out.println(s);
            }

//            if (rooms.size()>1)
//                System.out.println("room chosen:" +rooms.get(1));
//            else
//                System.out.println("room chosen:" +rooms.get(0));


            //TODO saw that two classrooms are being taken as one, like 102203 in 507
            if (buildings.size()>1) {
                if (buildings.get(1).equals("")) {
                    //return classroom;
                    return null;
                }
                classroom.SetBuildingNumber(Integer.parseInt(buildings.get(1)));
            }
            else {
                if (buildings.get(0).equals("")) {
                    //return classroom;
                    return null;
                }
                classroom.SetBuildingNumber(Integer.parseInt(buildings.get(0)));
            }

            /*String[] parsedRooms =new String[2];
            int j=0;
            for (String r: rooms) {
                parsedRooms[j] = "";
                for (Character c : room.toCharArray()) {
                    if (Character.isDigit(c)) {
                        parsedRooms[j] += c;
                    }
                }
                j++;
            }*/
            if(rooms.size()>1)
                classroom.SetClassNumber(Integer.parseInt(rooms.get(1)));
            else
                classroom.SetClassNumber(Integer.parseInt(rooms.get(0)));

            String[] hours = handleHours(hour);
            classroom.SetHours(hours);

            System.out.println("IN ADAPTER:");

            System.out.println("****   Sem: " + classroom.getSemester() + " day: " + classroom.getDay() + " building: " + classroom.getBuildingNumber() + " room: " + classroom.getClassNumber() + " Start Hour: " + classroom.getHours().get(0) + " ****");
            System.out.println();
        }
        return classroom;

    }

    private ArrayList<String> handleStrings(String str){ //}, int length, boolean isBuilding){
        String[] newStr = str.split(" ");
        //String[] parsedStr = new String[newStr.length];
        ArrayList<String> parsedStr = new ArrayList<String>();
        int index=0;
        for (int i=0; i<newStr.length; i++){
            StringBuilder sb = new StringBuilder();
            String curr = newStr[i];
            //if (isBuilding) {
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
/*            } else {
                int k=0;
                boolean hasDigits = true;
                while (!Character.isDigit(curr.charAt(k))){
                    k++;
                    if (k==curr.length()){
                        hasDigits=false;
                        break;
                    }
                }
                if (hasDigits) {
                    sb.append(curr);
                }
            }*/
          /*  String curr = newStr[i];
            *//*String[] currentStr= newStr[i].split("-");
            String curr;
            if (currentStr.length>1){
                curr = currentStr[1];
            } else {
                curr = currentStr[0];
            }*//*
            int size = curr.length();
            for (int j=0; j<size; j++){
                if (Character.isDigit(curr.charAt(j))){
                    sb.append(curr.charAt(j));
                }
            }*/
            //if (sb.length()>0) {
            if (sb.toString().contains("<")){//sb.toString().contains("</tr>") || sb.toString().contains("</td>")){
                //sb.delete(sb.length()-5,sb.length());
                if (curr.contains("<")){//sb.toString().contains("</tr>") || sb.toString().contains("</td>")){
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


            //}

            //TODO ADD A CHECK IF STRING IS NUMBER AND THEN TAKE IT FROM NEWSTR

           /* if (curr.length()>length)
                newStr[i]=curr.substring(0,length);*/
        }
        return parsedStr;
    }


    private ArrayList<String> handleRooms(String str){
        String[] newStr = str.split(" ");
        ArrayList<String> parsedStr = new ArrayList<String>();
        for (int i=0; i<newStr.length; i++){
            StringBuilder sb = new StringBuilder();
            String curr = newStr[i];
            int k=0;
                //boolean hasDigits = true;
            while (Character.isDigit(curr.charAt(k))){
                k++;
                if (k==curr.length()){
                    break;
                }
            }
            if (k>0) {
                sb.append(curr);
                if (curr.contains("<")){//sb.toString().contains("</tr>") || sb.toString().contains("</td>")){
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








            //TODO ADD A CHECK IF STRING IS NUMBER AND THEN TAKE IT FROM NEWSTR

           /* if (curr.length()>length)
                newStr[i]=curr.substring(0,length);*/
        }
        return parsedStr;
    }

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

       /* String hours = hour.replace(":00", "");
        hours = hours.replace(" - ", " ");
        String[] allTimes = hours.split(" ");*/
       return newStr;
    }
}
