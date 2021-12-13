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
    static String[] result;

    static boolean isGrammarCorrect = false; // На случай если произошла ошибка при парсинге

    static StringBuilder historyOfAll = new StringBuilder();

    public static void generateRegExpByGrammar() {
        if (!isGrammarCorrect) {
            FrameMain.currentFrame.outputArea.setText("Регулярная Грамматика не задана!");
            return;
        }

        convertGrammarToMatrix();

        // Преобразовали в ПЛГ
        if (grammarType.equals("ЛЛГ")) {
            ConvertLLGtoRLG converter = new ConvertLLGtoRLG(matrix);
            nonTerminals = converter.getNonTerminals();
            matrix = converter.getResult();
        }
        result = new String[matrix.length];
        Arrays.fill(result, "");

        // Находим решение системы уравнений с регулярными коэффициентами
        SLAU.nonTerminals = nonTerminals.clone();
        SLAU.matrix = matrix.clone();
        SLAU.result = result.clone();
        Arrays.fill(SLAU.result, "");

        SLAU.forwardRun();
        SLAU.reverseRun();

        // Берём полученное регулярное выражение для целевого символа S
        someRegExp = SLAU.result[1];

        // Генерируем цепочки по РГ и РВ
        List<String> generatedChainsOfRG = generateChainsByRG();
        List<String> generatedChainsOfRV = generateChainsByRV();

        // Проверяем на совпадение цепочки из РГ в РВ
        for (String tempChain : generatedChainsOfRG) {
            if (!generatedChainsOfRV.contains(tempChain)) {
                FrameMain.currentFrame.outputArea.append("Цепочки не совпали.\nРегулярная грамматика вывела цепочку: " + tempChain);
                return;
            }
        }

        // Проверяем на совпадение цепочки из РВ в РГ
        for (String tempChain : generatedChainsOfRV) {
            if (!generatedChainsOfRG.contains(tempChain)) {
                FrameMain.currentFrame.outputArea.append("Цепочки не совпали.\nРегулярное выражение вывело цепочку: " + tempChain);
                return;
            }
        }

        // Если цепочки совпали
        FrameMain.currentFrame.outputArea.append("Цепочки совпали.");
    }

    private static void convertGrammarToMatrix() {
        List<String> listOfNT = new LinkedList<>(); // GrammarGenerator.mapOfRules.keySet()
        for (String elTempoStr : GrammarGenerator.mapOfRules.keySet()) {
            listOfNT.add(elTempoStr);
        }

        Map<String, String[]> mapOfRules = new HashMap<>();
        for (Map.Entry<String, String[]> entry : GrammarGenerator.mapOfRules.entrySet()) { // Копируем
            String[] tempRulesArray = new String[entry.getValue().length];
            for (int ii = 0; ii < entry.getValue().length; ii++) {
                tempRulesArray[ii] = entry.getValue()[ii];
            }
            mapOfRules.put(entry.getKey(), tempRulesArray);
        }

        /*System.out.println("BEFORE");
        for (Map.Entry<String, String[]> entry : mapOfRules.entrySet()) {
            System.out.println(entry.getKey() + ":" + Arrays.toString(entry.getValue()));
        }
        System.out.println("BEFORE");
        for (Map.Entry<String, String[]> entry : GrammarGenerator.mapOfRules.entrySet()) {
            System.out.println(entry.getKey() + ":" + Arrays.toString(entry.getValue()));
        }*/

        // Создаём массив с номерами нетерминалов и их названиями
        nonTerminals = new String[mapOfRules.size() + 1]; // Плюс пустая нулевая строка
        Arrays.fill(nonTerminals, "");
        nonTerminals[1] = "S";
        for (int i = 2; i < nonTerminals.length; i++) {
            nonTerminals[i] = String.valueOf(Character.toChars('A' + (i - 2)));
        }

        int indexOfNT = 2; // Начинаем с NT = "A"
        List<String> newListOfNT = new ArrayList<>(listOfNT); // TODO copyOf

        // Проходимся по всем NT и меняем их названия на порядковые
        for (String someNT : listOfNT) {
            if (someNT.equals("S")) continue; // Пропускаем целевой символ

            String newCurrentNT = nonTerminals[indexOfNT++]; // Берём новый текущий NT

            // Проходимся по всем NT
            for (String currNT : newListOfNT) {
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

            // Меняем в новом списке NT
            newListOfNT.remove(someNT); // Удаляем старый
            newListOfNT.add(newCurrentNT); // Добавляем новый
        }

        /*System.out.println("AFTER");
        for (Map.Entry<String, String[]> entry : mapOfRules.entrySet()) {
            System.out.println(entry.getKey() + ":" + Arrays.toString(entry.getValue()));
        }*/

        matrix = new String[nonTerminals.length][nonTerminals.length];
        Map<String, Integer> mapOfNTIndexes = new HashMap<>();
        for (int i = 1; i < nonTerminals.length; i++) {
            mapOfNTIndexes.put(nonTerminals[i], i);
        }

        for (String[] row: matrix) // Заполняем матрицу пустотой
            Arrays.fill(row, "");

        // Формируем матрицу уравнений
        for (int i = 1; i < nonTerminals.length; i++) { // Идём по строкам
            String currentNT = nonTerminals[i]; // Выбираем текущую строку

            String[] rulesOfCurrentNT = mapOfRules.get(currentNT); // Все правила текущего NT

            boolean isContains = false;

            for (String tempRule : rulesOfCurrentNT) { // Проходимся по всем правилам NT
                isContains = false;
                for (int j = 1; j < nonTerminals.length; j++) { // Ищем в правилах какой-нибудь NT
                    if (tempRule.contains(nonTerminals[j])) {
                        isContains = true;
                        if (grammarType.equals("ПЛГ")) {
                            matrix[i][j] += tempRule.substring(0, tempRule.length() - 1) + "+";
                        } else if (grammarType.equals("ЛЛГ")) {
                            matrix[i][j] += tempRule.substring(1) + "+";
                        }
                        break;
                    }
                }
                if (!isContains) { // Если правило не собедержит NT, то оно состоит из свободных членов
                    matrix[i][0] += tempRule + "+";
                }
            }

            // Получили строки с коэфф перечисленными через "+"
            // Возьмём коэфф в скобки и уберём лишние "+" и припишем NT, где надо
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j].isEmpty()) continue; // Пропускаем пустые строки
                matrix[i][j] = matrix[i][j].substring(0, matrix[i][j].length() - 1); // Удаляем лишний "+"

                if (matrix[i][j].contains("+")) { // Если в строке 2 коэфф и больше, то объединим их в скобки
                    matrix[i][j] = "(" + matrix[i][j] + ")";
                }

                // И добавляем NT в нужный столбец
                if (j != 0) {
                    if (grammarType.equals("ПЛГ")) {
                        matrix[i][j] += nonTerminals[j];
                    } else if (grammarType.equals("ЛЛГ")) {
                        matrix[i][j] = nonTerminals[j] + matrix[i][j];
                    }
                }
            }
        }

        /*System.out.println("MATRIX");
        for (String[] arr : matrix) {
            for (String str : arr) {
                System.out.print(str + " : ");
            }
            System.out.println();
        } System.out.println();*/
        /*System.out.println("AFTER");
        for (Map.Entry<String, String[]> entry : mapOfRules.entrySet()) {
            System.out.println(entry.getKey() + ":" + Arrays.toString(entry.getValue()));
        }
        System.out.println("AFTER");
        for (Map.Entry<String, String[]> entry : GrammarGenerator.mapOfRules.entrySet()) {
            System.out.println(entry.getKey() + ":" + Arrays.toString(entry.getValue()));
        }*/
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
        StringBuilder outputAreaText = new StringBuilder("\nРегулярная грамматика\nСгенерированные цепочки:\n" + generatedChains + "\nПроцесс вывода цепочек:\n");
        for (List<String> list : historyOfGeneration) {
            outputAreaText.append(list).append("\n");
        }
        FrameMain.currentFrame.outputArea.append(outputAreaText.toString());

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
        StringBuilder outputAreaText = new StringBuilder("\nРегулярное выражение\nСгенерированные цепочки:\n" + generatedChains + "\nПроцесс вывода цепочек:\n");
        for (List<String> list : historyOfGeneration) {
            outputAreaText.append(list).append("\n");
        }
        FrameMain.currentFrame.outputArea.append(outputAreaText.toString());

        // Записываем результаты генерации в историю
        historyOfAll.append(outputAreaText).append("\n\n");

        return generatedChains;
    }
}
