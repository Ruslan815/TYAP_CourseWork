import java.util.*;

public class SLAU {
    static String[][] matrix = {
            {"", "", ""},
            {"a", "bA", "cB"},
            {"d", "eA", "fB"}
    };

    static String[] result = {
            "",
            "",
            ""
    };

    // static String[][] matrix2;

    /*static String[][] coeffs = {
            {"", "", ""},
            {"", "", ""}, // После первого уравнения
            {"", "", ""} // После второго уравнения и т.д.
    };

    static String[][] alfaBeta = {
            {"", ""},
            {"", ""},
            {"", ""}
    };*/

    static String[] nonTerminals = {"", "A", "B"};

    static Map<String, Integer> indexesOfNonTerminalsMap = new HashMap<>();

    public static void forwardRun() {
        /*// Решаем уравнение для первой строки
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

        // Считаем коэффициенты перехода из первого уравнения
        coeffs[currentNTIndex][0] = alfa + "*" + matrix[currentNTIndex][0];
        for (int i = currentNTIndex + 1; i < coeffs[currentNTIndex].length; i++) { // Треугольная матрица СТРОГО, не надо считать коэффициенты для уже посчитанных уравнений!
            if (matrix[currentNTIndex][i].isEmpty()) continue;
            String resStr = alfa + "*" + matrix[currentNTIndex][i];
            coeffs[currentNTIndex][i] = resStr.substring(0, resStr.length() - 1);
        }*/

        // Создаём копию матрицы для нахождения BETA
        /*matrix2 = new String[matrix.length][matrix[0].length];
        for (int x = 0; x < matrix.length; x++) {
            System.arraycopy(matrix[x], 0, matrix2[x], 0, matrix[x].length);
        }*/


        // Идём по всем уравнениям (строкам)
        for (int currentNTIndex = 1; currentNTIndex < matrix.length; currentNTIndex++) { // TODO 2
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
            if (!alfaSb.isEmpty()) {
                alfaSb.delete(alfaSb.length() - 1, alfaSb.length());
            }
            if (!betaSb.isEmpty()) {
                betaSb.delete(betaSb.length() - 1, betaSb.length());
            }

            System.out.println("ALFA");
            System.out.println(alfaSb);

            System.out.println("BETA");
            System.out.println(betaSb);

            if (!alfaSb.isEmpty() && !betaSb.isEmpty()) { // a*b
                result[currentNTIndex] = "((" + alfaSb + ")*(" + betaSb + "))";
            } else if (alfaSb.isEmpty() && !betaSb.isEmpty()) { // b
                result[currentNTIndex] = "(" + betaSb + ")";
            } else if (!alfaSb.isEmpty() && betaSb.isEmpty()) { // a*
                result[currentNTIndex] = "(" + alfaSb + ")*";
            }

            for (int i = currentNTIndex + 1; i < matrix.length; i++) {
                for (int j = 1; j < matrix[i].length; j++) {
                    if (matrix[i][j].contains(currentNonTerminal)) {
                        matrix[i][j] = matrix[i][j].replace(currentNonTerminal, result[currentNTIndex]);
                        /*if (!alfaSb.isEmpty() && !betaSb.isEmpty()) { // a*b
                            result[currentNTIndex] = "((" + alfaSb + ")*(" + betaSb + "))";
                            matrix[i][j] = matrix[i][j].replace(currentNonTerminal, "((" + alfaSb + ")*(" + betaSb + "))");
                        } else if (alfaSb.isEmpty() && !betaSb.isEmpty()) { // b
                            result[currentNTIndex] = "(" + betaSb + ")";
                            matrix[i][j] = matrix[i][j].replace(currentNonTerminal, "(" + betaSb + ")");
                        } else if (!alfaSb.isEmpty() && betaSb.isEmpty()) { // a*
                            result[currentNTIndex] = "(" + alfaSb + ")*";
                            matrix[i][j] = matrix[i][j].replace(currentNonTerminal, "(" + alfaSb + ")*");
                        } else if (alfaSb.isEmpty() && betaSb.isEmpty()) { // Пустота
                            matrix[i][j] = matrix[i][j].replace(currentNonTerminal, "");
                        }*/
                    }
                }
            }

            // Идём по всем правилам ДО ТЕКУЩЕГО ИНДЕКСА, подсталяем a*b(раскрываем правила) и ищем коэффициент при текущем нетерминале
            /*for (int i = 1; i < currentNTIndex; i++) {
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
            System.out.println("Found ALFA: " + alfaSb);*/

            ///////////////////////////////////////////////
            // Beta
            ///////////////////////////////////////////////
            /*List<String> betaList = new LinkedList<>();
            StringBuilder betaSb = new StringBuilder();
            if (!matrix2[currentNTIndex][0].isEmpty()) { // Добавляем свободный член, если не пустой
                betaList.add(matrix2[currentNTIndex][0]);
                betaSb.append(matrix2[currentNTIndex][0]).append("+");
            }

            // Идём по всем правилам ДО ТЕКУЩЕГО ИНДЕКСА, подсталяем a*b(раскрываем правила) и ищем коэффициент при текущем нетерминале
            for (int i = 1; i < currentNTIndex; i++) {
                if (matrix2[currentNTIndex][i].isEmpty()) continue; // Пропускаем пустые правила

                // Записываем коэффициент при правиле на случай, если получится раскрыть правило
                StringBuilder tempCoeff = new StringBuilder();
                String someCoeff = matrix2[currentNTIndex][i].substring(0, matrix2[currentNTIndex][i].length() - 1);
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
                    for (int j = 0; j < matrix2[indexesOfNonTerminalsMap.get(s)].length; j++) {
                        if (j == currentNTIndex) continue; // Коэффициент для ALFA
                        if (j == 0) {
                            betaList.add(someCoeff + coeffs[indexesOfNonTerminalsMap.get(s)][j]);
                            tempCoeff.append(someCoeff).append(coeffs[indexesOfNonTerminalsMap.get(s)][j]).append("+"); // fb*a
                        } else {
                            betaList.add(someCoeff + coeffs[indexesOfNonTerminalsMap.get(s)][j] + nonTerminals[j]);
                            tempCoeff.append(someCoeff).append(coeffs[indexesOfNonTerminalsMap.get(s)][j]).append(nonTerminals[j]).append("+"); // fb*dB
                        }
                    }
                }

                betaSb.append(tempCoeff).append("+");

                if (!ruleWithoutNT.isEmpty()) {
                    betaSb.append(ruleWithoutNT).append("+");
                }
            }

            // Дозаписываем оставшиеся нерешённые уравнения справа от текущего нетерминала
            for (int i = currentNTIndex + 1; i < matrix2[currentNTIndex].length; i++) {
                betaList.add(matrix2[currentNTIndex][i]);
                betaSb.append(matrix2[currentNTIndex][i]).append("+"); // hB
            }

            betaSb.delete(betaSb.length() - 1, betaSb.length());
            System.out.println("Found BETA: " + betaSb);

            // Смотрим всё, что записали в BETA и оттуда вычленяем нужные коэффициенты для нетерминалов
            for (int i = 0; i < coeffs[0].length; i++) {
                if (i == 0) { // Если ищем коэффициенты для свободных членов
                    StringBuilder result = new StringBuilder();
                    String tempCoeff = "";
                    for (String s : betaList) {
                        tempCoeff = s;
                        if (!containNT(tempCoeff)) {
                            result.append(tempCoeff).append("+");
                        }
                    }

                    if (!result.isEmpty()) {
                        result.delete(0, result.length() - 1);
                        coeffs[currentNTIndex][0] = result.toString(); // Записали свободные Коэффициенты
                    }
                    continue;
                }

                StringBuilder result = new StringBuilder();
                String tempCoeff = "";
                for (String s : betaList) {
                    tempCoeff = s;
                    if (tempCoeff.contains(nonTerminals[i])) { // Если содержит нужный нетерминал
                        result.append(tempCoeff).append("+");
                    }
                }

                if (!result.isEmpty()) {
                    result.delete(0, result.length() - 1);
                    coeffs[currentNTIndex][i] = result.toString(); // Записали Коэффициенты для i-го нетерминала
                }
            }

            alfaBeta[currentNTIndex][0] = "(" + alfaSb + ")";
            alfaBeta[currentNTIndex][1] = "(" + betaSb + ")";

            if (currentNTIndex == 1) {
                // Создаём копию матрицы для нахождения BETA
                matrix2 = new String[matrix.length][matrix[0].length];
                for (int x = 0; x < matrix.length; x++) {
                    System.arraycopy(matrix[x], 0, matrix2[x], 0, matrix[x].length);
                }
            }*/
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

    public static void reverseRun() {
        for (int currentNTIndex = matrix.length - 1; currentNTIndex >= 1; currentNTIndex--) {
            for (int i = currentNTIndex + 1; i < matrix.length; i++) {
                if (result[currentNTIndex].contains(nonTerminals[i])) {
                    result[currentNTIndex] = result[currentNTIndex].replace(nonTerminals[i], result[i]);
                }
            }
        }
    }

    public static void main(String[] args) {
        String[][] LLG = {
                {"", "", ""},
                {"y", "Ss", "Aa"},
                {"c", "Sx", "Az"}
        };

        ConvertLLGtoRLG converter = new ConvertLLGtoRLG(LLG);
        nonTerminals = converter.getNonTerminals();
        matrix = converter.getResult();
        result = new String[matrix.length];
        Arrays.fill(result, "");

        //indexesOfNonTerminalsMap.put("A", 1);
        //indexesOfNonTerminalsMap.put("B", 2);

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

        /*System.out.println("COEFFS");
        for (String[] arr : coeffs) {
            for (String str : arr) {
                System.out.print(str + " : ");
            }
            System.out.println();
        }

        System.out.println("ALFA_BETA");
        for (String[] arr : alfaBeta) {
            for (String str : arr) {
                System.out.print(str + " : ");
            }
            System.out.println();
        }*/
    }
}
