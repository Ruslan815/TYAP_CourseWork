import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static boolean readFromFile = false; // Read data from file or from form

    public static String grammarType; // L or R
    static Grammar parsedGrammar;
    static String someRegExp;
    static int startLen;
    static int endLen;

    public static void generateRegExpByGrammar() {
        if (grammarType.equals("L")) {
            // Преобразовали в ПЛ
            String[][] systemOfGrammar = null; // Заполнить систему уравнений
            ConvertLLGtoRLG converter = new ConvertLLGtoRLG(systemOfGrammar);
        }

        String[][] RLG3 = { // TODO Заменить на systemOfGrammar
                {"", "", "", ""},
                {"a", "bS", "", "dB"},
                {"e", "fS", "gA", ""},
                {"i", "kS", "", "zB"},
        };

        // Находим решение системы уравнений с регулярными коэффициентами
        SLAU.nonTerminals = new String[]{"", "S", "A", "B"};
        SLAU.matrix = RLG3;
        SLAU.result = new String[SLAU.matrix.length];
        Arrays.fill(SLAU.result, "");
        SLAU.forwardRun();
        SLAU.reverseRun();

        // Берём полученное регулярное выражение для целевого символа S
        String resultRegExp = SLAU.result[0];

        // Генерируем цепочки по регулярному выражению
        RegExpGenerator generator = new RegExpGenerator();
        List<String> generatedRegExp = generator.solve(resultRegExp, 0, 3);

        // Генерируем цепочки по регулярной грамматике
        try {
            InputAndValidate.inputData();
        } catch (IOException e) {
            System.err.println("Input data error!"); //e.printStackTrace();
            return;
        }

        Grammar parsedGrammar;
        try {
            parsedGrammar = InputAndValidate.parseGrammar();
            System.out.println(parsedGrammar);
        } catch (Exception e) {
            System.err.println("Grammar parsing Exception!"); //e.printStackTrace();
            return;
        }
        System.out.println("Grammar after parsing:\n" + parsedGrammar);

        InputAndValidate.fillMapOfRules(parsedGrammar.getNonTerminals(), parsedGrammar.getRules()); //displayMapOfRules();
        GrammarGenerator.generateLanguageChains(parsedGrammar.getStartRule(), parsedGrammar.getStartRule(), 0);

        // Записываем результат генерации обоих генераторов и сравниваем их
        /*for (String str: resultList1) {
            if (!resultList2.contains(str)) {
                System.out.println("Расхождение в цепочке: " + str);
            }
        }*/
    }

    // Генерирует цепочки по текущей Регулярной Грамматике
    public static void generateChainsByRG() {
        if (parsedGrammar == null) {
            FrameMain.currentFrame.outputArea.setText("Регулярная Грамматика не задана!");
            return;
        }

        // Очищаем историю вывода
        GrammarGenerator.stepCounter = 0;
        GrammarGenerator.listOfRuleChain = new LinkedList<>();
        GrammarGenerator.outputList = new LinkedList<>();
        GrammarGenerator.resultList = new LinkedList<>();

        GrammarGenerator.generateLanguageChains(parsedGrammar.getStartRule(), parsedGrammar.getStartRule(), 0);

        List<String> generatedChains = GrammarGenerator.resultList;
        List<List<String>> historyOfGeneration = GrammarGenerator.outputList;
    }

    // Генерирует цепочки по текущему Регулярному Выражению
    public static void generateChainsByRV() {
        if (someRegExp.isEmpty()) {
            FrameMain.currentFrame.outputArea.setText("Регулярное Выражение не задано!");
            return;
        }

        RegExpGenerator.setOutputList(new LinkedList<>()); // Очистили историю вывода
        RegExpGenerator regExpGenerator = new RegExpGenerator();

        List<String> generatedChains = regExpGenerator.solve(someRegExp, startLen, endLen);
        List<List<String>> historyOfGeneration = RegExpGenerator.getOutputList();
    }

    public static void main(String[] args) {

    }
}
