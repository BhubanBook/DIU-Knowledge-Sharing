package bd.com.siba.siba_diuhelper.OtherClass;

import java.util.ArrayList;
import java.util.List;

public class CoursesInfo {
    public List<String> courseNameList;
    public String[] courseCodeList = {"CSE001", "CSE002", "CSE003", "CSE004"};

    public CoursesInfo() {
        courseNameList = new ArrayList<>();
        courseNameList.add("Java Programming");
        courseNameList.add("C Programming");
        courseNameList.add("Database");
        courseNameList.add("Algorithm");
    }


}
