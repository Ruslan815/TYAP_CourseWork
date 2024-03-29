import java.io.IOException;
import java.util.*;

public class GrammarGenerator {
    // G = {acsxyz; S, A; S -> y | Ss | Aa, A -> c | Sx | Az; S} WORK (LLG)
    // G = {acsxyz; S, A; S -> y | Ss | A, A -> c | Sx | Az; S} WORK (LLG1 with lambda transition)
    // G = {acsxyzk; S, A; S -> y | Ss | Aa | Ak, A -> c | Sx | Az; S} WORK (LLG2 выражение в скобках в качестве терминала)

    // G = {abcdef; S, A; S -> a | bS | cA, A -> d | eS | fA; S} WORK (RLG)
    // G = {abcdefz; S, A; S -> a | bS | cA | zS, A -> d | eS | fA; S} WORK (RLG1 выражение в скобках в качестве терминала)
    // G = {abcdef; S, A; S -> a | S | cA, A -> d | eS | fA; S} WORK (RLG2 with lambda transition) сделай лимит рекурсии меньше (10)

    // G = {abcdefghikmz; S, A, B; S -> a | bS | dB, A -> e | fS | gA, B -> i | kS | zB; S} WORK (RLG3 3 уравнения)

    static final int LIMIT_OF_STEPS = 100;
    static String grammar = "G = {acsxyz; S, A; S -> y | Ss | Aa, A -> c | Sx | Az; S}"; // Знак ! означает лямбду (пустой символ)
    static int startLength;
    static int endLength;
    static Map<String, String[]> mapOfRules = new HashMap<>();
    static int stepCounter = 0;
    static Set<Character> nonTerminalsSet;
    static List<String> listOfRuleChain = new LinkedList<>();
    static List<List<String>> outputHistoryList = new LinkedList<>();
    static List<String> resultList = new LinkedList<>();

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
                resultList.add(currentChain); //System.out.println(currentChain);
                List<String> tempList = new LinkedList<>(listOfRuleChain);
                outputHistoryList.add(tempList); //System.out.println(listOfRuleChain); // TODO copyOf
                stepCounter--;
                listOfRuleChain.remove(listOfRuleChain.size() - 1);
                return;
            }
        }

        String previousChain = currentChain;
        String nonTerminalForSteps = currentNonTerminal;
        // System.out.println(nonTerminalForSteps);
        for (String currentRule : mapOfRules.get(nonTerminalForSteps)) {
            // Заменяем первый встреченный нетерминал на текущее правило
            currentChain = previousChain.replaceFirst(String.valueOf(nonTerminalForSteps), currentRule);

            currentLengthInTerminals = countOfTerminals(currentChain);

            boolean isNonTerminalFound = false;
            for (int i = 0; i < currentChain.length(); i++) {
                if (Character.isUpperCase(currentChain.charAt(i))) {
                    isNonTerminalFound = true;
                    currentNonTerminal = String.valueOf(currentChain.charAt(i));
                    break;
                }
            }

            // Если найдена законченная цепочка неподходящей длины
            if (!isNonTerminalFound && (currentLengthInTerminals < startLength || currentLengthInTerminals > endLength)) {
                continue;
                //stepCounter--;
                //listOfRuleChain.remove(listOfRuleChain.size() - 1);
                //return;
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
        /*try {
            InputAndValidate.inputData();
        } catch (IOException e) {
            System.err.println("Input data error!"); //e.printStackTrace();
            return;
        }*/

        startLength = 0;
        endLength = 3;

        Grammar parsedGrammar;
        try {
            parsedGrammar = InputAndValidate.parseGrammar();
            //System.out.println(parsedGrammar);
        } catch (Exception e) {
            System.err.println("Grammar parsing Exception!"); //e.printStackTrace();
            return;
        }
        //System.out.println("Grammar after parsing:\n" + parsedGrammar);

        InputAndValidate.fillMapOfRules(parsedGrammar.getNonTerminals(), parsedGrammar.getRules()); //displayMapOfRules();
        generateLanguageChains(parsedGrammar.getStartRule(), parsedGrammar.getStartRule(), 0);

        System.out.println("History:");
        for (List<String> list : outputHistoryList) {
            System.out.println(list);
        }
        System.out.println("Result:");
        System.out.println(resultList);
    }
}
