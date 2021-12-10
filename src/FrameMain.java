import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class FrameMain extends Frame implements ActionListener {

    TextArea outputArea = new TextArea();
    TextField fromField = new TextField();
    TextField toField = new TextField();

    FrameMain() {
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
        this.setTitle("Курсовая работа ТЯПиМТ");
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
                // Форма для ввода с клавиатуры (textArea)
                break;
            case "Файл":
                // Диалог выбора файла
                break;
            case "Ввод РВ":
                // Форма для ввода РВ с клавиатуры (textField)
                break;
            case "История":
                createTextFrame("История", "Я история");
                break;
            case "Записать историю в файл":
                // Получить всю историю и записать в файл history.txt
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
                createTextFrame("Текущая РГ", "Я РГ");
                break;
            case "Показать РВ":
                createTextFrame("Текущее РВ", "Я РВ");
                break;
            case "Сгенерировать РВ по РГ и проверить":
                break;
            case "Сгенерировать по РГ":
                break;
            case "Сгенерировать по РВ":
                break;
            default:
                System.err.println("Неизвестное событие");
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
