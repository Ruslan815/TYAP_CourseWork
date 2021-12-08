import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static String grammarType; // L or R
    public static boolean readFromFile = false; // Read data from file or from form

    public static void inputDataFromFile() {

    }

    public static void inputDataFromForm() {

    }

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

    public static void main(String[] args) {

    }
}
