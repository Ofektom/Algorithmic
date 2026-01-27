package trie;

/**
 * Implement Trie (Prefix Tree) - LeetCode 208
 * A trie (pronounced as "try") or prefix tree is a tree data structure used to efficiently
 * store and retrieve keys in a dataset of strings. There are various applications of this
 * data structure, such as autocomplete and spellchecker.
 * Implement the Trie class:
 * - Trie() Initializes the trie object.
 * - void insert(String word) Inserts the string word into the trie.
 * - boolean search(String word) Returns true if the string word is in the trie (i.e., was inserted before), and false otherwise.
 * - boolean startsWith(String prefix) Returns true if there is a previously inserted string word that has the prefix prefix, and false otherwise.
 */
public class ImplementTrie {

    static class TrieNode {
        TrieNode[] children;
        boolean isEndOfWord;

        TrieNode() {
            children = new TrieNode[26];
            isEndOfWord = false;
        }
    }

    private TrieNode root;

    public ImplementTrie() {
        root = new TrieNode();
    }

    /**
     * Insert a word into the trie
     * Time Complexity: O(m) where m is the length of the word
     * Space Complexity: O(m)
     */
    public void insert(String word) {
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }
            current = current.children[index];
        }
        current.isEndOfWord = true;
    }

    /**
     * Search for a word in the trie
     * Time Complexity: O(m) where m is the length of the word
     * Space Complexity: O(1)
     */
    public boolean search(String word) {
        TrieNode node = searchPrefix(word);
        return node != null && node.isEndOfWord;
    }

    /**
     * Check if any word starts with the given prefix
     * Time Complexity: O(m) where m is the length of the prefix
     * Space Complexity: O(1)
     */
    public boolean startsWith(String prefix) {
        return searchPrefix(prefix) != null;
    }

    private TrieNode searchPrefix(String prefix) {
        TrieNode current = root;
        for (char c : prefix.toCharArray()) {
            int index = c - 'a';
            if (current.children[index] == null) {
                return null;
            }
            current = current.children[index];
        }
        return current;
    }

    // Static methods for testing
    public static ImplementTrie createTrie() {
        return new ImplementTrie();
    }

    public static void insert(ImplementTrie trie, String word) {
        trie.insert(word);
    }

    public static boolean search(ImplementTrie trie, String word) {
        return trie.search(word);
    }

    public static boolean startsWith(ImplementTrie trie, String prefix) {
        return trie.startsWith(prefix);
    }
}

