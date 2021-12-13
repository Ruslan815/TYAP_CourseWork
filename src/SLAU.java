import java.util.*;

public class SLAU {

    static String[] nonTerminals;
    static String[][] matrix;
    static String[] result;

    public static void forwardRun() {
        // Идём по всем уравнениям (строкам)
        for (int currentNTIndex = 1; currentNTIndex < matrix.length; currentNTIndex++) {
            StringBuilder alfaSb = new StringBuilder();
            StringBuilder betaSb = new StringBuilder();
            String currentNonTerminal = nonTerminals[currentNTIndex];

            // Записали в альфу коэффициент при самом нетерминале(A[i][i]), если он есть
            String Aii = matrix[currentNTIndex][currentNTIndex];
            if (!Aii.isEmpty()) {
                alfaSb.append(Aii.substring(0, Aii.length() - 1)).append("+");
            }

            // Свободный член записываем в бету, если он есть
            if (!matrix[currentNTIndex][0].isEmpty()) {
                betaSb.append(matrix[currentNTIndex][0]).append("+");
            }

            // Добавляем в бету коэфф. после A[i][i], если они есть
            for (int i = currentNTIndex + 1; i < matrix[currentNTIndex].length; i++) {
                if (!matrix[currentNTIndex][i].isEmpty()) {
                    betaSb.append(matrix[currentNTIndex][i]).append("+");
                }
            }

            // Подставляем вычисленные ранее значения в многочлены
            for (int i = 1; i < currentNTIndex; i++) {
                if (matrix[currentNTIndex][i].isEmpty()) continue; // Пропускаем пустые правила

                List<String> coeffsList = new LinkedList<>();
                ExpressionParser parser = new ExpressionParser();
                coeffsList = parser.solve(matrix[currentNTIndex][i]);

                // Распределяем полученные коэфф. на Альфа и Бета
                for (String someStr: coeffsList) {
                    if (someStr.contains(currentNonTerminal)) {
                        alfaSb.append(someStr.substring(0, someStr.length() - 1)).append("+");
                    } else {
                        betaSb.append(someStr).append("+");
                    }
                }
                /*System.out.println("PARSED:");
                for (String someStr: coeffsList) {
                    System.out.println(someStr);
                }*/
            }

            // Удаляем лишний последний плюс
            if (alfaSb.length() != 0) { // TODO isEmpty
                alfaSb.delete(alfaSb.length() - 1, alfaSb.length());
            }
            if (betaSb.length() != 0) { // TODO isEmpty
                betaSb.delete(betaSb.length() - 1, betaSb.length());
            }

            System.out.println("ALFA");
            System.out.println(alfaSb);

            System.out.println("BETA");
            System.out.println(betaSb);

            // Записываем в Result = alfa*beta
            if (alfaSb.length() != 0 && betaSb.length() != 0) { // a*b // TODO isEmpty
                result[currentNTIndex] = "((" + alfaSb + ")*(" + betaSb + "))";
            } else if (alfaSb.length() == 0 && betaSb.length() != 0) { // b
                result[currentNTIndex] = "(" + betaSb + ")";
            } else if (alfaSb.length() != 0 && betaSb.length() == 0) { // a*
                result[currentNTIndex] = "(" + alfaSb + ")*";
            }

            // Подставляем вычисленное уравнение в уравнения ниже
            for (int i = currentNTIndex + 1; i < matrix.length; i++) {
                for (int j = 1; j < matrix[i].length; j++) {
                    if (matrix[i][j].contains(currentNonTerminal)) {
                        matrix[i][j] = matrix[i][j].replace(currentNonTerminal, result[currentNTIndex]);
                    }
                }
            }
        }
    }

    public static void reverseRun() {
        for (int currentNTIndex = matrix.length - 1; currentNTIndex >= 1; currentNTIndex--) {
            for (int i = currentNTIndex + 1; i < matrix.length; i++) {
                if (result[currentNTIndex].contains(nonTerminals[i])) {
                    result[currentNTIndex] = result[currentNTIndex].replace(nonTerminals[i], result[i]);
                }
            }
        }
        //System.out.println("SLAU: RESULT: " + Arrays.toString(result));
    }

    public static void main(String[] args) {
        /*String[][] LLG2 = {
                {"", "", ""},
                {"y", "Ss", "A(a+k)"},
                {"c", "Sx", "Az"}
        };


        ConvertLLGtoRLG converter = new ConvertLLGtoRLG(LLG);
        nonTerminals = converter.getNonTerminals();
        matrix = converter.getResult();
        result = new String[matrix.length];
        Arrays.fill(result, "");*/

        String[][] RLG2 = {
                {"", "", ""},
                {"a", "A", "cB"},
                {"d", "eA", "fB"}
        };

        nonTerminals = new String[]{"", "S", "A", "B"};
        matrix = RLG2;
        result = new String[matrix.length];
        Arrays.fill(result, "");

        System.out.println("START MATRIX");
        for (String[] arr : matrix) {
            for (String str : arr) {
                System.out.print(str + " : ");
            }
            System.out.println();
        } System.out.println();

        forwardRun();
        System.out.println("FORWARD MATRIX");
        for (String[] arr : matrix) {
            for (String str : arr) {
                System.out.print(str + " : ");
            }
            System.out.println();
        } System.out.println();

        System.out.println("RESULT");
        for (String str : result) {
            System.out.println(str);
        } System.out.println();

        reverseRun();
        System.out.println("REVERSE MATRIX");
        for (String[] arr : matrix) {
            for (String str : arr) {
                System.out.print(str + " : ");
            }
            System.out.println();
        } System.out.println();

        System.out.println("RESULT");
        for (String str : result) {
            System.out.println(str);
        } System.out.println();
    }
}
