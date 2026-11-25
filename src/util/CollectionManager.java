package util;

import java.util.*;

// Collections Framework demonstration
public class CollectionManager {
    public void demonstrate() {
        List<String> arrayList = new ArrayList<>(List.of("A", "B", "C"));
        List<String> linkedList = new LinkedList<>(List.of("X", "Y"));

        Set<String> hashSet = new HashSet<>(List.of("1", "2", "1"));
        Set<String> linkedHashSet = new LinkedHashSet<>(List.of("a", "b", "a"));
        Set<String> treeSet = new TreeSet<>(List.of("z", "a", "m"));

        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("one", 1);
        Map<String, Integer> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("first", 1);
        Map<String, Integer> treeMap = new TreeMap<>();
        treeMap.put("x", 10);

        Queue<String> queue = new PriorityQueue<>();
        queue.add("high");
        Deque<String> deque = new LinkedList<>();
        deque.addFirst("top");

        System.out.println("ArrayList: " + arrayList);
        System.out.println("LinkedList: " + linkedList);
        System.out.println("HashSet (unique): " + hashSet);
        System.out.println("LinkedHashSet (insertion order): " + linkedHashSet);
        System.out.println("TreeSet (sorted): " + treeSet);
        System.out.println("HashMap: " + hashMap);
        System.out.println("LinkedHashMap: " + linkedHashMap);
        System.out.println("TreeMap (sorted keys): " + treeMap);
        System.out.println("PriorityQueue poll: " + queue.poll());
        System.out.println("Deque peekFirst: " + deque.peekFirst());

        // Iterator
        System.out.print("Iterator over LinkedList: ");
        Iterator<String> it = linkedList.iterator();
        while (it.hasNext()) System.out.print(it.next() + " ");
        System.out.println();

        // ListIterator
        System.out.print("ListIterator over ArrayList: ");
        ListIterator<String> lit = arrayList.listIterator();
        while (lit.hasNext()) System.out.print(lit.next() + " ");
        System.out.println();

        // Collections utility methods
        Collections.sort(arrayList);
        System.out.println("Sorted ArrayList: " + arrayList);
        Collections.shuffle(arrayList);
        System.out.println("Shuffled ArrayList: " + arrayList);

        // Wrapper classes and autoboxing/unboxing
        List<Integer> numbers = new ArrayList<>();
        numbers.add(10); // autoboxing int -> Integer
        int val = numbers.get(0); // unboxing
        System.out.println("Autoboxed/unboxed value: " + val);
    }
}
