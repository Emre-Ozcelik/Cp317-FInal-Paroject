import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;
import java.util.*;
public class gui extends JFrame {

    private JTextArea filePreviewArea1;
    private JTextArea filePreviewArea2;
    private String filePath1;
    private String filePath2;
    private final String pathToErrorLogFile = "ErrorLog.txt"; // Default path for ErrorLog.txt
    private final String pathToResultsFile = "Results.txt";   // Default path for Results.txt

    public gui() {
        // Set up the frame
        setTitle("File Chooser GUI with Preview");
        setSize(800, 600); // Size of the GUI
        setLocationRelativeTo(null); // Center the window
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create buttons
        JButton chooseFile1Button = new JButton("Choose Course File");
        JButton chooseFile2Button = new JButton("Choose Name File");
        JButton calculateButton = new JButton("Calculate");
        JButton closeButton = new JButton("Close");

        // Create text areas for file previews
        filePreviewArea1 = new JTextArea(15, 30); // Size of text areas
        filePreviewArea2 = new JTextArea(15, 30); // Size of text areas
        filePreviewArea1.setEditable(false);
        filePreviewArea2.setEditable(false);

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

        JPanel previewPanel = new JPanel();
        previewPanel.add(new JScrollPane(filePreviewArea1));
        previewPanel.add(new JScrollPane(filePreviewArea2));

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.NORTH);
        add(previewPanel, BorderLayout.CENTER);
    }

    private void chooseFile(int fileNumber) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                String content = new String(Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath())));
                if (fileNumber == 1) {
                    filePreviewArea1.setText(content);
                    filePath1 = selectedFile.getAbsolutePath();
                } else {
                    filePreviewArea2.setText(content);
                    filePath2 = selectedFile.getAbsolutePath();
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
                    System.out.println(" anythign " + filePath1 + " \n " + filePath2);
                    Main.processFiles(filePath2, filePath1, pathToErrorLogFile, pathToResultsFile);
                    JOptionPane.showMessageDialog(this, "Processing completed successfully.");
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
