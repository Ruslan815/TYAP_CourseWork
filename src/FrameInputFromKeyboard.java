import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FrameInputFromKeyboard extends Frame implements ActionListener {

    public TextField inputField = new TextField();
    public Choice grammarTypeChoice = new Choice();
    String grammarType;

    FrameInputFromKeyboard(String type) {
        grammarType = type;
        if (type.equals("РГ")) {
            grammarTypeChoice.add("ПЛГ");
            grammarTypeChoice.add("ЛЛГ");
            grammarTypeChoice.setBounds(700, 30, 80, 30);
            this.add(grammarTypeChoice);
        }

        Label inputLabel = new Label("Введите " + type + ":");
        Button continueButton = new Button("Продолжить");

        inputLabel.setBounds(10, 30, 220, 25);
        inputField.setBounds(10, 60, 780, 25);
        continueButton.setBounds(180, 100, 450, 30);

        this.add(inputLabel);
        this.add(inputField);
        this.add(continueButton);

        continueButton.addActionListener(this);

        this.setBackground(new Color(0x4EC25A));
        this.setForeground(new Color(0x2626CC));
        this.setResizable(false);
        this.setBounds(600, 250, 800, 150);
        this.setTitle("Ввод");
        this.setLayout(null);
        this.setVisible(true);

        addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent we) {
                        dispose();
                    }
                }
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (grammarType) {
            case "РГ" -> {  // Счиать РГ с клавиатуры
                String passedGrammarString = this.inputField.getText();
                String someGrammarType = this.grammarTypeChoice.getSelectedItem();
                FrameMain.setInputOfGrammar(passedGrammarString, someGrammarType);
            }
            case "РВ" -> {  // Счиать РВ с клавиатуры
                String passedRegExpString = this.inputField.getText();
                FrameMain.setInputOfRegExp(passedRegExpString);
            }
            case "путь к файлу" -> { // Считать РГ из файла
                String passedFilenameString = this.inputField.getText();
                FrameMain.loadGrammarFromFile(passedFilenameString);
            }
            case "название файла" -> { // Сохранить историю в файл
                String passedFilenameString = this.inputField.getText();
                FrameMain.saveHistoryToFile(passedFilenameString);
            }
        }
        dispose();
    }
}
