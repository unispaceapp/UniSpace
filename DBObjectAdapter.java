import org.jsoup.nodes.Element;

/**
 * Converts HTML elements into JSoup objectss
 */
public class DBObjectAdapter {


    public ClassroomDBObject Convert(Element cTable) {
        ClassroomDBObject room = createClassroom(cTable.getElementById("ContentPlaceHolder1_tdHours").text(),
                cTable.getElementById("ContentPlaceHolder1_tdDayOfTheWeek").text(),
                cTable.getElementById("ContentPlaceHolder1_tdSessionStartHour").text(),
                cTable.getElementById("ContentPlaceHolder1_tdBuilding").text(),
                cTable.getElementById("ContentPlaceHolder1_tdRoom").getElementsByTag("td").first().text());
        return room;
    }

    private ClassroomDBObject createClassroom(String semester, String day, String hour, String building, String room) {
        //System.out.println("Sem: " +semester + " day: "+ day + " hour: "+hour+" building: "+building+" room: "+room);
        //Might be online course, so skip it
        ClassroomDBObject classroom = new ClassroomDBObject();

        if (!building.equals("") && !room.equals("") && !hour.equals("")) {
            if (semester.length() > 17) {
                classroom.setSemester('Y');
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

            String b = "";
            for (Character c : building.toCharArray()) {
                if (Character.isDigit(c)) {
                    b += c;
                }
            }
            //TODO falls here when there are two classrooms/buildings - put in as two different entries
            if (room.contains(" ")) {
                String[] rooms = room.split(" ");
            }
            classroom.SetBuildingNumber(Integer.parseInt(b));

            String r = "";
            for (Character c : room.toCharArray()) {
                if (Character.isDigit(c)) {
                    r += c;
                }
            }
            classroom.SetClassNumber(Integer.parseInt(r));

            classroom.SetHours(hour);


            System.out.println("****   Sem: " + classroom.getSemester() + " day: " + classroom.getDay() + " building: " + classroom.getBuildingNumber() + " room: " + classroom.getClassNumber() + " Start Hour: " + classroom.getHours().get(0) + " ****");
            System.out.println();
        }
        return classroom;

    }
}
