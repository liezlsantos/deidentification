import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;

import SplashScreen.SplashScreen;


@SuppressWarnings("serial")
public class DeidentificationTool extends JFrame implements ActionListener {

    private Deidentifier deidentifier;

    private JFileChooser fileChooser = new JFileChooser();

    private JMenu fileMenu = new JMenu("File"), helpMenu = new JMenu("Help");
    private JMenuBar menuBar = new JMenuBar();
    private JMenuItem openFile = new JMenuItem("Open File"),
        instruction = new JMenuItem("Instructions"),
        about = new JMenuItem("About De-identification Tool"),
        exit = new JMenuItem("Exit");

    private JPanel mainPanel = new JPanel(),
        pseudoPanel = new JPanel(new GridLayout(0,1)),
        randomPanel = new JPanel(new GridLayout(0,1)),
        swapPanel = new JPanel(new GridLayout(0,1)),
        kPanel = new JPanel(new GridLayout(0,1)),
        kValuePanel = new JPanel(),
        methodsPanel, panel = new JPanel();

    private JLabel info = new JLabel(),
        csvFileLabel = new JLabel("Input File:"),
        subsamplingLabel = new JLabel("Number of records to disclose:"),
        kLabel = new JLabel("k value:");

    private JTextField inputFile = new JTextField(),
        noOfRecordsField = new JTextField(),
        kField = new JTextField("", 9);

    private JButton deidentifyButton = new JButton("De-identify");
    private String instructionMessage = "<html>" +
        "<br> 1. Open the CSV file containing all the records to be de-identified. <br> " +
        "<br> 2. Enter the number of records to be disclosed. It must not exceed the total number of records. <br> " +
        "<br> 3. Check the fields to be de-indentified under each de-identification method. <br>" +
        "<br> 4. Click the 'De-identify' button. <br> "+
        "<br> 5. Enter the filename of the csv file that will contain the de-identified data set and click save. <br> "+
        "</html>";

    private JCheckBox[] pseudoChecks, randomChecks, swapChecks, kChecks;
    private JScrollPane pseudoPane, randomPane, swapPane, kPane;

    private static final Font PLAIN_FONT = new Font("Arial", Font.PLAIN, 12);
    private SplashScreen splash;

    public DeidentificationTool(boolean firstLaunch, String inputFilename) {
        super("De-identification Tool");
        setSize(830, 620);
        setResizable(false);

        fileMenu.setFont(PLAIN_FONT);
            openFile.addActionListener(this);
            openFile.setFont(PLAIN_FONT);
            openFile.setIcon(new ImageIcon(this.getClass().getResource("images/open.png")));
            fileMenu.add(openFile);
            exit.addActionListener(this);
            exit.setFont(PLAIN_FONT);
            exit.setIcon(new ImageIcon(this.getClass().getResource("images/exit.png")));
            fileMenu.add(exit);
        menuBar.add(fileMenu);

        helpMenu.setFont(PLAIN_FONT);
            instruction.addActionListener(this);
            instruction.setFont(PLAIN_FONT);
            instruction.setIcon(new ImageIcon(this.getClass().getResource("images/help.png")));
            helpMenu.add(instruction);
            about.addActionListener(this);
            about.setFont(PLAIN_FONT);
            about.setIcon(new ImageIcon(this.getClass().getResource("images/about.png")));
            helpMenu.add(about);
        menuBar.add(helpMenu);
        this.setJMenuBar(menuBar);

        mainPanel = new JPanel();
        mainPanel.setLayout(null);

        if (firstLaunch) {
            info.setFont(PLAIN_FONT);
            info.setText("Open a CSV file to start.");
            info.setBounds(10, 15, 700, 15);
            mainPanel.add(info);
        } else {
            try {
                loadCSV(inputFilename);
            } catch (Exception e) {
                info.setText("Out of memory.");
            }
        }

        add(mainPanel);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(this.getClass().getResource("images/icon.png")).getImage());

        if (firstLaunch) {
            splash = new SplashScreen();
            while (splash.visible);
        }
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void loadCSV(String inputFilename) {
        deidentifier = new Deidentifier(inputFilename);

        panel = new JPanel();
        panel.setLayout(null);
        info.setFont(PLAIN_FONT);
        info.setText("Choose the de-identification methods to be used.");
        panel.add(info);
        info.setBounds(15, 15, 750, 20);
        panel.setBounds(15, 10, 780, 50);
        TitledBorder border = new TitledBorder(null, " ", TitledBorder.LEADING, TitledBorder.TOP, null, null);
        border.setTitleFont(PLAIN_FONT);
        panel.setBorder(border);
        mainPanel.add(panel);

        JPanel panel2 = new JPanel();
        panel2.setLayout(null);
        csvFileLabel.setFont(PLAIN_FONT);
        csvFileLabel.setBounds(10, 20, 80, 22);
        panel2.add(csvFileLabel);
        inputFile.setBounds(80, 20, 360, 22);
        inputFile.setText(inputFilename);
        inputFile.setEditable(false);
        inputFile.setBackground(Color.white);
        panel2.add(inputFile);

        subsamplingLabel.setBounds(480, 20, 200, 22);
        subsamplingLabel.setFont(PLAIN_FONT);
        panel2.add(subsamplingLabel);

        noOfRecordsField.setBounds(660, 20, 80, 22);
        noOfRecordsField.setFont(PLAIN_FONT);
        panel2.add(noOfRecordsField);
        panel2.setBounds(15, 65, 780, 60);
        border = new TitledBorder(null, "Subsampling", TitledBorder.LEADING, TitledBorder.TOP, null, null);
        border.setTitleFont(PLAIN_FONT);
        panel2.setBorder(border);
        mainPanel.add(panel2);

        GridLayout layout = new GridLayout(1,4);
        layout.setHgap(20);
        layout.setVgap(5);
        methodsPanel = new JPanel(layout);

        kLabel.setBounds(0, 0, 10, 22);
        kValuePanel.add(kLabel);
        kField.setBounds(30, 0, 30, 22);
        kValuePanel.add(kField);
        kPanel.add(kValuePanel);

        randomChecks = new JCheckBox[deidentifier.getNoOfFields()];
        swapChecks = new JCheckBox[deidentifier.getNoOfFields()];
        pseudoChecks = new JCheckBox[deidentifier.getNoOfFields()];
        kChecks = new JCheckBox[deidentifier.getNoOfFields()];

        for (int i = 0; i < deidentifier.getNoOfFields(); i++) {
            randomChecks[i] = new JCheckBox(deidentifier.getHeader()[i]);
            randomPanel.add(randomChecks[i]);
            swapChecks[i] = new JCheckBox(deidentifier.getHeader()[i]);
            swapPanel.add(swapChecks[i]);
            pseudoChecks[i] = new JCheckBox(deidentifier.getHeader()[i]);
            pseudoPanel.add(pseudoChecks[i]);
            kChecks[i]= new JCheckBox(deidentifier.getHeader()[i]);
            kPanel.add(kChecks[i]);
        }

        randomPane = new JScrollPane(randomPanel);
        //randomPane.setBounds(20, 120, 150, 350);
        randomPane.setViewportBorder(new TitledBorder(null, "Random Replacement",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        methodsPanel.add(randomPane);
        //mainPanel.add(randomPane);

        swapPane = new JScrollPane(swapPanel);
        swapPane.setViewportBorder(new TitledBorder(null, "Swapping",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        methodsPanel.add(swapPane);

        pseudoPane = new JScrollPane(pseudoPanel);
        pseudoPane.setViewportBorder(new TitledBorder(null, "Pseudonymization",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        methodsPanel.add(pseudoPane);

        kPane = new JScrollPane(kPanel);
        kPane.setViewportBorder(new TitledBorder(null, "k-Anonymization",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        methodsPanel.add(kPane);

        border = new TitledBorder("De-identification Methods") {
             private Insets customInsets = new Insets(25, 10, 10, 10);
             public Insets getBorderInsets(Component c) {
                  return customInsets;
             }
        };
        border.setTitleFont(PLAIN_FONT);
        methodsPanel.setBorder(border);
        methodsPanel.setBounds(15, 130, 780, 380);

        mainPanel.add(methodsPanel);

        deidentifyButton.setBounds(690, 518, 105, 25);
        deidentifyButton.addActionListener(this);
        deidentifyButton.setIcon(new ImageIcon(this.getClass().getResource("images/save.png")));
        mainPanel.add(deidentifyButton);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(openFile)) {
            int val = fileChooser.showOpenDialog(this);
            if (val == JFileChooser.APPROVE_OPTION) {
                new DeidentificationTool(false,
                    fileChooser.getCurrentDirectory().toString() +
                    "\\" + fileChooser.getSelectedFile().getName()
                );
                setVisible(false);
            }
        } else if (e.getSource().equals(exit)) {
            System.exit(0);
        } else if (e.getSource().equals(instruction)) {
            UIManager.put("OptionPane.messageFont", PLAIN_FONT);
            JOptionPane.showMessageDialog(this,
                instructionMessage,
                "Instruction",
                JOptionPane.PLAIN_MESSAGE
            );
        } else if (e.getSource().equals(about)) {
            UIManager.put("OptionPane.messageFont", PLAIN_FONT);
            JOptionPane.showMessageDialog(this,
                "<html> De-identification Tool <br><br> Version: 1.0 " +
                    "<br> Developed by: Liezl Santos <br><br> " +
                    "This application is used to de-identify records in CSV format.</html>",
                "About De-identification Tool",
                JOptionPane.PLAIN_MESSAGE);
        } else if (e.getSource().equals(deidentifyButton)) {
            if (!noOfRecordsField.getText().equals("")) {
                try {
                    int noOfRecords = Integer.parseInt(noOfRecordsField.getText());
                    if (noOfRecords > deidentifier.getNoOfRecords() || noOfRecords < 1) {
                        info.setText("No. of records to be disclosed must be a number from (1-" +
                            deidentifier.getNoOfRecords() +")"
                        );
                        Toolkit.getDefaultToolkit().beep();
                        return;
                    }
                } catch (Exception e) {
                    info.setText("No. of records to be disclosed is not a valid number.");
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }
            if (!kField.getText().equals("")) {
                try {
                    int k = Integer.parseInt(kField.getText());
                    if (k > deidentifier.getNoOfRecords()) {
                        info.setText("<html> <i>k</i> must not exceed the total no. of records. </html>");
                        Toolkit.getDefaultToolkit().beep();
                        return;
                    }
                } catch (Exception e) {
                    info.setText("<html> Invalid <i>k</i> value. </html>");
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }
            if (kField.getText().equals("") && hasChecked(kChecks)) {
                info.setText("<html> Input <i>k</i> value. </html>");
                Toolkit.getDefaultToolkit().beep();
                return;
            } else {
                if (!noOfRecordsField.getText().equals("")) {
                    deidentifier.subsample(Integer.parseInt(noOfRecordsField.getText()));
                }

                int noOfKChecks = 0;
                for (int i = 0; i < randomChecks.length; i++) {
                    if (randomChecks[i].isSelected()) {
                        deidentifier.replaceRandomly(i);
                    }
                    if (swapChecks[i].isSelected()) {
                        deidentifier.swap(i);
                    }
                    if (pseudoChecks[i].isSelected()) {
                        deidentifier.pseudonymize(i);
                    }
                    if (kChecks[i].isSelected()) {
                        deidentifier.kAnonymize(Integer.parseInt(kField.getText()), i);
                        noOfKChecks++;
                    }
                }

                int[] fields = new int[noOfKChecks];
                int j = 0;
                for (int i = 0; i < randomChecks.length; i++) {
                    if (kChecks[i].isSelected()) {
                        fields[j] = i;
                        j++;
                    }
                }

                if (!kField.getText().equals("")) {
                    deidentifier.kAnonymizeMultipleFields(Integer.parseInt(kField.getText()), fields);
                }

                fileChooser.setSelectedFile(new File("de-identified data set.csv"));
                int rVal = fileChooser.showSaveDialog(this);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    File f = new File(fileChooser.getCurrentDirectory().toString() +
                        "\\" + fileChooser.getSelectedFile().getName()
                    );

                    if (!f.exists() && !f.isDirectory()) {
                        deidentifier.writeCSV(fileChooser.getCurrentDirectory().toString() +
                            "\\" + fileChooser.getSelectedFile().getName()
                        );
                        info.setText("File saved.");
                    } else {
                        int value = JOptionPane.showConfirmDialog(fileChooser,
                            fileChooser.getSelectedFile().getName() +
                                " already exists. \r\n Do you want to replace it?", "Confirm Save As",
                            JOptionPane.YES_NO_OPTION);

                        if (value == JOptionPane.YES_OPTION) {
                            deidentifier.writeCSV(fileChooser.getCurrentDirectory().toString()+ "\\" + fileChooser.getSelectedFile().getName());
                            info.setText("File saved.");
                            inputFile.setText(fileChooser.getCurrentDirectory().toString()+ "\\" + fileChooser.getSelectedFile().getName());
                        }
                    }
                }
            }
        }
    }

    public boolean hasChecked(JCheckBox[] checkboxes) {
        for (int i = 0; i < checkboxes.length; i++) {
            if (checkboxes[i].isSelected()) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException |
            ClassNotFoundException |
            InstantiationException |
            IllegalAccessException e) {
        	e.printStackTrace();
        }
        new DeidentificationTool(true, "");
    }
}
