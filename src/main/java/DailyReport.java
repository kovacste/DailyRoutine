import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Eldo on 2017.01.14..
 */
public class DailyReport extends JFrame{

    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;

    public DailyReport(){

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocation(
                (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth() - WIDTH) / 2,
                (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight() - HEIGHT) / 2
        );
        setResizable(false);
        setTitle("Daily Report");
        setLayout(new FlowLayout());
        final JTextField saveDirectoryTextField = new JTextField();
        saveDirectoryTextField.setPreferredSize(new Dimension(250, 20));
        saveDirectoryTextField.setEnabled(false);

        final JTextField emailAddressTextField = new JTextField();
        emailAddressTextField.setPreferredSize(new Dimension(250, 20));

        final JButton saveButton = new JButton("Save");
        JButton selectDestinationButton = new JButton("Select destination");
        JButton sendEmailButton = new JButton("Send in e-mail");



        selectDestinationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("."));
                chooser.setDialogTitle("Choose save destination");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                    saveDirectoryTextField.setText(String.valueOf(chooser.getSelectedFile()));
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //saving file to directory set in text field
                if(!saveDirectoryTextField.getText().equals("")){
                    Utilities.updateDailyReportInfo();
                    Utilities.saveToPDF(saveDirectoryTextField.getText());
                } else {
                    JOptionPane.showMessageDialog(getFrame(),"Choose destination folder!");
                }

            }
        });

        sendEmailButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //also implement What to send
                Utilities.sendReportInEmail(emailAddressTextField.getText());
            }
        });

        add(saveButton);
        add(selectDestinationButton);
        add(saveDirectoryTextField);

        add(sendEmailButton);
        add(emailAddressTextField);

        pack();
        setVisible(true);
    }

    public JFrame getFrame(){
        return this;
    }
}
