import java.util.LinkedList;
import java.util.List;

public class RegExpGenerator {
    private List<String> klini(List<String> list, int maxLength) {
        List<String> res = new LinkedList<>();
        List<String> tmp = new LinkedList<>();
        tmp = Combined(tmp, list);
        res.add("");
        //res.AddRange(tmp.FindAll(x => x.Length <= maxLength));
        res.addAll(tmp.stream().filter(x -> x.length() <= maxLength).toList());

        //while (tmp.Count(x => x.Length < maxLength) > 0)
        while (tmp.stream().anyMatch(x -> x.length() < maxLength)) {
            tmp = Combined(tmp, list);
            //res.AddRange(tmp.FindAll(x => x.Length <= maxLength));
            res.addAll(tmp.stream().filter(x -> x.length() <= maxLength).toList());
        }

        return res;
    }

    private List<String> splitByPluses(String str) // Разделяем выражения по знаку +
    {
        List<String> splitedString = new LinkedList<>();
        int depth = 0;
        int prevInd = 0;
        for (int i = 0; i < str.length(); i++) {
            if (depth == 0 && str.charAt(i) == '+') {
                //splitedString.Add(str.Substring(prevInd, i - prevInd)); // длина
                splitedString.add(str.substring(prevInd, i)); // конечный индекс
                prevInd = i + 1;
            }
            if (str.charAt(i) == '(') depth++;
            if (str.charAt(i) == ')') depth--;
        }
        //splitedString.Add(str.Substring(prevInd));
        splitedString.add(str.substring(prevInd));
        return splitedString;
    }

    private List<String> recursion(String str, int minLenght, int maxLenght) {
        List<String> splitedList = splitByPluses(str);
        List<String> ans = new LinkedList<>();

        //foreach (var substr in splitedList) // Проходимся по выражениям, разделённым плюсом
        for (String substr : splitedList) {
            List<String> tempResultList = new LinkedList<>();
            for (int i = 0; i < substr.length(); i++) // Проходимся по выражению
            {
                if (substr.charAt(i) == '(') // Если оно в скобках
                {
                    int indexOfClosetBracet = findClosestBracet(substr.substring(i + 1)); // Ищем конец скобки
                    if (i + indexOfClosetBracet + 1 < substr.length() && substr.charAt(i + indexOfClosetBracet + 1) == '*') // Если стоит * после скобок выражения
                    { // (a+b)*
                        tempResultList = Combined(tempResultList, klini(recursion(substr.substring(i + 1, i + indexOfClosetBracet), minLenght, maxLenght), maxLenght)); // TODO
                    } else // Если после скобок не стоит *
                    {
                        tempResultList = Combined(tempResultList, recursion(substr.substring(i + 1, i + indexOfClosetBracet), minLenght, maxLenght)); // TODO
                    }
                    i += indexOfClosetBracet;
                } else if (Character.isLetter(substr.charAt(i))) // Если встретили символ алфавита
                {
                    if (i + 1 < substr.length() && substr.charAt(i + 1) == '*') // И после него стоит звёздочка
                    {
                        List<String> temp777List = new LinkedList<>();
                        temp777List.add(String.valueOf(substr.charAt(i)));
                        tempResultList = Combined(tempResultList, klini(temp777List, maxLenght));
                    }
                    if (tempResultList.size() == 0) // Если в список ещё ничего не записано
                    {
                        tempResultList.add(String.valueOf(substr.charAt(i)));
                    } else // А если что-то было, то находим комбинации текущего символа и того, чтобы было в списке
                    {
                        List<String> temp777List = new LinkedList<>();
                        temp777List.add(String.valueOf(substr.charAt(i)));
                        tempResultList = Combined(tempResultList, temp777List);
                    }
                }
            }

            //ans.AddRange(tempResultList);
            ans.addAll(tempResultList);
        }

        return ans;
    }

    private List<String> Combined(List<String> a, List<String> b) // Находит все комбинации значений из двух списков
    {
        List<String> tmp = new LinkedList<>();
        if (a.size() > 0 && b.size() > 0) {
            for (String str1 : a) {
                for (String str2 : b) {
                    tmp.add(str1 + str2);
                }
            }
        } else if (a.size() > 0 && b.size() == 0) {
            for (String str : a) {
                tmp.add(str);
            }
        } else if (a.size() == 0 && b.size() > 0) {
            for (String str : b) {
                tmp.add(str);
            }
        }
        return tmp;
    }

    private int findClosestBracet(String str) // Ищем закрывающую скобку, пропускаю вложенные
    {
        int index = 0, count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ')' && count == 0) {
                index = i;
                break;
            } else if (str.charAt(i) == '(') {
                count++;
            } else if (str.charAt(i) == ')') {
                count--;
            }
        }
        return index + 1;
    }

    public List<String> solve(String str, int minLenght, int maxLenght) {
        //return recursion(str, minLenght, maxLenght).FindAll(x => x.Length >= minLenght & x.Length <= maxLenght);
        return recursion(str, minLenght, maxLenght).stream().filter(x -> x.length() >= minLenght && x.length() <= maxLenght).toList();
    }

    /*public static void main(String[] args) {
        RegExpGenerator generator = new RegExpGenerator();
        System.out.println(generator.solve("(a+b)*",0, 2));
    }*/
}