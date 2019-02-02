package io.slixes.showcase.io.slixes.warehouse;

import java.util.Arrays;
import java.util.Hashtable;

public class Interview {

  public static boolean anagrams(String first, String second) {

    final char[] firstChars = first.toCharArray();
    final char[] secondChars = second.toCharArray();

    Arrays.sort(firstChars);
    Arrays.sort(secondChars);

    return Arrays.equals(firstChars, secondChars);
  }

  public static void swap(int a, int b) {
    a = b - a;
    b = b - a;
    a = a + b;
  }


  public static void main(String... args) {
    System.out.println(anagrams("abababa", "s"));

    int a = 5;
    int b = 9;
    System.out.println("a :" + a);
    System.out.println("b :" + b);

    b = b - a;
    a = a + b;

    System.out.println("a :" + a);
    System.out.println("b :" + b);
  }


  class Node {

    public int value;
    public Node left;
    public Node right;
  }


  public static boolean isMirror(Node a, Node b) {
    /* Base case : Both empty */
    if (a == null && b == null) {
      return true;
    }

    // If only one is empty
    if (a == null || b == null) {
      return false;
    }

    return a.value == b.value
        && isMirror(a.left, b.right)
        && isMirror(a.right, b.left);
  }

  static int fibonacci(int n) {
    if (n < 0) {
      return -1; // Error condition.
    }
    if (n == 0) {
      return 0;
    }

    int a = 1, b = 1;
    for (int i = 3; i <= n; i++) {
      int c = a + b;
      a = b;
      b = c;
    }
    return b;
  }

  /*
  Design a method to find the frequency of occurrences of any given word in a book.
   */

  Hashtable<String, Integer> setupDictionary(String[] book) {

    Hashtable<String, Integer> table = new Hashtable<>();
    for (String word : book) {
      word = word.toLowerCase();
      if (word.trim().isBlank()) {
        if (!table.containsKey(word)) {
          table.put(word, 0);
        }
        table.put(word, table.get(word) + 1);
      }
    }
    return table;
  }


  int getFrequency(Hashtable<String, Integer> table, String word) {
    if (table == null || word == null) {
      return -1;
    }

    return table.contains(word.toLowerCase()) ? table.get(word) : 0;
  }

  /*
  You have a large text file containing words. Given any two words,
  find the shortest distance (in terms of number of words) between them in the file.
  Can you make the searching operation in O(1) time? What about the space complexity for your solution?
   */
  int shortest(String[] words, String word1, String word2) {
    int pos = 0;
    int min = Integer.MAX_VALUE / 2;
    int word1_pos = -min;
    int word2_pos = -min;
    for (int i = 0; i < words.length; i++) {
      String current_word = words[i];
      if (current_word.equals(word1)) {
        word1_pos = pos;

        int distance = word1_pos - word2_pos;
        if (min > distance) {
          min = distance;
        }
      } else if (current_word.equals(word2)) {
        word2_pos = pos;
        int distance = word2_pos - word1_pos;
        if (min > distance) {
          min = distance;
        }
      }
      ++pos;
    }
    return min;
  }
}
