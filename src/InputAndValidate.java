import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeSet;

public class InputAndValidate {
    public static void inputData() throws IOException {
        Scanner scanner = new Scanner(System.in);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Если вы хотите использовать грамматику ПО УМОЛЧАНИЮ введите цифру 0, \n" +
                "если вы хотите ввести СВОЮ грамматику введите цифру 1: ");
        int isUserGrammar = scanner.nextInt();
        if (isUserGrammar == 1) {
            System.out.println("Введите грамматику (G = {\"\"; [ , ]; [ , ]; \"\"}): ");
            Main.grammar = reader.readLine();
            // grammar = scanner.next();
            // System.out.println(inputStr);
        } else if (isUserGrammar != 0) {
            System.err.println("Wrong type of input grammar mode!");
            throw new IOException();
        }

        System.out.print("Введите тип регулярной грамматики (Леволинейная - L, Праволинейная - R): ");
        String outputTypeStr = scanner.next();
        // System.out.println(outputTypeStr);
        if (!outputTypeStr.equals("L") && !outputTypeStr.equals("R")) {
            System.err.println("Wrong type of output grammar type!");
            throw new IOException();
        } else if (outputTypeStr.equals("L")) {
            Main.grammarType = "L";
        } else {
            Main.grammarType = "R";
        }

        System.out.print("Введите диапазон длин генерируемых цепочек (start end): ");
        Main.startLength = scanner.nextInt();
        Main.endLength = scanner.nextInt();
        if (Main.startLength < 0 || Main.endLength < 0 || Main.startLength > Main.endLength) {
            System.err.println("Invalid range of generate sequences length!");
            throw new IOException();
        }
        //System.out.println("Start && end: " + startLength + " && " + endLength);

        scanner.close();
    }

    public static Grammar parseGrammar() throws Exception {
        Main.grammar = Main.grammar.replace(" ", "");
        Main.grammar = Main.grammar.replace("{", "");
        Main.grammar = Main.grammar.replace("}", "");
        Main.grammar = Main.grammar.substring(2);

        String[] grammarMembers = Main.grammar.split(";");

        char[] terminals = grammarMembers[0].toCharArray();
        String[] nonTerminals = grammarMembers[1].split(",");
        String[] rules = grammarMembers[2].split(",");
        String startRule = grammarMembers[3];

        terminals = validateTerminals(terminals);
        nonTerminals = validateNonTerminals(nonTerminals);
        startRule = validateStartRule(startRule, nonTerminals);
        validateRules(rules, terminals, nonTerminals);

        // System.out.println(Arrays.toString(nonTerminals));

        System.out.println(Arrays.toString(grammarMembers));

        return new Grammar(terminals, nonTerminals, rules, startRule);
    }

    /**
     * Переводим в нижний регистр и удаляем повторения
     */
    public static char[] validateTerminals(char[] terminals) throws Exception {
        TreeSet<Character> terminalSet = new TreeSet<>();
        for (char someTerminal : terminals) {
            if (someTerminal == '|' || someTerminal == ',') {
                System.err.println("Terminal character can't be \"|\", \",\"!");
                throw new Exception();
            }
            terminalSet.add(Character.toLowerCase(someTerminal));
        }
        //System.out.println(terminalSet.size());
        char[] tempArr = new char[terminalSet.size()];
        for (int i = 0; i < tempArr.length; i++) {
            tempArr[i] = terminalSet.pollFirst();
        }
        return tempArr;
    }

    /**
     * Переводим в верхний регистр и удаляем повторения
     */
    public static String[] validateNonTerminals(String[] nonTerminals) throws Exception {
        TreeSet<String> nonTerminalSet = new TreeSet<>();
        for (String someNonTerminal : nonTerminals) {
            if (someNonTerminal.equals("|")) {
                System.err.println("Terminal character can't be \"|\"!");
                throw new Exception();
            }

            if (someNonTerminal.length() != 1) {
                System.err.println("NonTerminal must contains only one symbol");
                throw new Exception();
            }

            if (Character.isDigit(someNonTerminal.charAt(0))) {
                System.err.println("NonTerminal can't be a numeric symbol");
                throw new Exception();
            }

            nonTerminalSet.add(someNonTerminal.toUpperCase());
        }
        //System.out.println(nonTerminalSet.size());
        String[] tempArr = new String[nonTerminalSet.size()];
        for (int i = 0; i < tempArr.length; i++) {
            tempArr[i] = nonTerminalSet.pollFirst();
        }
        return tempArr;
    }

    /**
     * Переводим в верхний регистр и проверяем на наличие в списке нетерминальных символов
     */
    public static String validateStartRule(String startRule, String[] nonTerminals) throws Exception {
        startRule = startRule.toUpperCase();
        if (!Arrays.asList(nonTerminals).contains(startRule)) {
            System.err.println("Unknown nonTerminal symbol used in start rule");
            throw new Exception();
        }

        return startRule;
    }

    /**
     * Проверяем на корректность формат левой и правой части правил
     */
    public static void validateRules(String[] rules, char[] terminals, String[] nonTerminals) throws Exception {
        Main.nonTerminalsSet = new HashSet<>();
        for (String rule : rules) {
            // Validate of Left part of rule
            if (!Character.isUpperCase(rule.charAt(0))) {
                System.err.println("NonTerminal must be in upper case");
                throw new Exception();
            } else if (!isElementInArray(rule.charAt(0), nonTerminals)) {
                System.err.println("Character is not nonTerminal");
                throw new Exception();
            } else {
                Main.nonTerminalsSet.add(rule.charAt(0));
            }

            if (rule.length() <= 3) {
                System.err.println("Rule length must be more than 3 characters");
                throw new Exception();
            }

            // Validate of Right part of rule
            for (int i = 3; i < rule.length(); i++) {
                if (rule.charAt(i) == '|' || rule.charAt(i) == '!') {
                    continue;
                }
                if (Character.isDigit(rule.charAt(i)) && !isElementInArray(rule.charAt(i), terminals)) {
                    System.err.println("Unknown numeric terminal in rule!");
                    throw new Exception();
                }
                if (!Character.isUpperCase(rule.charAt(i)) && !isElementInArray(rule.charAt(i), terminals)) { //!!!!!!!!!!!!!
                    System.err.println("Wrong terminal character in rule!");
                    throw new Exception();
                }
                if (Character.isUpperCase(rule.charAt(i)) && !isElementInArray(rule.charAt(i), nonTerminals)) {
                    System.err.println("Wrong nonTerminal character in rule!");
                    throw new Exception();
                }
            }

            if (rule.charAt(rule.length() - 1) != '!' && (!isElementInArray(rule.charAt(rule.length() - 1), terminals) && !isElementInArray(rule.charAt(rule.length() - 1), nonTerminals))) {
                System.err.println("Rule must finish by terminal or nonTerminal!");
                throw new Exception();
            }
        }

        // Sanity check
        if (Main.nonTerminalsSet.size() != nonTerminals.length) {
            System.err.println("All nonTerminal must be used in rules!");
            throw new Exception();
        }
    }

    public static boolean isElementInArray(char element, String[] arr) {
        for (String s : arr) {
            if (element == s.charAt(0)) return true;
        }
        return false;
    }

    public static boolean isElementInArray(char element, char[] arr) {
        for (char c : arr) {
            if (c == element) return true;
        }
        return false;
    }

    /**
     * Заполняем мапу правил: Map<NonTerminal, Rules[]>
     */
    public static void fillMapOfRules(String[] nonTerminals, String[] rules) {
        for (String someNonTerminal : nonTerminals) {
            String[] arrOfRules = null;
            for (String someRule : rules) {
                // Если нашли правило для текущего нетерминала
                if (someRule.charAt(0) == someNonTerminal.charAt(0)) {
                    arrOfRules = someRule.substring(3).split("\\|");
                    break;
                }
            }
            Main.mapOfRules.put(someNonTerminal, arrOfRules);
        }
    }
}
