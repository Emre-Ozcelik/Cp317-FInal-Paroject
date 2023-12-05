import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResultDisplayGUI extends JFrame {
    private JTextArea resultsArea;
    private JTextArea errorLogArea;

    public ResultDisplayGUI(String resultsFilePath, String errorLogFilePath) {
        setTitle("Results and Error Log");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        resultsArea = new JTextArea(15, 30);
        errorLogArea = new JTextArea(15, 30);
        resultsArea.setEditable(false);
        errorLogArea.setEditable(false);

        displayFileContents(resultsFilePath, resultsArea);
        displayFileContents(errorLogFilePath, errorLogArea);

        JPanel resultPanel = new JPanel(new GridLayout(2, 1));
        resultPanel.add(new JScrollPane(resultsArea));
        resultPanel.add(new JScrollPane(errorLogArea));

        add(resultPanel);
    }

    private void displayFileContents(String filePath, JTextArea textArea) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            textArea.setText(content);
        } catch (IOException e) {
            textArea.setText("Failed to load file: " + e.getMessage());
        }
    }
}
