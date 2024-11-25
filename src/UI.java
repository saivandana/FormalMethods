// Using AWT container and component classes

import controller.CTLFormula;
import model.KripkeStructure;
import model.State;
import utils.Util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;


public class UI extends JFrame{

    private final JTextField ctlFormula;
    private final JLabel modelTitle;
    private final JTextArea resultArea;
    private final JTextArea modelText;
    private final JComboBox<String> jComboBox;
    private final JFrame jFrame;

    private static KripkeStructure kripke;


    public UI(){
        //constructor
        jFrame = new JFrame();
        jFrame.setTitle("CTL Model Checker");
        jFrame.setSize(new Dimension(300,200));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p1= (JPanel) getContentPane();
        p1.setLayout(new GridLayout(5, 1));
        p1.setBorder(new EmptyBorder(15, 15, 15, 15));
        Border border = BorderFactory.createLineBorder(Color.BLUE);
        p1.setBorder(border);

        JPanel mjPanel1 = new JPanel();
        mjPanel1.setLayout(new GridLayout(1, 2));
        mjPanel1.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel jPanel11 = new JPanel();
        jPanel11.setLayout(new GridLayout(1, 2));

        JButton button1 = new JButton("Upload File");
        button1.addActionListener(new UploadFileListener());
        button1.setBackground(Color.cyan.darker());

        jPanel11.add(new JLabel("Please upload the Model Checker test file: "));
        jPanel11.add(button1);
        jPanel11.setBorder(new EmptyBorder(15, 15, 15, 15));

        mjPanel1.add(jPanel11);

        JPanel mjPanel2 = new JPanel(new FlowLayout());
        mjPanel2.setBorder(new EmptyBorder(10, 10, 10, 10));
        modelTitle = new JLabel("Model");

        JPanel jPanel22 = new JPanel();
        modelText = new JTextArea(7, 60);
        modelText.setEditable(false);
        jPanel22.add(modelText);
        modelText.setBorder(border);
        JScrollPane scrollPane = new JScrollPane(jPanel22);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(500,120));

        mjPanel2.add(modelTitle);
        mjPanel2.add(scrollPane);

        JPanel mjPanel3 = new JPanel();
        mjPanel3.setLayout(new GridLayout(1, 2));
        mjPanel3.setBorder(new EmptyBorder(25, 25, 25, 25));

        jComboBox = new JComboBox<>();
        jComboBox.setSize(23,14);
        JPanel jPanel31 = new JPanel();
        jPanel31.setLayout(new GridLayout(2,2));
        jPanel31.add(new JLabel("Select Model State : "));
        jPanel31.add(jComboBox);
        jPanel31.add(new JLabel("Enter CTL Formula: "));
        ctlFormula =  new JTextField(4);
        jPanel31.add(ctlFormula);
        jPanel31.setBorder(new EmptyBorder(1, 1, 2, 1));

        mjPanel3.add(jPanel31);

        JPanel mjPanel4 =  new JPanel();
        mjPanel4.setLayout(new GridLayout(1, 1));
        mjPanel4.setBorder(new EmptyBorder(15, 15, 15, 15));

        JButton button = new JButton("Check for the result");
        button.addActionListener(new UI.CheckActionListener());
        button.setBackground(Color.green.darker());
        button.setOpaque(true);
        mjPanel4.add(button);
        mjPanel4.setBorder(new EmptyBorder(35, 325, 35, 275));

        JPanel mjPanel5 = new JPanel(new FlowLayout());
        mjPanel5.add(new JLabel("Result: ",JLabel.CENTER));

        JPanel jPanel55 = new JPanel();
        resultArea = new JTextArea(3, 25);
        resultArea.setEditable(false);
        resultArea.setBorder(border);
        jPanel55.add(resultArea);

        mjPanel5.add(jPanel55);

        p1.add(mjPanel1);
        p1.add(mjPanel2);
        p1.add(mjPanel3);
        p1.add(mjPanel4);
        p1.add(mjPanel5);

        jFrame.setPreferredSize(new Dimension(800, 750));
        jFrame.setContentPane(p1);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setLocationRelativeTo(null);

    }

    private void ClearModel() {
        modelText.setText("");
        modelTitle.setText("Model");
        if(jComboBox.getSelectedIndex() != -1) {
            DefaultComboBoxModel theModel = (DefaultComboBoxModel) jComboBox.getModel();
            theModel.removeAllElements();
        }
    }

    class UploadFileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ClearModel();
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new java.io.File("."));
            fileChooser.setFileFilter(new FileNameExtensionFilter("TEXT FILES", "txt", "text"));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int Value  = fileChooser.showOpenDialog(jFrame);
            if(Value == JFileChooser.APPROVE_OPTION) {
                try {
                    new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
                    System.out.println("Selected File"+fileChooser.getSelectedFile());
                    File filePath = fileChooser.getSelectedFile();

                    try {
                        if(filePath == null) {
                            String message  = "Please upload a File!";
                            JOptionPane.showMessageDialog(new JFrame(), message, "Comment",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        assert filePath != null;
                        System.out.println("PATH: "+ filePath.getAbsolutePath());
                        String file = Util.readFile((filePath.getAbsolutePath()));
                        kripke = new KripkeStructure(Util.cleanText(file));

                        ClearModel();
                        for(String s: kripke.getStates()) {
                            jComboBox.addItem(s);
                        }
                        String modelName = filePath.getAbsolutePath().substring(filePath.getAbsolutePath().lastIndexOf('M'));
                        modelTitle.setText(modelName);
                        modelText.setText(kripke.toString());
                    }catch(Exception kse) {
                        kse.printStackTrace();
                        JOptionPane.showMessageDialog(new JFrame(), kse.getMessage(), "Dialog",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }catch(IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Dialog",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    class CheckActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            resultArea.setText("");
            System.out.println("Clicked: " + e.getActionCommand()+" "+ctlFormula.getText());
            try {
                if (kripke == null)
                {
                    throw new Exception("Please load Kripke model");
                }
                if(ctlFormula.getText().isEmpty()) {
                    throw new Exception("Please enter CTL formula!");
                }
                String originalExpression = ctlFormula.getText();
                String expression = originalExpression.replaceAll("\\s", "");
                System.out.println("Model.State  "+ Objects.requireNonNull(jComboBox.getSelectedItem()));
                String checkedStateID = jComboBox.getSelectedItem().toString();

                State checkedState = new State(checkedStateID);

                CTLFormula ctlFormula = new CTLFormula(expression, checkedState, kripke);
                boolean isSatisfy = ctlFormula.IsSatisfy();
                String message = Util.GetMessage(isSatisfy, originalExpression, checkedStateID);
                resultArea.setText("");
                resultArea.append(message);
                System.out.println(message);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                JOptionPane.showMessageDialog(new JFrame(), e1.getMessage(), "Dialog",
                        JOptionPane.ERROR_MESSAGE);
            }

        }
    }


}
