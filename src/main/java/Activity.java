import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created by Eldo on 2017.01.14..
 */
public class Activity {

    public static ArrayList<Activity> activities = new ArrayList<>();


    private String category;
    private String subCategory;
    private Date startTime;
    private Date endTime;
    private String remark;


    public Activity(String category, String subCategory, Date startTime, Date endTime, String remark){
        this.category = category;
        this.subCategory = subCategory;
        this.startTime = startTime;
        this.endTime = endTime;
        this.remark += remark;
    }

    public String getCategory(){
        return category;
    }

    public Date getStartTime(){
        return startTime;
    }

    public Date getEndTime(){
        return endTime;
    }

    public String toString(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return "Start time: " +
                formatter.format(getStartTime()) +
                "   End time:  " +
                formatter.format(getEndTime()) +
                "   Activity name: " +
                getCategory() + " " +
                getSubCategory() + " " +
                getRemark();
    }

    public String getRemark(){
        return remark;
    }

    public String getSubCategory(){
        return subCategory;
    }

    public long getDurationMinutes(){
        long difference = getEndTime().getTime() - getStartTime().getTime();
        System.out.println(difference);
        long durationInMinutes = MILLISECONDS.toMinutes(difference);
        return durationInMinutes;
    }

}
