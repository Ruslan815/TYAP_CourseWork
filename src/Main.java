import java.io.IOException;
import java.util.*;

public class Main {
    public static boolean readFromFile = false; // Read data from file or from form

    public static String grammarType; // L or R
    static Grammar parsedGrammar;
    static String someRegExp;
    static int startLen;
    static int endLen;

    static String[] nonTerminals;
    static String[][] matrix;

    static boolean isGrammarCorrect = false; // На случай если произошла ошибка при парсинге

    static StringBuilder historyOfAll = new StringBuilder();

    public static void generateRegExpByGrammar() {
        if (!isGrammarCorrect) {
            FrameMain.currentFrame.outputArea.setText("Регулярная Грамматика не задана!");
            return;
        }

        convertGrammarToMatrix();

        if (grammarType.equals("ЛЛГ")) {
            // Преобразовали в ПЛГ
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

        // Генерируем цепочки по РГ и РВ
        List<String> generatedChainsOfRG = generateChainsByRG();
        List<String> generatedChainsOfRV = generateChainsByRV();

        // Проверяем на совпадение цепочки из РГ в РВ
        for (String tempChain : generatedChainsOfRG) {
            if (!generatedChainsOfRV.contains(tempChain)) {
                FrameMain.currentFrame.outputArea.setText("Цепочки не совпали.\nРегулярная грамматика вывела цепочку: " + tempChain);
                return;
            }
        }

        // Проверяем на совпадение цепочки из РВ в РГ
        for (String tempChain : generatedChainsOfRV) {
            if (!generatedChainsOfRG.contains(tempChain)) {
                FrameMain.currentFrame.outputArea.setText("Цепочки не совпали.\nРегулярное выражение вывело цепочку: " + tempChain);
                return;
            }
        }

        // Если цепочки совпали
        FrameMain.currentFrame.outputArea.setText("Цепочки совпали.");
    }

    private static void convertGrammarToMatrix() {
        Set<String> setOfNT = GrammarGenerator.mapOfRules.keySet();
        List<String> listOfNT = new LinkedList<>(setOfNT);
        Map<String, String[]> mapOfRules = new HashMap<>(Map.copyOf(GrammarGenerator.mapOfRules));

        // Создаём массив с номерами нетерминалов и их названиями
        nonTerminals = new String[mapOfRules.size() + 1]; // Плюс пустая нулевая строка
        Arrays.fill(nonTerminals, "");
        nonTerminals[1] = "S";
        for (int i = 2; i < nonTerminals.length; i++) {
            nonTerminals[i] = String.valueOf(Character.toChars('A' + (i - 2)));
        }

        int indexOfNT = 2; // Начинаем с NT = "A"

        // Проходимся по всем NT и меняем их названия на порядковые
        for (String someNT : listOfNT) {
            if (someNT.equals("S")) continue;

            String newCurrentNT = nonTerminals[indexOfNT++]; // Берём новый текущий NT

            // Проходимся по всем NT
            for (String currNT : listOfNT) {
                // Проходимся по всем правилам текущего NT и меняем someNT на порядковый NT (A, B, C, ...)
                String[] currentRules = mapOfRules.get(currNT); // Получаем все правила для текущего NT
                for (int i = 0; i < currentRules.length; i++) {
                    if (currentRules[i].contains(someNT)) { // Если правило содержит текущий NT, то заменяем его на порядковый
                        currentRules[i] = currentRules[i].replace(someNT, newCurrentNT);
                    }
                }
                mapOfRules.put(currNT, currentRules); // Записываем новые правила
            }

            // И меняем название самомого NT в мапе
            String[] currentRules = mapOfRules.get(someNT);
            mapOfRules.remove(someNT);
            mapOfRules.put(newCurrentNT, currentRules);
        }

        matrix = new String[nonTerminals.length][nonTerminals.length];

        for (String[] row: matrix) // Заполняем матрицу пустотой
            Arrays.fill(row, "");

        // Формируем матрицу уравнений
        for (int i = 1; i < nonTerminals.length; i++) {
            String currentNT = nonTerminals[i]; // Выбираем текущую строку

            // Считаем коэффициенты для столбца свободных членов
            // Считаем коэффициенты для каждого столца нетерминала если он есть
        }
    }

    // Генерирует цепочки по текущей Регулярной Грамматике
    public static List<String> generateChainsByRG() {
        if (!isGrammarCorrect) {
            FrameMain.currentFrame.outputArea.setText("Регулярная Грамматика не задана!");
            return null;
        }

        // Очищаем историю вывода
        GrammarGenerator.stepCounter = 0;
        GrammarGenerator.listOfRuleChain = new LinkedList<>();
        GrammarGenerator.outputHistoryList = new LinkedList<>();
        GrammarGenerator.resultList = new LinkedList<>();

        // Генерируем
        GrammarGenerator.generateLanguageChains(parsedGrammar.getStartRule(), parsedGrammar.getStartRule(), 0);

        List<String> generatedChains = GrammarGenerator.resultList;
        List<List<String>> historyOfGeneration = GrammarGenerator.outputHistoryList;

        // Выводим результаты генерации в форму
        StringBuilder outputAreaText = new StringBuilder("Регулярная грамматика\nСгенерированные цепочки:\n" + generatedChains + "\nПроцесс вывода цепочек:\n");
        for (List<String> list : historyOfGeneration) {
            outputAreaText.append(list).append("\n");
        }
        FrameMain.currentFrame.outputArea.setText(outputAreaText.toString());

        // Записываем результаты генерации в историю
        historyOfAll.append(outputAreaText).append("\n\n");

        return generatedChains;
    }

    // Генерирует цепочки по текущему Регулярному Выражению
    public static List<String> generateChainsByRV() {
        if (someRegExp == null || someRegExp.isEmpty()) {
            FrameMain.currentFrame.outputArea.setText("Регулярное Выражение не задано!");
            return null;
        }

        // Очищаем историю вывода
        RegExpGenerator.setOutputList(new LinkedList<>());
        RegExpGenerator regExpGenerator = new RegExpGenerator();

        // Генерируем
        List<String> generatedChains = regExpGenerator.solve(someRegExp, startLen, endLen);
        List<List<String>> historyOfGeneration = RegExpGenerator.getOutputList();

        // Выводим результаты генерации в форму
        StringBuilder outputAreaText = new StringBuilder("Регулярное выражение\nСгенерированные цепочки:\n" + generatedChains + "\nПроцесс вывода цепочек:\n");
        for (List<String> list : historyOfGeneration) {
            outputAreaText.append(list).append("\n");
        }
        FrameMain.currentFrame.outputArea.setText(outputAreaText.toString());

        // Записываем результаты генерации в историю
        historyOfAll.append(outputAreaText).append("\n\n");

        return generatedChains;
    }

    public static void main(String[] args) {

    }
}
