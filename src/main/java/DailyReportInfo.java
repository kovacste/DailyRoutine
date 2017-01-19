import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eldo on 2017.01.16..
 */
public class DailyReportInfo {

    public static ArrayList<DailyReportInfo> dailyReportInfos = new ArrayList<>();

    private final String category;
    private List<String> subCategories;
    private List<Long> duration;
    private List<String> remark;

    public DailyReportInfo(String category){
        this.category = category;
        this.subCategories = new ArrayList<>();
        this.duration = new ArrayList<>();
        this.remark = new ArrayList<>();
    }


    public void appendRemark(String remark){
        this.remark.add(remark);
    }

    public String getCategory() {
        return category;
    }

    public List<String> getRemark() {
        return remark;
    }

    public void addSubcatAndDuration(String subcat, Long duration){
        this.subCategories.add(subcat);
        this.duration.add(duration);
    }

    public List<String> getSubCategories(){
        return subCategories;
    }

    public List<Long> getDuration(){
        return duration;
    }

    public static List<String> getPrintableReport() {
        List<String> lines = new ArrayList<>();

        for(DailyReportInfo dailyReportInfo: dailyReportInfos){
            long totalDuration = 0;
            lines.add(dailyReportInfo.getCategory());

            for(int x = 0; x < dailyReportInfo.getSubCategories().size(); x++){
                lines.add("       " + dailyReportInfo.getSubCategories().get(x) + "    " + formatMinutes(dailyReportInfo.getDuration().get(x)));
                lines.add("       " +   "Megjegyzesek: " + dailyReportInfo.getRemark().get(x));
                totalDuration += dailyReportInfo.getDuration().get(x);
            }
            lines.add("Teljes ido: " + formatMinutes(totalDuration));
            lines.add(" ");
        }
        return lines;
    }

    public static String formatMinutes(long minutes){//to: x óra y perc   x.y óra
        int hours = (int) (minutes / 60);
        int remainingMinutes = (int) (minutes - hours * 60);
        float decimal = (float)minutes / 60f;
        return String.valueOf(hours) + " ora     " + String.valueOf(remainingMinutes) + " perc" + " // " + String.format("%.1f", decimal) + "ora";
    }
}
