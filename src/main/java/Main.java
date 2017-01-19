import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Eldo on 2017.01.13..
 */
public class Main {


    public static void main(String[] args){

        Utilities.loadTodaysActivities();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main().createGui();
            }
        });
    }

    private void createGui(){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(300, 400));
        frame.setResizable(false);
        frame.setTitle("Daily Routine");
        setupSysTray(frame);
        frame.setJMenuBar(createMenuBar());
        frame = createActivityElements(frame);
        frame.setLayout(new GridLayout(3,1));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void setupSysTray(final JFrame frame) {

        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
        }

        final PopupMenu popupMenu = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(Utilities.createImage("megafontrayicon.gif", "tray image"));
        final SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        trayIcon.setPopupMenu(popupMenu);
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,"System tray");
            }
        });

        MenuItem show = new MenuItem("Show window");
        MenuItem exit = new MenuItem("Exit");
        show.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(true);
            }
        });

        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                frame.dispose();
                System.exit(0);
            }
        });
        popupMenu.add(show);
        popupMenu.add(exit);
    }

    private JMenuBar createMenuBar(){

        /*Menu bar*/
        JMenuBar menuBar = new JMenuBar();

        /*First menu*/
        JMenu menu = new JMenu("File");

        JMenuItem item = new JMenuItem("Whatever");
        item.setMnemonic(KeyEvent.VK_F);
        item.setEnabled(false);

        menu.add(item);

        item = new JMenuItem("Quit");
        item.setMnemonic(KeyEvent.VK_Q);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.add(item);

        menuBar.add(menu);


        /*Second menu*/
        menu = new JMenu("Report");
        menu.setMnemonic(KeyEvent.VK_R);

        item = new JMenuItem("Daily report");
        item.setMnemonic(KeyEvent.VK_D);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new DailyReport();
            }
        });
        menu.add(item);

        item = new JMenuItem("Weekly report");
        item.setMnemonic(KeyEvent.VK_D);
        item.setEnabled(false);
        menu.add(item);

        item = new JMenuItem("Overall statistics");
        item.setMnemonic(KeyEvent.VK_D);
        item.setEnabled(false);
        menu.add(item);

        menuBar.add(menu);


        return menuBar;
    }

    private JFrame createActivityElements(final JFrame frame){

        FlowLayout rowLayout = new FlowLayout(FlowLayout.RIGHT);
        JPanel activityRow = new JPanel();
        activityRow.setLayout(rowLayout);

        JPanel timeRow = new JPanel();
        timeRow.setLayout(rowLayout);

        JPanel remarkRow = new JPanel();
        remarkRow.setLayout(rowLayout);


        JFrame finalFrame = frame;

        String[] category = new String[]{"Sport","Work","Free time Activity"};
        String[] sportSubcategories = new String[]{"Football", "Basketball", "Baseball", "Swimming", "Running"};
        String[] workSubcategories = new String[]{"Work time","Overtime", "Meeting", "Traveling"};
        String[] freeTimeSubcategories = new String[]{"Television", "Cinema", "Theatre", "Reading", "Playing"};

        final ComboBoxModel[] models = new ComboBoxModel[3];
        models[0] = new DefaultComboBoxModel(sportSubcategories);
        models[1] = new DefaultComboBoxModel(workSubcategories);
        models[2] = new DefaultComboBoxModel(freeTimeSubcategories);



        final JComboBox<String> categoryComboBox = new JComboBox<>(category);
        categoryComboBox.setPreferredSize(new Dimension(150,20));

        final JComboBox<String> subcategoryComboBox = new JComboBox<>();
        subcategoryComboBox.setPreferredSize(new Dimension(150,20));
        subcategoryComboBox.setModel(models[0]);

        categoryComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = categoryComboBox.getSelectedIndex();
                subcategoryComboBox.setModel(models[i]);
            }
        });



        JMenuItem item = new JMenuItem("Item");
        categoryComboBox.add(item);
        final JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(200,70));
        JButton addButton = new JButton("Add");


        /*Start time selector*/
        final JSpinner startTimeSpinner = new JSpinner(new SpinnerDateModel());
        startTimeSpinner.setPreferredSize(new Dimension(200,20));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(startTimeSpinner, "yyyy/MM/dd HH:mm");
        startTimeSpinner.setEditor(dateEditor);

        /*End time selector*/
        final JSpinner endTimeSpinner = new JSpinner(new SpinnerDateModel());
        endTimeSpinner.setPreferredSize(new Dimension(200,20));
        dateEditor = new JSpinner.DateEditor(endTimeSpinner, "yyyy/MM/dd HH:mm");

        endTimeSpinner.setEditor(dateEditor);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                Date startDate = (Date) startTimeSpinner.getValue();
                Date endDate = (Date) endTimeSpinner.getValue();
                if(startDate.after(endDate)){
                    JOptionPane.showMessageDialog(frame, "The start of the activity cannot be later than the beginning of it!");
                    return;
                }

                if(!Utilities.compareDates(startDate, endDate)){
                    JOptionPane.showMessageDialog(frame, "There are activities in this time!");
                    return;
                }

                if(!Utilities.isSameDay(startDate, endDate)){
                    JOptionPane.showMessageDialog(frame, "Activities should start and finish the same day");
                    return;
                }
                if(textField.getText().equals(""))textField.setText("No remark");
                Activity.activities.add(new Activity(
                        String.valueOf(categoryComboBox.getSelectedItem()),
                        String.valueOf(subcategoryComboBox.getSelectedItem()),
                        (Date) startTimeSpinner.getValue(),
                        (Date) endTimeSpinner.getValue(),
                         textField.getText()));

                try {
                    Utilities.saveTodaysActivities();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });

        JLabel label = new JLabel("Select an activity");
        JLabel label1 = new JLabel("Select sub activity");
        activityRow.add(label);
        activityRow.add(categoryComboBox);
        activityRow.add(label1);
        activityRow.add(subcategoryComboBox);

        frame.add(activityRow);
        JLabel label2 = new JLabel("Start time:");
        JLabel label3 = new JLabel("End time:");
        timeRow.add(label2);
        timeRow.add(startTimeSpinner);
        timeRow.add(label3);
        timeRow.add(endTimeSpinner);
        frame.add(timeRow);
        JLabel label4 = new JLabel("Remark:");
        remarkRow.add(label4);
        remarkRow.add(textField);
        remarkRow.add(addButton);
        frame.add(remarkRow);
        return finalFrame;
    }

}
