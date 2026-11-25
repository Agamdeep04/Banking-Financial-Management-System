
package sort;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SortManager {

    public void sortAndFilter(Scanner sc) {

        List<String> accounts = new ArrayList<>(
                List.of("ACC1001", "ACC1002", "ACC1003", "ACC1001")
        );

        System.out.println("Original: " + accounts);

        List<String> result = accounts.stream()
                .filter(s -> s.startsWith("ACC"))
                .map(String::toUpperCase)
                .distinct()
                .sorted()
                .limit(3)
                .skip(1)
                .collect(Collectors.toList());

        System.out.println("Stream result: " + result);

        Comparator<String> byLength =
                (a, b) -> Integer.compare(a.length(), b.length());

        List<String> sortedByLength = new ArrayList<>(accounts);

        Collections.sort(sortedByLength, byLength);

        System.out.println("Sorted by length: " + sortedByLength);

        Predicate<String> longAccounts = s -> s.length() > 8;

        List<String> filtered = accounts.stream()
                .filter(longAccounts)
                .collect(Collectors.toList());

        System.out.println("Accounts length > 8: " + filtered);
    }
}
