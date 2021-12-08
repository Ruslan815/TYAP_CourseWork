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
                } else if (Character.isLetter(substr.charAt(i))) // Если встретили символ алфавита //  || substr.charAt(i) == '!'
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

    public static void main(String[] args) {
        RegExpGenerator generator = new RegExpGenerator();
        /*List<String> list1 = generator.solve("(y+((c+ys*x)(as*x+z)*)a)(s)*",0, 4);
        List<String> list2 = generator.solve("((c((z)*(a(xz*a+s)*)))+(y(xz*a+s)*))",0, 4);
        for (String str1 : list1) {
            System.out.print(str1 + " : ");
            if (!list2.contains(str1)) {
                System.out.print("NO");
            }
            System.out.println();
        }

        System.out.println("\n");
        for (String str2 : list2) {
            System.out.print(str2 + " : ");
            if (!list1.contains(str2)) {
                System.out.print("NO");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println(); // "(b*(a+c(eb*c+f)*(d+eb*a)))" // (g+fb*c)* // (a+b)* // (y+((c+ys*x)(as*x+z)*)a)(s)**/

        //System.out.println(generator.solve("(y((s)*(!+x((z+a(s)*x)*(a(s)*!))))+c((z+a(s)*x)*(a(s)*!)))", 0, 3));
        //System.out.println(generator.solve("(y+y((s)*(s+x((z+a(s)*x)*(a+a(s)*s))))+c((z+a(s)*x)*(a+a(s)*s)))", 0, 4));
        //System.out.println(generator.solve("(y+c+y((s)*(s+x+x((z+(s)*x)*(z+(s)*s+(s)*x))))+c((z+(s)*x)*(z+(s)*s+(s)*x)))", 0, 3));
        //System.out.println(generator.solve("(y+y((s)*(s+x((z+a(s)*x+k(s)*x)*((a+k)+a(s)*s+k(s)*s))))+c((z+a(s)*x+k(s)*x)*((a+k)+a(s)*s+k(s)*s)))", 0, 3));
        //System.out.println(generator.solve("((b)*(a+c((f+e(b)*c)*(d+e(b)*a))))", 0, 3));
        //System.out.println(generator.solve("(((b+z))*(a+c((f+e((b+z))*c)*(d+e((b+z))*a))))", 0, 3));
        //System.out.println(generator.solve("(a+c((f+ec)*(d+ea)))", 0, 3));
        //System.out.println(generator.solve("((b)*(a+c((g+f(b)*c)*(e+h((z+k(b)*c(g+f(b)*c)*h+k(b)*c(g+f(b)*c)*fd+k(b)*c(g+f(b)*c)*(b)*d+k(b)*d+m(g+f(b)*c)*h+m(g+f(b)*c)*fd+m(g+f(b)*c)*(b)*d)*(i+k(b)*a+k(b)*c(g+f(b)*c)*e+k(b)*c(g+f(b)*c)*fa+k(b)*c(g+f(b)*c)*(b)*a+m(g+f(b)*c)*e+m(g+f(b)*c)*fa+m(g+f(b)*c)*(b)*a))+f(b)*a+f(b)*d((z+k(b)*c(g+f(b)*c)*h+k(b)*c(g+f(b)*c)*fd+k(b)*c(g+f(b)*c)*(b)*d+k(b)*d+m(g+f(b)*c)*h+m(g+f(b)*c)*fd+m(g+f(b)*c)*(b)*d)*(i+k(b)*a+k(b)*c(g+f(b)*c)*e+k(b)*c(g+f(b)*c)*fa+k(b)*c(g+f(b)*c)*(b)*a+m(g+f(b)*c)*e+m(g+f(b)*c)*fa+m(g+f(b)*c)*(b)*a))))+d((z+k(b)*c(g+f(b)*c)*h+k(b)*c(g+f(b)*c)*fd+k(b)*c(g+f(b)*c)*(b)*d+k(b)*d+m(g+f(b)*c)*h+m(g+f(b)*c)*fd+m(g+f(b)*c)*(b)*d)*(i+k(b)*a+k(b)*c(g+f(b)*c)*e+k(b)*c(g+f(b)*c)*fa+k(b)*c(g+f(b)*c)*(b)*a+m(g+f(b)*c)*e+m(g+f(b)*c)*fa+m(g+f(b)*c)*(b)*a))))", 0, 3));
        System.out.println(generator.solve("((b)*(a+d((z+k(b)*d)*(i+k(b)*a))))", 0, 3));
    }
}