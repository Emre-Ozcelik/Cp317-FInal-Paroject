import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class gui extends JFrame {

    private JTextArea filePreviewArea1, filePreviewArea2, filePreviewArea3, filePreviewArea4;
    private String filePath1, filePath2;
    private JLabel filePreviewLabel1, filePreviewLabel2;
    private final String pathToErrorLogFile = "ErrorLog.txt"; // Default path for ErrorLog.txt
    private final String pathToResultsFile = "Results.txt";   // Default path for Results.txt

    public gui() {
        // Set up the frame
        setTitle("Final Marks Calculator v1.0");
        setSize(800, 600); // Size of the GUI
        setLocationRelativeTo(null); // Center the window
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initComponents(); // Initialize and set up components
    }

    private void initComponents() {
        // Create labels
        filePreviewLabel1 = new JLabel("Preview of Grades Data File");
        filePreviewLabel2 = new JLabel("Preview of Student's Names File");

        // Create buttons
        JButton chooseFile1Button = new JButton("Select Grades Data File");
        JButton chooseFile2Button = new JButton("Select Student's Names File");
        JButton calculateButton = new JButton("Calculate Final Marks");
        JButton closeButton = new JButton("Close");

        // Set button colors
        chooseFile1Button.setBackground(Color.BLUE);
        chooseFile1Button.setForeground(Color.WHITE);
        chooseFile2Button.setBackground(Color.BLUE);
        chooseFile2Button.setForeground(Color.WHITE);
        calculateButton.setBackground(new Color(0, 128, 0)); // Dark green
        calculateButton.setForeground(Color.WHITE);
        closeButton.setBackground(Color.RED);
        closeButton.setForeground(Color.WHITE);

        // Ensure buttons are opaque to show background color
        chooseFile1Button.setOpaque(true);
        chooseFile1Button.setBorderPainted(false); // Optional, for a flat look
        chooseFile2Button.setOpaque(true);
        chooseFile2Button.setBorderPainted(false); // Optional, for a flat look
        calculateButton.setOpaque(true);
        calculateButton.setBorderPainted(false); // Optional, for a flat look
        closeButton.setOpaque(true);
        closeButton.setBorderPainted(false); // Optional, for a flat look

        // Create text areas for file previews
        filePreviewArea1 = new JTextArea(15, 30);
        filePreviewArea2 = new JTextArea(15, 30);
        filePreviewArea3 = new JTextArea(15, 30);
        filePreviewArea4 = new JTextArea(15, 30);
        setEditableForTextAreas(false); // Set text areas to non-editable

        // Add action listeners
        chooseFile1Button.addActionListener(e -> chooseFile(1));
        chooseFile2Button.addActionListener(e -> chooseFile(2));
        calculateButton.addActionListener(e -> runMainLogic());
        closeButton.addActionListener(e -> System.exit(0)); // Terminate the program

        // Layout
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(chooseFile1Button);
        buttonPanel.add(chooseFile2Button);
        buttonPanel.add(calculateButton);
        buttonPanel.add(closeButton);

        JPanel mainPreviewPanel = createPreviewPanel(); // Create panel with previews and labels

        // Add panels to frame
        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.NORTH);
        add(mainPreviewPanel, BorderLayout.CENTER);
    }

    private void setEditableForTextAreas(boolean editable) {
        filePreviewArea1.setEditable(editable);
        filePreviewArea2.setEditable(editable);
        filePreviewArea3.setEditable(editable);
        filePreviewArea4.setEditable(editable);
    }

    private JPanel createPreviewPanel() {
        // Create Labels
        JLabel resultsPreviewLabel = new JLabel("Results Preview");
        JLabel errorLogPreviewLabel = new JLabel("Error Log Preview");

        // Create Panels for previews
        JPanel previewPanel1 = createIndividualPreviewPanel(filePreviewLabel1, filePreviewArea1);
        JPanel previewPanel2 = createIndividualPreviewPanel(filePreviewLabel2, filePreviewArea2);
        JPanel previewPanel3 = createIndividualPreviewPanel(resultsPreviewLabel, filePreviewArea3);
        JPanel previewPanel4 = createIndividualPreviewPanel(errorLogPreviewLabel, filePreviewArea4);

        // Main preview panel
        JPanel mainPreviewPanel = new JPanel(new GridLayout(2, 2));
        mainPreviewPanel.add(previewPanel1);
        mainPreviewPanel.add(previewPanel2);
        mainPreviewPanel.add(previewPanel3);
        mainPreviewPanel.add(previewPanel4);

        return mainPreviewPanel;
    }


    private JPanel createIndividualPreviewPanel(JLabel label, JTextArea textArea) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Set a margin around the JScrollPane
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 10 pixels margin on all sides

        return panel;
    }

    private void chooseFile(int fileNumber) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String fileName = selectedFile.getName(); // Get the name of the selected file
            try {
                String content = new String(Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath())));
                if (fileNumber == 1) {
                    filePreviewArea1.setText(content);
                    filePath1 = selectedFile.getAbsolutePath();
                    filePreviewLabel1.setText("Preview of " + fileName); // Update the label with the file name
                } else {
                    filePreviewArea2.setText(content);
                    filePath2 = selectedFile.getAbsolutePath();
                    filePreviewLabel2.setText("Preview of " + fileName); // Update the label with the file name
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
            }
        }
    }
    
    private void runMainLogic() {
        if (filePath1 != null && filePath2 != null) {
            new Thread(() -> {
                try {
                    System.out.println(" anything " + filePath1 + " \n " + filePath2);
                    Main.processFiles(filePath2, filePath1, pathToErrorLogFile, pathToResultsFile);
                    JOptionPane.showMessageDialog(this, "Processing completed successfully.");

                    // Display the results file
                    String content = new String(Files.readAllBytes(Paths.get(pathToResultsFile)));
                    filePreviewArea3.setText(content);

                    // Display the error log file
                    content = new String(Files.readAllBytes(Paths.get(pathToErrorLogFile)));
                    filePreviewArea4.setText(content);

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error during processing: " + e.getMessage());
                }
            }).start();
        } else {
            JOptionPane.showMessageDialog(this, "Please select both files first.");
        }
    }


    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            gui ex = new gui();
            ex.setVisible(true);
        });
    }
}