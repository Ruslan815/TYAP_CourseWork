import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    // G = {acsxyz; F, S, A, K; F -> cA | yS, A -> zA | aS, S -> sS | xA | K, K -> !; F}

    // G = {acsxyz; S, A; S -> y | Ss | Aa, A -> c | Sx | Az; S} WORK (LL)
    // G = {acsxyz; S, A; S -> y | Ss | A, A -> c | Sx | Az; S} WORK (LL with lambda transition)
    static final int LIMIT_OF_STEPS = 100;
    static String grammar = "G = {ab; S, A; S -> aA | bS, A -> aA | a; S}"; // Знак ! означает лямбду (пустой символ)
    // private static String grammar = "G = {01; S, A; S -> 1A | 0A, A -> 1 | 0 | !; S}";
    static String grammarType; // L, R
    static int startLength;
    static int endLength;
    static Map<String, String[]> mapOfRules = new HashMap<>();
    static int stepCounter = 0;
    static Set<Character> nonTerminalsSet;
    static List<String> listOfRuleChain = new LinkedList<>();
    // private static final char[] notAllowedCharacters = {'!'};

    public static void generateLanguageChains(String currentNonTerminal, String currentChain, int currentLengthInTerminals) {
        // System.out.println(stepCounter);
        // System.out.println(currentChain);
        listOfRuleChain.add(currentChain);
        stepCounter++;

        // Если сгенерировали цепочку длины, больше чем надо, то дальше не генерируем
        if (currentLengthInTerminals > endLength) {
            stepCounter--;
            listOfRuleChain.remove(listOfRuleChain.size() - 1);
            return;
        }

        // Если нашли цепочку подходящей длины
        if (currentLengthInTerminals >= startLength) {
            boolean isNonTerminalExistInChain = false;
            for (int i = 0; i < currentChain.length(); i++) {
                if (Character.isUpperCase(currentChain.charAt(i))) { // If nonTerminal found in current chain
                    isNonTerminalExistInChain = true;
                }
            }
            if (!isNonTerminalExistInChain) {
                currentChain = currentChain.replaceAll("!", "");
                System.out.println(currentChain);
                System.out.println(listOfRuleChain);
                stepCounter--;
                listOfRuleChain.remove(listOfRuleChain.size() - 1);
                return;
            }
        }

        // S->00SAB|11S|01A|10A|!
        // A->00A|11A|01S|10S|1|0S0
        // S, "S", 0
        // S, 00S, 2
        // S, 0000S, 4
        String previousChain = currentChain;
        String nonTerminalForSteps = currentNonTerminal;
        // System.out.println(nonTerminalForSteps);
        for (String currentRule : mapOfRules.get(nonTerminalForSteps)) {
            // Заменяем первый встреченный нетерминал на текущее правило
            currentChain = previousChain.replaceFirst(String.valueOf(nonTerminalForSteps), currentRule);

            currentLengthInTerminals = countOfTerminals(currentChain);

            boolean isNonTerminalFound = false;
            // Левосторонний если не делать ревёрс
            for (int i = 0; i < currentChain.length(); i++) {
                if (Character.isUpperCase(currentChain.charAt(i))) {
                    isNonTerminalFound = true;
                    currentNonTerminal = String.valueOf(currentChain.charAt(i));
                    break;
                }
            }

            // Если найдена законченная цепочка неподходящей длины
            if (!isNonTerminalFound && (currentLengthInTerminals < startLength || currentLengthInTerminals > endLength)) {
                stepCounter--;
                listOfRuleChain.remove(listOfRuleChain.size() - 1);
                return;
            }
            // Защита от зацикливания
            if (stepCounter > LIMIT_OF_STEPS) {
                stepCounter--;
                listOfRuleChain.remove(listOfRuleChain.size() - 1);
                return;
            }

            generateLanguageChains(currentNonTerminal, currentChain, currentLengthInTerminals);
        }
        stepCounter--;
        listOfRuleChain.remove(listOfRuleChain.size() - 1);
    }

    public static String reverseString(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    public static int countOfTerminals(String someChain) {
        int count = 0;
        for (int i = 0; i < someChain.length(); i++) {
            if (someChain.charAt(i) != '!' && (!Character.isUpperCase(someChain.charAt(i)) || Character.isDigit(someChain.charAt(i)))) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
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
        generateLanguageChains(parsedGrammar.getStartRule(), parsedGrammar.getStartRule(), 0);
    }
}
