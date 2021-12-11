import java.util.Arrays;

public class ConvertLLGtoRLG {
    int countOfNT;
    String[][] matrix;
    String[] nonTerminals;

    String[][] result;

    public ConvertLLGtoRLG(String[][] someGrammar) {
        countOfNT = someGrammar.length + 1; // Добавляем S0 и F NT
        matrix = new String[countOfNT][countOfNT];

        for (String[] row: matrix) // Заполняем матрицу пустотой
            Arrays.fill(row, "");

        // Формируем матрицу для автомата, добавляем состояние S0 и определяем нулевую строку под состояния F
        for (int i = 1; i < matrix.length - 1; i++) { // Исключая строку S0
            matrix[i][0] = someGrammar[i][0];
            for (int j = 1; j < matrix[i].length - 1; j++) { // Исключая столбец S0
                if (someGrammar[i][j].length() > 1) { // Если есть переход с добавлением символов к цепочке
                    matrix[i][j] = someGrammar[i][j].substring(1);
                } else if (someGrammar[i][j].length() == 1) { // Если есть переход без добавления символов к цепочке
                    matrix[i][j] = "!";
                    //matrix[i][j] = someGrammar[i][j];
                }
            }
        }
        matrix[matrix.length - 1][1] = "!"; // Переход в целевой символ S по лямбде

        // Транспонируем матрицу (меняем направление переходов в автомате и меняем местами состояния S0 и F)
        String[][] temp = new String[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                temp[j][i] = matrix[i][j];
            }
        }
        matrix = temp;
        matrix[1][0] = "!"; // Выход из целевого(изначального, теперь финального) символа по лямбде

        // Выводим
        /*System.out.println("TRANSPOSED");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }*/

        // Создаём результирующую ПЛ грамматику
        String[][] newRLG = new String[matrix.length][matrix.length];
        for (String[] row: newRLG) // Заполняем матрицу пустотой
            Arrays.fill(row, "");

        // Создаём массив с номерами нетерминалов и их названиями
        nonTerminals = new String[newRLG.length];
        Arrays.fill(nonTerminals, "");
        nonTerminals[1] = "S";
        for (int i = 2; i < nonTerminals.length; i++) {
            nonTerminals[i] = String.valueOf(Character.toChars('A' + (i - 2)));
        }

        // Сдвигаем транспонированную матрицу вправо вниз на одну позицию и приписываем коэфф-ты
        for (int i = 1; i < matrix.length; i++) { // Начинаем с первого NT
            for (int j = 2; j < matrix[i].length; j++) { // Со второго правила
                if (matrix[i - 1][j - 1] .equals("!")) { // Переход без добавления символов (по лямбде)
                    newRLG[i][j] = nonTerminals[j];
                } else { // Переход c добавлением символов
                    newRLG[i][j] = matrix[i - 1][j - 1] + nonTerminals[j];
                }
            }
        }
        //newRLG[2][0] = ""; // Переход в финальное состояние по лямбде

        // Добавляем дополнительные свободные члены для выхода
        for (int i = 1; i < newRLG.length; i++) {
            for (int j = 1; j < newRLG[i].length; j++) {
                if (!newRLG[i][j].isEmpty() && newRLG[i][j].contains("A") && newRLG[i][j].length() > 1) { // Если правило содержит нетерминал А и коэфф к нему
                    if (newRLG[i][0].isEmpty()) { // Если у правило не было свободных членов
                        newRLG[i][0] = newRLG[i][j].substring(0, newRLG[i][j].length() - 1);
                    } else { // Если у правила были свободные члены
                        newRLG[i][0] = "+" + newRLG[i][j].substring(0, newRLG[i][j].length() - 1);
                    }
                }
            }
        }

        // Добавляем дополнительные свободные члены для выхода по cS->A->B (где B - финальное состояние)
        for (int i = 1; i < newRLG.length; i++) {
            for (int j = 1; j < newRLG[i].length; j++) {
                if (newRLG[i][j].length() == 1) { // Если правило содержит нетерминал А без коэфф.
                    String tempNT = nonTerminals[i]; // Запоминаем этот нетерминал
                    for (int k = 1; k < newRLG.length; k++) { // Проходимся по всем строкам сверху
                        for (int l = 0; l < newRLG[k].length; l++) { // Ищем правило, содержащие этот нетерминал
                            if (newRLG[k][l].contains(tempNT) && newRLG[k][l].length() > 1) { // Если при нём есть коэфф.
                                if (newRLG[k][0].isEmpty()) {
                                    newRLG[k][0] += newRLG[k][l].substring(0, newRLG[k][l].length() - 1); // То записываем в свободный член
                                } else {
                                    newRLG[k][0] += "+" + newRLG[k][l].substring(0, newRLG[k][l].length() - 1); // То прибавляем его к свободному члену
                                }
                            }
                        }
                    }
                }
            }
        }

        // Выводим
        /*System.out.println("RESULT");
        for (int i = 0; i < newRLG.length; i++) {
            for (int j = 0; j < newRLG[0].length; j++) {
                System.out.print(newRLG[i][j] + " ");
            }
            System.out.println();
        }*/

        result = newRLG;
    }

    public String[][] getResult() {
        return result;
    }

    public String[] getNonTerminals() {
        return nonTerminals;
    }

    public static void main(String[] args) {
        String[][] LLG = {
                {"", "", ""},
                {"y", "Ss", "Aa"},
                {"c", "Sx", "Az"}
        };
        ConvertLLGtoRLG convertLLGtoRLG = new ConvertLLGtoRLG(LLG);
    }
}
