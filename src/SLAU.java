import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SLAU {
    static String[][] matrix = {
            {"", "", ""},
            {"a", "bA", "cB"},
            {"d", "eA", "fB"}
    };

    static String[][] matrix2;

    static String[][] coeffs = {
            {"", "", ""},
            {"", "", ""}, // После первого уравнения
            {"", "", ""} // После второго уравнения и т.д.
    };

    static String[][] alfaBeta = {
            {"", ""},
            {"", ""},
            {"", ""}
    };

    static String[] nonTerminals = {"_", "A", "B"};

    static Map<String, Integer> indexesOfNonTerminalsMap = new HashMap<>();

    public static void forwardRun() {
        // Решаем уравнение для первой строки
        int currentNTIndex = 1; // indexesOfNonTerminalsMap.get(currentNonTerminal);
        String currentNonTerminal = nonTerminals[currentNTIndex];
        String alfa = matrix[currentNTIndex][currentNTIndex].substring(0, matrix[currentNTIndex][currentNTIndex].length() - 1); // alfa = b
        StringBuilder beta = new StringBuilder();
        for (int i = 0; i < matrix[currentNTIndex].length; i++) { // beta = a + cB
            if (i == currentNTIndex) continue;

            if (!matrix[currentNTIndex][i].isEmpty()) {
                beta.append(matrix[currentNTIndex][i]).append("+");
            }
        }
        beta.delete(beta.length() - 1, beta.length());

        // TODO Если Альфа пустая (подправить формат хранения/записи)
        alfaBeta[currentNTIndex][0] = "(" + alfa + ")";
        alfaBeta[currentNTIndex][1] = "(" + beta + ")";
        System.out.println("alfa: " + alfa);
        System.out.println("beta: " + beta);

        String resultRule = "(" + alfa + ")*(" + beta + ")";
        // Подставили на место нетерминала А полученное выражение a*b во все правила
        /*for (int i = currentNTIndex + 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[i].length; j++) {
                matrix[i][j] = matrix[i][j].replace(currentNonTerminal, resultRule);
            }
        }*/

        coeffs[currentNTIndex][0] = alfa + "*" + matrix[currentNTIndex][0];
        for (int i = currentNTIndex + 1; i < coeffs[currentNTIndex].length; i++) { // Треугольная матрица СТРОГО, не надо считать коэффициенты для уже посчитанных уравнений!
            if (matrix[currentNTIndex][i].isEmpty()) continue;
            String resStr = alfa + "*" + matrix[currentNTIndex][i];
            coeffs[currentNTIndex][i] = resStr.substring(0, resStr.length() - 1);
        }

        matrix2 = new String[matrix.length][matrix[0].length];
        for (int x = 0; x < matrix.length; x++) {
            System.arraycopy(matrix[x], 0, matrix2[x], 0, matrix[x].length);
        }

        StringBuilder alfaSb = new StringBuilder();
        // Идём по всем уравнениям (строкам)
        for (currentNTIndex = 2; currentNTIndex < matrix.length; currentNTIndex++) {
            currentNonTerminal = nonTerminals[currentNTIndex];

            // Записали в альфу коэффициент при самом нетерминале, если он есть
            String Aii = matrix[currentNTIndex][currentNTIndex];
            if (!Aii.isEmpty()) {
                alfaSb.append(Aii.substring(0, Aii.length() - 1)).append("+");
            }

            // Идём по всем правилам ДО ТЕКУЩЕГО ИНДЕКСА, подсталяем a*b(раскрываем правила) и ищем коэффициент при текущем нетерминале
            for (int i = 1; i < currentNTIndex; i++) {
                if (matrix[currentNTIndex][i].isEmpty()) continue; // Пропускаем пустые правила

                // Записываем коэффициент при правиле на случай, если получится раскрыть правило
                StringBuilder tempCoeff = new StringBuilder(matrix[currentNTIndex][i].substring(0, matrix[currentNTIndex][i].length() - 1));
                int tempIndex = i; // Индекс текущего РАСКРЫВАЕМОГО нетерминала
                String tempNT = nonTerminals[i]; // Текущего РАСКРЫВАЕМЫЙ нетерминал
                List<String> listOfNT = new LinkedList<>(); // Последовательность раскрытия нетерминалов
                listOfNT.add(tempNT);
                boolean isOk = false;

                while (true) { // tempIndex < currentNTIndex && containNT(matrix[currentNTIndex][tempIndex])
                    matrix[currentNTIndex][tempIndex] = matrix[currentNTIndex][tempIndex].replace(tempNT, alfaBeta[tempIndex][0] + "*" + alfaBeta[tempIndex][1]);

                    if (matrix[currentNTIndex][tempIndex].contains(currentNonTerminal)) {
                        isOk = true;
                        listOfNT.add(currentNonTerminal);
                        break; // OK
                    }

                    if (!containNT(matrix[currentNTIndex][tempIndex])) { // Нашли правило, которое не содержит NT
                        break;
                    }

                    if (getNT(matrix[currentNTIndex][tempIndex]).equals("")) { // Не нашли NT в правиле
                        System.err.println("ERROR!");
                        System.exit(777);
                    }

                    if (indexesOfNonTerminalsMap.get(getNT(matrix[currentNTIndex][tempIndex])) > tempIndex) { // Нашли правило с нерешённым NT
                        break;
                    }

                    // Продолжаем раскрывать правила
                    tempIndex = indexesOfNonTerminalsMap.get(getNT(matrix[currentNTIndex][tempIndex]));
                    tempNT = getNT(matrix[currentNTIndex][tempIndex]);
                    listOfNT.add(tempNT);
                }

                if (isOk) {
                    String currNT = listOfNT.get(0);
                    listOfNT.remove(0);

                    // Проходимся по пути вывода правила и добавляем коэффициенты
                    for (String s : listOfNT) {
                        String prevNT = currNT;
                        currNT = s;
                        tempCoeff.append(coeffs[indexesOfNonTerminalsMap.get(prevNT)][indexesOfNonTerminalsMap.get(currNT)]);
                    }

                    alfaSb.append(tempCoeff).append("+");
                }
            }

            alfaSb.delete(alfaSb.length() - 1, alfaSb.length());
            System.out.println("Found ALFA: " + alfaSb);

            ///////////////////////////////////////////////
            // Beta
            ///////////////////////////////////////////////
            StringBuilder betaSb = new StringBuilder();
            if (!matrix2[currentNTIndex][0].isEmpty()) { // Добавляем свободный член, если не пустой
                betaSb.append(matrix2[currentNTIndex][0]).append("+");
            }

            // Идём по всем правилам ДО ТЕКУЩЕГО ИНДЕКСА, подсталяем a*b(раскрываем правила) и ищем коэффициент при текущем нетерминале
            for (int i = 1; i < currentNTIndex; i++) {
                if (matrix2[currentNTIndex][i].isEmpty()) continue; // Пропускаем пустые правила

                // Записываем коэффициент при правиле на случай, если получится раскрыть правило
                StringBuilder tempCoeff = new StringBuilder(matrix2[currentNTIndex][i].substring(0, matrix2[currentNTIndex][i].length() - 1));
                int tempIndex = i; // Индекс текущего РАСКРЫВАЕМОГО нетерминала
                String tempNT = nonTerminals[i]; // Текущего РАСКРЫВАЕМЫЙ нетерминал
                List<String> listOfNT = new LinkedList<>(); // Последовательность раскрытия нетерминалов
                listOfNT.add(tempNT);

                String ruleWithoutNT = "";

                while (true) { // tempIndex < currentNTIndex && containNT(matrix[currentNTIndex][tempIndex])
                    matrix2[currentNTIndex][tempIndex] = matrix2[currentNTIndex][tempIndex].replace(tempNT, alfaBeta[tempIndex][0] + "*" + alfaBeta[tempIndex][1]);

                    if (matrix2[currentNTIndex][tempIndex].contains(currentNonTerminal)) {
                        break; // OK
                    }

                    if (!containNT(matrix2[currentNTIndex][tempIndex])) { // Нашли правило, которое не содержит NT
                        ruleWithoutNT = matrix2[currentNTIndex][tempIndex];
                        break;
                    }

                    if (getNT(matrix2[currentNTIndex][tempIndex]).equals("")) { // Не нашли NT в правиле
                        System.err.println("ERROR!");
                        System.exit(777);
                    }

                    if (indexesOfNonTerminalsMap.get(getNT(matrix2[currentNTIndex][tempIndex])) > tempIndex) { // Нашли правило с нерешённым NT
                        ruleWithoutNT = matrix2[currentNTIndex][tempIndex];
                        break;
                    }

                    // Продолжаем раскрывать правила
                    tempIndex = indexesOfNonTerminalsMap.get(getNT(matrix2[currentNTIndex][tempIndex]));
                    tempNT = getNT(matrix2[currentNTIndex][tempIndex]);
                    listOfNT.add(tempNT);
                }


                // Проходимся по пути вывода правила и добавляем коэффициенты
                for (String s : listOfNT) {
                    tempCoeff.append(coeffs[indexesOfNonTerminalsMap.get(s)][0]);
                }

                betaSb.append(tempCoeff).append("+");

                if (!ruleWithoutNT.isEmpty()) {
                    betaSb.append(ruleWithoutNT).append("+");
                }
            }

            betaSb.delete(betaSb.length() - 1, betaSb.length());
            System.out.println("Found BETA: " + betaSb);
        }
    }

    public static boolean containNT(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i)) && Character.isUpperCase(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static String getNT(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i)) && Character.isUpperCase(str.charAt(i))) {
                return String.valueOf(str.charAt(i));
            }
        }
        return "";
    }

    /*public static void reverseRun() {
        for (int i = matrix.length - 1; i > 0; i--) {
            String currentNonTerminal = nonTerminals[i];

            for (int j = 0; j < i; j++) { // Заменяем все вхождения текущего нетерминала на РВ (a*b)
                matrix[j] = matrix[j].replace(currentNonTerminal, matrix[i]);
            }
        }
    }*/

    public static void main(String[] args) {
        indexesOfNonTerminalsMap.put("A", 1);
        indexesOfNonTerminalsMap.put("B", 2);

        forwardRun();
        //reverseRun();
        for (String[] arr : matrix) {
            for (String str : arr) {
                System.out.print(str + " : ");
            }
            System.out.println();
        }

        for (String[] arr : coeffs) {
            for (String str : arr) {
                System.out.print(str + " : ");
            }
            System.out.println();
        }

        for (String[] arr : alfaBeta) {
            for (String str : arr) {
                System.out.print(str + " : ");
            }
            System.out.println();
        }
    }
}
