import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FrameMain extends Frame implements ActionListener {

    TextArea outputArea = new TextArea();
    TextField fromField = new TextField();
    TextField toField = new TextField();
    static FrameMain currentFrame;

    FrameMain() {
        currentFrame = this;
        createMenu();

        Label outputLabel = new Label("Результаты:");
        Label rangeLabel = new Label("Диапазон длин");
        Label fromLabel = new Label("От:");
        Label toLabel = new Label("До:");

        Button mainGenerationButton = new Button("Сгенерировать РВ по РГ и проверить");
        Button rgGenerationButton = new Button("Сгенерировать по РГ");
        Button rvGenerationButton = new Button("Сгенерировать по РВ");
        Button showRGButton = new Button("Показать РГ");
        Button showRVButton = new Button("Показать РВ");

        outputLabel.setBounds(10, 65, 150, 15);
        rangeLabel.setBounds(580, 440, 150, 15);
        fromLabel.setBounds(570, 475, 30, 15);
        toLabel.setBounds(570, 510, 30, 15);

        mainGenerationButton.setBounds(10, 500, 230, 30);
        rgGenerationButton.setBounds(250, 500, 150, 30);
        rvGenerationButton.setBounds(410, 500, 150, 30);

        showRGButton.setBounds(580, 85, 100, 40);
        showRVButton.setBounds(580, 135, 100, 40);

        outputArea.setBounds(10, 85, 550, 400);
        outputArea.setEditable(false);

        fromField.setBounds(600, 470, 50, 25);
        toField.setBounds(600, 505, 50, 25);

        this.setBackground(new Color(0x4EC25A));
        this.setForeground(new Color(0x2626CC));

        outputArea.setFont(new Font("Arial", Font.PLAIN, 16));

        mainGenerationButton.addActionListener(this);
        rgGenerationButton.addActionListener(this);
        rvGenerationButton.addActionListener(this);
        showRGButton.addActionListener(this);
        showRVButton.addActionListener(this);

        this.add(outputLabel);
        this.add(rangeLabel);
        this.add(fromLabel);
        this.add(toLabel);
        this.add(mainGenerationButton);
        this.add(rgGenerationButton);
        this.add(rvGenerationButton);
        this.add(showRVButton);
        this.add(showRGButton);
        this.add(outputArea);
        this.add(fromField);
        this.add(toField);

        this.setResizable(false);
        this.setBounds(600, 250, 700, 550);
        this.setTitle("Курсовая работа ТЯПиМТ 12 Вариант by Ruslan815");
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

    private void createMenu() {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Меню");
        MenuItem i1 = new MenuItem("Автор");
        MenuItem i2 = new MenuItem("Тема");
        Menu submenu = new Menu("Ввод РГ");
        MenuItem i31 = new MenuItem("Клавиатура");
        MenuItem i32 = new MenuItem("Файл");
        MenuItem i3 = new MenuItem("Ввод РВ");
        MenuItem i4 = new MenuItem("История");
        MenuItem i5 = new MenuItem("Записать историю в файл");
        MenuItem i6 = new MenuItem("Справка");
        menu.add(i1);
        menu.add(i2);
        menu.add(submenu);
        submenu.add(i31);
        submenu.add(i32);
        menu.add(i3);
        menu.add(i4);
        menu.add(i5);
        menu.add(i6);
        menuBar.add(menu);

        submenu.addActionListener(this);
        menu.addActionListener(this);

        this.setMenuBar(menuBar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        switch (e.getActionCommand()) {
            case "Автор":
                createTextFrame("Автор",
                        """
                                Разработчик:
                                Галактическая Нить Персея-Пегаса, комплекс сверхскоплений Рыб-Кита, Ланиакея,\s
                                сверхскопление Девы, Местная группа галактик, галактика Млечный Путь,\s
                                рукав Ориона, Солнечная система, планета Земля,
                                страна Российская Федерация, город Новосибирск, СибГУТИ,
                                факультет ИВТ 4 курс ИП-815 Сафонов Руслан""");
                break;
            case "Тема":
                createTextFrame("Тема", """
                        Написать программу, которая по заданной регулярной грамматике
                        (ЛЛ или ПЛ по желанию пользователя) построит эквивалентное регулярное выражение.
                        Программа должна сгенерировать по заданной грамматике и построенному регулярному выражению
                        множества всех цепочек в указанном диапазоне длин, проверить их на совпадение.
                        Процесс построения цепочек отображать на экране. Для подтверждения корректности
                        выполняемых действий предусмотреть возможность генерации цепочек по введённому пользователем
                        регулярному выражению. При обнаружении несовпадения в элементах множеств
                        должна выдаваться диагностика различий – где именно несовпадения и в чём они состоят.""");
                break;
            case "Клавиатура":
                FrameInputFromKeyboard inputRGFrame = new FrameInputFromKeyboard("РГ");
                break;
            case "Файл":
                FrameInputFromKeyboard inputRGFromFileFrame = new FrameInputFromKeyboard("путь к файлу");
                break;
            case "Ввод РВ":
                FrameInputFromKeyboard inputRVFrame = new FrameInputFromKeyboard("РВ");
                break;
            case "История":
                createTextFrame("История", Main.historyOfAll.toString());
                break;
            case "Записать историю в файл":
                FrameInputFromKeyboard saveHistoryToFileFrame = new FrameInputFromKeyboard("название файла");
                break;
            case "Справка":
                createTextFrame("Справка", """
                        Регулярная грамматика должна задаваться в следующем формате:
                        G = {acsxyz; S, A; S -> y | Ss | Aa, A -> c | Sx | Az; S}

                        Для корректной работы приложения в качестве целевого символа
                        указывайте заглавную латинскую букву S, и начинайте запись
                        функции переходов именно с этого нетерминала. Нетерминал -
                        всегда заглавная латинская буква. Терминал - всегда строчная
                        латинская буква(цифра). Нетерминал должен иметь хотя бы одно\s
                        правило. Грамматика должна быть полностью либо ЛЛ либо ПЛ.

                        Регулярное выражение должно задаваться в следующем формате:
                        (z(a+b)*(c+d))*

                        Используйте скобочки и строчные латинские буквы и цифры.
                        * - итерация
                        + - объединение
                        Рекомендуется итерацию одиночного терминала представлять
                        таким образом: (a)*""");
                break;
            case "Показать РГ":
                // Если грамматика задана
                if (Main.parsedGrammar != null && Main.isGrammarCorrect) {
                    createTextFrame("Текущая РГ", Main.parsedGrammar.toString());
                } else {
                    createTextFrame("Текущая РГ", "Регулярная Грамматика не задана");
                }
                break;
            case "Показать РВ":
                if (Main.someRegExp != null) {
                    createTextFrame("Текущее РВ", Main.someRegExp);
                } else {
                    createTextFrame("Текущее РВ", "Регулярное Выражение не задано");
                }
                break;
            case "Сгенерировать РВ по РГ и проверить":
                // Проверяем введённый диапазон длин цепочек
                try {
                    int startLen = Integer.parseInt(currentFrame.fromField.getText());
                    int endLen = Integer.parseInt(currentFrame.toField.getText());
                    if (startLen < 0 || endLen < 0 || startLen > endLen) throw new NumberFormatException();
                    GrammarGenerator.startLength = startLen;
                    GrammarGenerator.endLength = endLen;
                    Main.startLen = startLen;
                    Main.endLen = endLen;
                } catch (NumberFormatException exception) {
                    currentFrame.outputArea.setText("Введён некорректный диапазон длин цепочек!");
                    return;
                }

                // Если диапазон корретный, то переходим к генерации
                // TODO Генерация
                break;
            case "Сгенерировать по РГ":
                // Проверяем введённый диапазон длин цепочек
                try {
                    int startLen = Integer.parseInt(currentFrame.fromField.getText());
                    int endLen = Integer.parseInt(currentFrame.toField.getText());
                    if (startLen < 0 || endLen < 0 || startLen > endLen) throw new NumberFormatException();
                    GrammarGenerator.startLength = startLen;
                    GrammarGenerator.endLength = endLen;
                    Main.startLen = startLen;
                    Main.endLen = endLen;
                } catch (NumberFormatException exception) {
                    currentFrame.outputArea.setText("Введён некорректный диапазон длин цепочек!");
                    return;
                }

                // Если диапазон корретный, то переходим к генерации
                Main.generateChainsByRG();
                break;
            case "Сгенерировать по РВ":
                // Проверяем введённый диапазон длин цепочек
                try {
                    int startLen = Integer.parseInt(currentFrame.fromField.getText());
                    int endLen = Integer.parseInt(currentFrame.toField.getText());
                    if (startLen < 0 || endLen < 0 || startLen > endLen) throw new NumberFormatException();
                    Main.startLen = startLen;
                    Main.endLen = endLen;
                } catch (NumberFormatException exception) {
                    currentFrame.outputArea.setText("Введён некорректный диапазон длин цепочек!");
                    return;
                }

                // Если диапазон корретный, то переходим к генерации
                Main.generateChainsByRV();
                break;
            default:
                System.err.println("Неизвестное событие");
        }
    }

    public static void setInputOfGrammar(String inputOfGrammar, String grammarType) {
        Grammar parsedGrammar;
        try {
            GrammarGenerator.grammar = inputOfGrammar;
            parsedGrammar = InputAndValidate.parseGrammar();
        } catch (Exception someException) {
            Main.isGrammarCorrect = false;
            currentFrame.outputArea.setText("Ошибка парсинга грамматики! Неверный формат!\n" + someException.getMessage());
            return;
        }
        InputAndValidate.fillMapOfRules(parsedGrammar.getNonTerminals(), parsedGrammar.getRules());
        Main.parsedGrammar = parsedGrammar;
        Main.grammarType = grammarType;
        Main.isGrammarCorrect = true;
    }

    public static void setInputOfRegExp(String regExpString) {
        Main.someRegExp = regExpString;
    }

    public static void loadGrammarFromFile(String filename) {
        try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }

            String[] everything = sb.toString().split(System.lineSeparator());
            FrameMain.setInputOfGrammar(everything[1], everything[0]);

        } catch (FileNotFoundException e) {
            currentFrame.outputArea.setText("Файл " + filename + " не найден!"); //e.printStackTrace();
        } catch (IOException exception) {
            currentFrame.outputArea.setText("Ошибка чтения из файла " + filename + "!"); //exception.printStackTrace();
        }
    }

    public static void saveHistoryToFile(String filename) {
        List<String> list = new LinkedList<>();
        list.add(Main.historyOfAll.toString());
        Path file = Paths.get(filename);
        try {
            Files.write(file, list, StandardCharsets.UTF_8);
        } catch (IOException e) {
            currentFrame.outputArea.setText("Ошибка записи истории в файл " + filename + "!"); //e.printStackTrace();
        }
    }

    /*public void createShowRGFrame() {
        Frame infoFrame = new Frame();
        TextArea infoTextArea = new TextArea();
        StringBuilder infoStringBuilder = new StringBuilder();
        infoStringBuilder.append("Начальное состояние: ").append(Lab4.MPP_Grammar.getStartState()).append("\n");
        infoStringBuilder.append("Множество конечных состояний: ").append(Arrays.toString(Lab4.MPP_Grammar.getEndStates())).append("\n");
        for (String[] rows : Lab4.MPP_transitionFunction) {
            for (int i = 0; i < 5; i++) {
                String element = rows[i];
                if (i == 2) {
                    infoStringBuilder.append(element).append(" -> ");
                } else if (i != 4) {
                    infoStringBuilder.append(element).append(", ");
                } else {
                    infoStringBuilder.append(element);
                }
            }
            infoStringBuilder.append("\n");
        }
        infoTextArea.setText(infoStringBuilder.toString());
        infoFrame.setForeground(new Color(0x6F0AA1));
        infoFrame.add(infoTextArea);
        infoFrame.setBounds(600, 250, 600, 390);
        infoFrame.setTitle("Текущий МПП");
        infoFrame.setVisible(true);
        infoFrame.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent we) {
                        infoFrame.dispose();
                    }
                }
        );
    }

    public void displayHistory() {
        Frame infoFrame = new Frame();
        TextArea infoTextArea = new TextArea();
        StringBuilder infoStringBuilder = new StringBuilder();
        infoStringBuilder.append("История генерации цепочек:\n");
        for (int i = 0; i < Lab4.outputHistory.size(); i++) {
            infoStringBuilder.append(i).append(") ").append(Lab4.outputHistory.get(i)).append("\n");
        }
        infoTextArea.setText(infoStringBuilder.toString());
        infoFrame.setForeground(new Color(0x6F0AA1));
        infoFrame.add(infoTextArea);
        infoFrame.setBounds(600, 250, 600, 390);
        infoFrame.setTitle("История");
        infoFrame.setVisible(true);
        infoFrame.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent we) {
                        infoFrame.dispose();
                    }
                }
        );
    }*/

    public void createTextFrame(String title, String someText) {
        Frame infoFrame = new Frame();
        TextArea infoTextArea = new TextArea();
        infoTextArea.setText(someText);
        infoFrame.setBackground(new Color(0x4EC25A));
        infoFrame.setForeground(new Color(0x2626CC));
        infoFrame.add(infoTextArea);
        infoFrame.setBounds(600, 250, 600, 390);
        infoFrame.setTitle(title);
        infoFrame.setVisible(true);
        infoFrame.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent we) {
                        infoFrame.dispose();
                    }
                }
        );
    }

    public static void main(String[] args) {
        FrameMain frameMain = new FrameMain();
    }
}
