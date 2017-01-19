
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.zip.InflaterInputStream;


/**
 * Created by Eldo on 2017.01.14..
 */
public class Utilities {

    public static void sendReportInEmail(String to){

        String from = "kovacst.elod@gmail.com";
        String host = "localhost";
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        Session session = Session.getDefaultInstance(properties);

        try{

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("This is the Subject Line!");
            message.setContent("<h1>This is actual message</h1>", "text/html");

            List<String> lines = DailyReportInfo.getPrintableReport();
            for(String line: lines){
                message.setText(line);
            }


            Transport.send(message);

        }catch (MessagingException e){
            e.printStackTrace();
        }
    }

    /*Returns true if the new date intersects with any of the older activities*/
    public static boolean compareDates(Date startTime, Date endTime){
        boolean result = true;
        for(Activity activity: Activity.activities){
            if(startTime.after(activity.getStartTime()) && startTime.before(activity.getEndTime()) || endTime.after(activity.getStartTime()) && endTime.before(activity.getEndTime())){
                result = false;
            }
            if(startTime.equals(activity.getStartTime()) || endTime.equals(activity.getEndTime())){
                result = false;
            }
        }
        return result;
    }


    public static void saveToPDF(String destination){

        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        PDFont font = PDType1Font.COURIER;

        try {
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            float fontSize = 10;
            float leading = 1.5f * fontSize;

            PDRectangle mediabox = page.getMediaBox();
            float margin = 72;

            float startX = mediabox.getLowerLeftX() + margin;
            float startY = mediabox.getUpperRightY() - margin;

            List<String> lines = DailyReportInfo.getPrintableReport();

            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.moveTextPositionByAmount(startX, startY);

            for(String line: lines){
                contentStream.drawString(line);
                contentStream.moveTextPositionByAmount(0, - leading);
            }

            contentStream.endText();

            contentStream.close();

            DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
            Date date = new Date();


            document.save(destination + "\\" + dateFormat.format(date) + ".pdf");
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Image createImage(String path, String description) {
        URL imageURL = Main.class.getResource(path);
        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }

    public static void loadTodaysActivities(){
        DateFormat dateFormat  = new SimpleDateFormat("YYYMMdd");
        Date date = new Date();
        String name = dateFormat.format(date);
        List<String> lines = null;

        /*if(!f.exists() || f.isDirectory()) {
            System.out.print("no such file, returning");
            return;
        }*/
        CodeSource codeSource = Main.class.getProtectionDomain().getCodeSource();
        File jarFile = null;
        try {
            jarFile = new File(codeSource.getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String jarDir = jarFile.getParentFile().getPath();

        if(!new File(jarDir + "/" + name + ".txt").exists()){
            return;
        }
        try {
             lines = readAllLines(name + ".txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (lines.size() == 0) return;

        String category = null;
        String subCategory = null;
        Date startTime = null;
        Date endTime = null;
        String remark = null;

        for(int i = 0; i < lines.size(); i++){


            String line = lines.get(i);
            //System.out.println(line);
            String[] tokens = line.split(";");
            System.out.println(tokens[1]);
            String parameterName = tokens[0];
            String parameterValue = tokens[1];


            switch (parameterName){

                case "category":
                    category = parameterValue;
                    break;
                case "subcategory":
                    subCategory = parameterValue;
                    break;
                case "start":
                    try {
                        startTime = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(parameterValue);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case "end":
                    try {
                        endTime = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(parameterValue);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case "remark":
                    remark = parameterValue;
                    break;
            }
            System.out.println(i);
            if((i+1)%5==0 && i!=0)Activity.activities.add(new Activity(category,subCategory, startTime, endTime, remark));
        }

    }

    public static void saveTodaysActivities() throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String outFileName = formatter.format(date);

        FileOutputStream fos = null;

        fos = new FileOutputStream(outFileName + ".txt");

        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fos));
        formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        for(Activity activity: Activity.activities){
            bufferedWriter.write("category;" + activity.getCategory());
            bufferedWriter.newLine();
            bufferedWriter.write("subcategory;" + activity.getSubCategory());
            bufferedWriter.newLine();
            bufferedWriter.write("start;" + formatter.format(activity.getStartTime()));
            bufferedWriter.newLine();
            bufferedWriter.write("end;" + formatter.format(activity.getEndTime()));
            bufferedWriter.newLine();
            bufferedWriter.write("remark;" + activity.getRemark());
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }

    public static List<String> readAllLines(String fileName) throws Exception {

        CodeSource codeSource = Main.class.getProtectionDomain().getCodeSource();
        File jarFile = new File(codeSource.getLocation().toURI().getPath());
        String jarDir = jarFile.getParentFile().getPath();

            List<String> list = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(jarDir + "/" + fileName)))) {
                String line;
                while ((line = br.readLine()) != null) {
                    list.add(line);
                }
            }
            return list;
    }

    public static void updateDailyReportInfo(){
        DailyReportInfo.dailyReportInfos.clear();
        boolean foundItsPlace = false;

        for(Activity activity: Activity.activities){

            if(DailyReportInfo.dailyReportInfos.size() == 0){
                DailyReportInfo info = new DailyReportInfo(activity.getCategory());
                info.addSubcatAndDuration(activity.getSubCategory(), activity.getDurationMinutes());
                info.appendRemark(activity.getRemark());
                DailyReportInfo.dailyReportInfos.add(info);
                continue;
            }

            for(int x = 0; x < DailyReportInfo.dailyReportInfos.size(); x++){

                if(DailyReportInfo.dailyReportInfos.get(x).getCategory().equals(activity.getCategory())){
                    foundItsPlace = true;
                    DailyReportInfo.dailyReportInfos.get(x).addSubcatAndDuration(activity.getSubCategory(), activity.getDurationMinutes());
                    DailyReportInfo.dailyReportInfos.get(x).appendRemark(activity.getRemark());
                }
            }

            if(!foundItsPlace){
                DailyReportInfo info = new DailyReportInfo(activity.getCategory());
                info.addSubcatAndDuration(activity.getSubCategory(), activity.getDurationMinutes());
                info.appendRemark(activity.getRemark());
                DailyReportInfo.dailyReportInfos.add(info);
            }
            foundItsPlace = false;
        }
    }


    /*http://www.java2s.com/Code/Java/Data-Type/Checksifacalendardateistoday.htm*/

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

}
