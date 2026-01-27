package com.algorithm.web.service;

import com.algorithm.web.model.ProblemInfo;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.*;

@Service
public class ProblemRegistryService {
    private final Map<String, List<ProblemInfo>> categoryMap = new HashMap<>();
    private final List<ProblemInfo> allProblems = new ArrayList<>();

    @PostConstruct
    public void initialize() {
        initializeRegistry();
    }

    public List<String> getCategories() {
        return Arrays.asList(
                "Arrays & Hashing", "Strings", "Trees", "Linked List",
                "Two Pointers", "Sliding Window", "Stack", "Search",
                "Dynamic Programming", "Graphs", "Heap/Priority Queue",
                "Backtracking", "Trie", "Intervals", "Math/Geometry",
                "Greedy", "Bit Manipulation");
    }

    public List<ProblemInfo> getProblemsForCategory(String category) {
        return categoryMap.getOrDefault(category, new ArrayList<>());
    }

    public List<ProblemInfo> getAllProblems() {
        Map<String, ProblemInfo> unique = new LinkedHashMap<>();
        for (ProblemInfo p : allProblems) {
            if (p == null)
                continue;
            String key;
            if (p.getClassName() != null && !p.getClassName().isEmpty()) {
                key = p.getClassName();
            } else if (p.getName() != null && !p.getName().isEmpty()) {
                key = p.getName();
            } else if (p.getAliases() != null && !p.getAliases().isEmpty() && p.getAliases().get(0) != null
                    && !p.getAliases().get(0).isEmpty()) {
                key = p.getAliases().get(0);
            } else {
                key = java.util.UUID.randomUUID().toString();
            }
            unique.putIfAbsent(key.toLowerCase(), p);
        }
        return new ArrayList<>(unique.values());
    }

    public ProblemInfo findProblem(String name) {
        return allProblems.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name) ||
                        p.getAliases().stream().anyMatch(a -> a.equalsIgnoreCase(name)))
                .findFirst()
                .orElse(null);
    }

    public List<ProblemInfo> searchProblems(String query) {
        String lowerQuery = query.toLowerCase().trim();
        List<ProblemInfo> results = new ArrayList<>();
        Map<ProblemInfo, Double> scoredResults = new HashMap<>();

        for (ProblemInfo problem : allProblems) {
            double score = calculateMatchScore(lowerQuery, problem);
            if (score > 0) {
                scoredResults.put(problem, score);
            }
        }

        // Sort by score (highest first) and return top results
        return scoredResults.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(20) // Limit to top 20 results
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toList());
    }

    private double calculateMatchScore(String query, ProblemInfo problem) {
        double score = 0.0;
        String lowerName = problem.getName().toLowerCase();
        String lowerDescription = problem.getDescription().toLowerCase();
        String lowerShortDesc = problem.getShortDescription() != null ? problem.getShortDescription().toLowerCase()
                : "";

        // For long queries (LeetCode-style problem descriptions), prioritize
        // description matching
        boolean isLongQuery = query.length() > 50;

        // Exact name match - highest score
        if (lowerName.equals(query)) {
            score += 100.0;
        }
        // Name contains query - high score
        else if (lowerName.contains(query)) {
            score += 80.0;
        }
        // Fuzzy match on name
        else {
            double nameSimilarity = calculateSimilarity(query, lowerName);
            if (nameSimilarity > 0.7) {
                score += nameSimilarity * 60.0;
            }
        }

        // Check aliases
        for (String alias : problem.getAliases()) {
            String lowerAlias = alias.toLowerCase();
            if (lowerAlias.equals(query)) {
                score += 90.0;
            } else if (lowerAlias.contains(query)) {
                score += 70.0;
            } else {
                double aliasSimilarity = calculateSimilarity(query, lowerAlias);
                if (aliasSimilarity > 0.7) {
                    score += aliasSimilarity * 50.0;
                }
            }
        }

        // Check keywords
        for (String keyword : problem.getKeywords()) {
            String lowerKeyword = keyword.toLowerCase();
            if (lowerKeyword.equals(query)) {
                score += 40.0;
            } else if (lowerKeyword.contains(query) || query.contains(lowerKeyword)) {
                score += 20.0;
            }
        }

        // Enhanced description matching for LeetCode-style queries
        if (isLongQuery) {
            // For long queries, use more sophisticated matching
            score += matchDescription(query, lowerDescription, lowerShortDesc);
            // Extract and match key phrases from long queries (LeetCode problem
            // descriptions)
            score += matchKeyPhrases(query, lowerDescription, lowerShortDesc);
        } else {
            // For short queries, use simpler matching
            if (lowerDescription.contains(query)) {
                score += 15.0;
            }
            if (!lowerShortDesc.isEmpty() && lowerShortDesc.contains(query)) {
                score += 12.0;
            }
        }

        // Check for LeetCode number patterns (e.g., "leetcode 1", "problem 1", "two
        // sum")
        if (query.matches(".*\\b(leetcode|problem|question)\\s*\\d+.*")) {
            // Extract number if present
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\b(\\d+)\\b");
            java.util.regex.Matcher matcher = pattern.matcher(query);
            if (matcher.find()) {
                String number = matcher.group(1);
                // Some problems might have numbers in their description
                if (lowerDescription.contains(number)) {
                    score += 25.0;
                }
            }
        }

        return score;
    }

    private double matchDescription(String query, String description, String shortDescription) {
        double score = 0.0;

        // Extract important words from query (remove common stop words)
        Set<String> stopWords = Set.of("the", "a", "an", "and", "or", "but", "in", "on", "at",
                "to", "for", "of", "with", "by", "from", "as", "is", "are", "was", "were", "be",
                "been", "being", "have", "has", "had", "do", "does", "did", "will", "would",
                "should", "could", "may", "might", "must", "can", "this", "that", "these", "those",
                "you", "your", "we", "they", "it", "its", "given", "return", "returning");

        Set<String> queryWords = new HashSet<>();
        String[] words = query.toLowerCase().split("\\s+");
        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-z]", "");
            if (cleanWord.length() > 2 && !stopWords.contains(cleanWord)) {
                queryWords.add(cleanWord);
            }
        }

        // Extract important phrases (2-3 word combinations)
        Set<String> queryPhrases = new HashSet<>();
        for (int i = 0; i < words.length - 1; i++) {
            String phrase = words[i] + " " + words[i + 1];
            String cleanPhrase = phrase.replaceAll("[^a-z\\s]", "");
            if (cleanPhrase.length() > 5) {
                queryPhrases.add(cleanPhrase);
            }
        }

        // Match words in description
        int matchedWords = 0;
        for (String word : queryWords) {
            if (description.contains(word)) {
                matchedWords++;
                score += 5.0; // Base score per matched word
            }
        }

        // Bonus for matching multiple words
        if (queryWords.size() > 0) {
            double wordMatchRatio = (double) matchedWords / queryWords.size();
            score += wordMatchRatio * 30.0; // Up to 30 points for word matching
        }

        // Match phrases in description (higher weight)
        int matchedPhrases = 0;
        for (String phrase : queryPhrases) {
            if (description.contains(phrase)) {
                matchedPhrases++;
                score += 15.0; // Higher score for phrase matches
            }
        }

        // Bonus for matching multiple phrases
        if (queryPhrases.size() > 0) {
            double phraseMatchRatio = (double) matchedPhrases / queryPhrases.size();
            score += phraseMatchRatio * 40.0; // Up to 40 points for phrase matching
        }

        // Check if description contains the full query (or large portion)
        if (description.contains(query)) {
            score += 50.0; // High score for exact description match
        } else {
            // Calculate text similarity for long queries
            if (query.length() > 50 && description.length() > 50) {
                double similarity = calculateTextSimilarity(query, description);
                if (similarity > 0.3) {
                    score += similarity * 35.0; // Up to 35 points for text similarity
                }
            }
        }

        // Check short description
        if (!shortDescription.isEmpty()) {
            int shortMatchedWords = 0;
            for (String word : queryWords) {
                if (shortDescription.contains(word)) {
                    shortMatchedWords++;
                }
            }
            if (queryWords.size() > 0) {
                double shortWordRatio = (double) shortMatchedWords / queryWords.size();
                score += shortWordRatio * 20.0;
            }
        }

        return score;
    }

    private double matchKeyPhrases(String query, String description, String shortDesc) {
        double score = 0.0;
        String lowerQuery = query.toLowerCase();

        // Extract significant phrases (2-4 words) from the query dynamically
        String[] queryWords = extractSignificantWords(lowerQuery);
        if (queryWords.length < 2) {
            return 0.0;
        }

        // Generate 2-word, 3-word, and 4-word phrases from query
        java.util.List<String> queryPhrases = new java.util.ArrayList<>();
        for (int i = 0; i < queryWords.length - 1; i++) {
            // 2-word phrases
            queryPhrases.add(queryWords[i] + " " + queryWords[i + 1]);

            // 3-word phrases
            if (i < queryWords.length - 2) {
                queryPhrases.add(queryWords[i] + " " + queryWords[i + 1] + " " + queryWords[i + 2]);
            }

            // 4-word phrases
            if (i < queryWords.length - 3) {
                queryPhrases.add(queryWords[i] + " " + queryWords[i + 1] + " " +
                        queryWords[i + 2] + " " + queryWords[i + 3]);
            }
        }

        // Match each phrase against description
        for (String phrase : queryPhrases) {
            if (phrase.length() < 5)
                continue; // Skip very short phrases

            // Exact phrase match - highest score
            if (description.contains(phrase)) {
                score += 35.0; // Higher score for longer, more specific phrases
            } else if (!shortDesc.isEmpty() && shortDesc.contains(phrase)) {
                score += 30.0;
            } else {
                // Fuzzy phrase matching - check if most words in phrase appear in description
                String[] phraseWords = phrase.split("\\s+");
                int matchingWords = 0;
                for (String phraseWord : phraseWords) {
                    if (phraseWord.length() > 3) { // Only check significant words
                        if (description.contains(phraseWord) ||
                                (!shortDesc.isEmpty() && shortDesc.contains(phraseWord))) {
                            matchingWords++;
                        }
                    }
                }

                // If most words match, give partial score
                if (phraseWords.length > 0) {
                    double matchRatio = (double) matchingWords / phraseWords.length;
                    if (matchRatio >= 0.7) {
                        score += matchRatio * 25.0; // Up to 25 points for partial matches
                    }
                }
            }
        }

        // Common LeetCode problem patterns (generic, not problem-specific)
        String[] commonPatterns = {
                "you are given", "you may assume", "return the", "given an",
                "find the", "determine if", "check if", "return true", "return false",
                "non-empty", "non negative", "non-negative", "single digit",
                "reverse order", "stored in", "contains a", "each node",
                "leading zero", "no leading", "do not contain"
        };

        for (String pattern : commonPatterns) {
            if (lowerQuery.contains(pattern) && description.contains(pattern)) {
                score += 15.0; // Bonus for matching common LeetCode patterns
            }
        }

        return score;
    }

    private String[] extractSignificantWords(String text) {
        // Extract words that are significant (not common stop words)
        String[] stopWords = {
                "the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for",
                "of", "with", "by", "from", "as", "is", "are", "was", "were", "be",
                "been", "being", "have", "has", "had", "do", "does", "did", "will",
                "would", "should", "could", "may", "might", "must", "can", "this",
                "that", "these", "those", "you", "your", "yours", "we", "our", "they",
                "their", "them", "it", "its", "i", "me", "my", "mine", "he", "she",
                "his", "her", "him", "hers", "given", "return", "returned", "itself"
        };

        java.util.Set<String> stopWordSet = new java.util.HashSet<>(java.util.Arrays.asList(stopWords));
        String[] words = text.toLowerCase().split("\\s+");
        java.util.List<String> significantWords = new java.util.ArrayList<>();

        for (String word : words) {
            // Remove punctuation and check if it's a significant word
            String cleanWord = word.replaceAll("[^a-z0-9]", "");
            if (cleanWord.length() > 2 && !stopWordSet.contains(cleanWord)) {
                significantWords.add(cleanWord);
            }
        }

        return significantWords.toArray(new String[0]);
    }

    private double calculateTextSimilarity(String text1, String text2) {
        // Use Jaccard similarity for longer texts
        Set<String> words1 = new HashSet<>(Arrays.asList(text1.toLowerCase().split("\\s+")));
        Set<String> words2 = new HashSet<>(Arrays.asList(text2.toLowerCase().split("\\s+")));

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);

        if (union.isEmpty())
            return 0.0;

        return (double) intersection.size() / union.size();
    }

    private double calculateSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null)
            return 0.0;
        if (s1.equals(s2))
            return 1.0;
        if (s1.isEmpty() || s2.isEmpty())
            return 0.0;

        int maxLength = Math.max(s1.length(), s2.length());
        if (maxLength == 0)
            return 1.0;

        int distance = levenshteinDistance(s1, s2);
        return 1.0 - ((double) distance / maxLength);
    }

    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), // deletion or insertion
                            dp[i - 1][j - 1] + 1 // substitution
                    );
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }

    private void initializeRegistry() {
        addProblemsToCategory("Arrays & Hashing", createArrayProblems());
        addProblemsToCategory("Strings", createStringProblems());
        addProblemsToCategory("Trees", createTreeProblems());
        addProblemsToCategory("Linked List", createLinkedListProblems());
        addProblemsToCategory("Two Pointers", createTwoPointersProblems());
        addProblemsToCategory("Sliding Window", createSlidingWindowProblems());
        addProblemsToCategory("Stack", createStackProblems());
        addProblemsToCategory("Search", createSearchProblems());
        addProblemsToCategory("Dynamic Programming", createDPProblems());
        addProblemsToCategory("Graphs", createGraphProblems());
        addProblemsToCategory("Heap/Priority Queue", createHeapProblems());
        addProblemsToCategory("Backtracking", createBacktrackProblems());
        addProblemsToCategory("Trie", createTrieProblems());
        addProblemsToCategory("Intervals", createIntervalsProblems());
        addProblemsToCategory("Math/Geometry", createMathProblems());
        addProblemsToCategory("Greedy", createGreedyProblems());
        addProblemsToCategory("Bit Manipulation", createBitManipProblems());
    }

    private void addProblemsToCategory(String category, ProblemInfo[] problems) {
        categoryMap.put(category, Arrays.asList(problems));
        allProblems.addAll(Arrays.asList(problems));
    }

    // Arrays & Hashing - 13 problems
    private ProblemInfo[] createArrayProblems() {
        return new ProblemInfo[] {
                new ProblemInfo("Two Sum",
                        Arrays.asList("TwoSum", "2Sum"),
                        "Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target. You may assume that each input would have exactly one solution, and you may not use the same element twice. You can return the answer in any order.",
                        "Find two numbers that add up to target",
                        Arrays.asList("array", "integers", "target", "indices", "add", "sum", "two", "numbers"),
                        "Arrays & Hashing",
                        "arrays.TwoSum",
                        Arrays.asList("computeWithHashMap", "computeWithHashMapTwoPass", "computeWithBruteForce"),
                        Arrays.asList("nums", "target"),
                        Arrays.asList("int[]", "int"),
                        new Object[] { new int[] { 2, 7, 11, 15 }, 9 }),

                new ProblemInfo("Contains Duplicate",
                        Arrays.asList("ContainsDuplicate", "Duplicate"),
                        "Given an integer array nums, return true if any value appears at least twice in the array, and return false if every element is distinct.",
                        "Check if array has duplicate values",
                        Arrays.asList("array", "duplicate", "appears", "twice", "distinct"),
                        "Arrays & Hashing",
                        "arrays.ContainsDuplicate",
                        Arrays.asList("computeWithHashSet", "computeWithStream", "computeWithSorting",
                                "computeWithBruteForce"),
                        Arrays.asList("nums"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { 1, 2, 3, 1 } }),

                new ProblemInfo("Missing Number",
                        Arrays.asList("MissingNumber", "Missing"),
                        "Given an array nums containing n distinct numbers in the range [0, n], return the only number in the range that is missing from the array.",
                        "Find the missing number in array [0, n]",
                        Arrays.asList("array", "missing", "number", "range", "distinct"),
                        "Arrays & Hashing",
                        "arrays.MissingNumber",
                        Arrays.asList("computeWithHashSet", "computeWithMath", "computeWithXOR", "computeWithSorting"),
                        Arrays.asList("nums"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { 3, 0, 1 } }),

                new ProblemInfo("Single Number",
                        Arrays.asList("SingleNumber", "Single"),
                        "Given a non-empty array of integers nums, every element appears twice except for one. Find that single one.",
                        "Find the number that appears only once",
                        Arrays.asList("array", "single", "appears", "twice", "once", "unique"),
                        "Arrays & Hashing",
                        "arrays.SingleNumber",
                        Arrays.asList("computeWithXOR", "computeWithHashSet", "computeWithHashMap"),
                        Arrays.asList("nums"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { 2, 2, 1 } }),

                new ProblemInfo("Plus One",
                        Arrays.asList("PlusOne", "Increment Array"),
                        "You are given a large integer represented as an integer array digits, where each digits[i] is the ith digit of the integer. Increment the large integer by one and return the resulting array of digits.",
                        "Increment a number represented as array by one",
                        Arrays.asList("array", "digits", "increment", "plus", "one", "large", "integer"),
                        "Arrays & Hashing",
                        "arrays.PlusOne",
                        Arrays.asList("computeIterative", "computeWithArrayList", "computeWithRecursion"),
                        Arrays.asList("digits"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { 1, 2, 3 } }),

                new ProblemInfo("Count Even Numbers",
                        Arrays.asList("CountEven", "Count Even"),
                        "Given an array of integers, count how many numbers are even.",
                        "Count even numbers in array",
                        Arrays.asList("array", "count", "even", "numbers", "integers"),
                        "Arrays & Hashing",
                        "arrays.CountEven",
                        Arrays.asList("compute", "computeWithStream", "computeWithRecursion", "computeWithWhile"),
                        Arrays.asList("nums"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { 1, 2, 3, 4, 5, 6 } }),

                new ProblemInfo("Product of Array Except Self",
                        Arrays.asList("ProductExceptSelf", "Array Product"),
                        "Given an integer array nums, return an array answer such that answer[i] is equal to the product of all the elements of nums except nums[i]. You must write an algorithm that runs in O(n) time and without using the division operator.",
                        "Return array where each element is product of all others",
                        Arrays.asList("array", "product", "except", "self", "prefix", "suffix"),
                        "Arrays & Hashing",
                        "arrays.ProductExceptSelf",
                        Arrays.asList("computeWithPrefixSuffix", "computeWithOptimizedSpace", "computeWithRecursion"),
                        Arrays.asList("nums"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { 1, 2, 3, 4 } }),

                new ProblemInfo("Top K Frequent Elements",
                        Arrays.asList("TopKFrequent", "K Most Frequent", "Top K"),
                        "Given an integer array nums and an integer k, return the k most frequent elements. You may return the answer in any order.",
                        "Find k most frequent elements in array",
                        Arrays.asList("array", "frequent", "top", "k", "frequency", "elements", "most"),
                        "Arrays & Hashing",
                        "arrays.TopKFrequent",
                        Arrays.asList("computeWithHeap", "computeWithBucketSort", "computeWithSorting"),
                        Arrays.asList("nums", "k"),
                        Arrays.asList("int[]", "int"),
                        new Object[] { new int[] { 1, 1, 1, 2, 2, 3 }, 2 }),

                new ProblemInfo("Longest Consecutive Sequence",
                        Arrays.asList("LongestConsecutive", "Consecutive Sequence"),
                        "Given an unsorted array of integers nums, return the length of the longest consecutive elements sequence. You must write an algorithm that runs in O(n) time.",
                        "Find length of longest consecutive sequence",
                        Arrays.asList("array", "consecutive", "sequence", "longest", "unsorted"),
                        "Arrays & Hashing",
                        "arrays.LongestConsecutive",
                        Arrays.asList("computeWithHashSet", "computeWithRecursion", "computeWithSorting"),
                        Arrays.asList("nums"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { 100, 4, 200, 1, 3, 2 } }),

                new ProblemInfo("Maximum Subarray",
                        Arrays.asList("MaximumSubarray", "Max Subarray", "Kadane"),
                        "Given an integer array nums, find the contiguous subarray (containing at least one number) which has the largest sum and return its sum. A subarray is a contiguous part of an array.",
                        "Find maximum sum of contiguous subarray",
                        Arrays.asList("array", "subarray", "maximum", "sum", "contiguous", "kadane"),
                        "Arrays & Hashing",
                        "arrays.MaximumSubarray",
                        Arrays.asList("computeWithKadane", "computeWithDP", "computeWithDivideConquer",
                                "computeWithBruteForce"),
                        Arrays.asList("nums"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { -2, 1, -3, 4, -1, 2, 1, -5, 4 } }),

                new ProblemInfo("Maximum Product Subarray",
                        Arrays.asList("MaximumProductSubarray", "Max Product Subarray", "Product Subarray"),
                        "Given an integer array nums, find a contiguous non-empty subarray within the array that has the largest product, and return the product. The test cases are generated so that the answer will fit in a 32-bit integer.",
                        "Find maximum product of contiguous subarray",
                        Arrays.asList("array", "subarray", "maximum", "product", "contiguous"),
                        "Arrays & Hashing",
                        "arrays.MaximumProductSubarray",
                        Arrays.asList("computeWithDP", "computeWithTwoPasses", "computeWithBruteForce",
                                "computeWithRecursion"),
                        Arrays.asList("nums"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { 2, 3, -2, 4 } }),

                new ProblemInfo("Contains Duplicate II",
                        Arrays.asList("ContainsDuplicateII", "Duplicate II", "Duplicate Distance"),
                        "Given an integer array nums and an integer k, return true if there are two distinct indices i and j in the array such that nums[i] == nums[j] and abs(i - j) <= k.",
                        "Check if duplicate exists within k distance",
                        Arrays.asList("array", "duplicate", "distance", "k", "indices", "hashmap", "sliding window"),
                        "Arrays & Hashing",
                        "arrays.ContainsDuplicateII",
                        Arrays.asList("computeWithHashMap", "computeWithHashSet", "computeWithBruteForce"),
                        Arrays.asList("nums", "k"),
                        Arrays.asList("int[]", "int"),
                        new Object[] { new int[] { 1, 2, 3, 1 }, 3 }),

                new ProblemInfo("Rotate Array",
                        Arrays.asList("RotateArray", "Rotate", "Shift Array"),
                        "Given an integer array nums, rotate the array to the right by k steps, where k is non-negative.",
                        "Rotate array to the right by k steps",
                        Arrays.asList("array", "rotate", "shift", "k", "steps", "right"),
                        "Arrays & Hashing",
                        "arrays.RotateArray",
                        Arrays.asList("computeWithExtraArray", "computeWithReverse", "computeWithArrayList",
                                "computeWithCyclic"),
                        Arrays.asList("nums", "k"),
                        Arrays.asList("int[]", "int"),
                        new Object[] { new int[] { 1, 2, 3, 4, 5, 6, 7 }, 3 })
        };
    }

    // Strings - 18 problems
    private ProblemInfo[] createStringProblems() {
        return new ProblemInfo[] {
                new ProblemInfo("Valid Anagram",
                        Arrays.asList("IsAnagram", "Anagram", "Valid Anagram"),
                        "Given two strings s and t, return true if t is an anagram of s, and false otherwise. An Anagram is a word or phrase formed by rearranging the letters of a different word or phrase.",
                        "Check if two strings are anagrams",
                        Arrays.asList("string", "anagram", "rearrange", "letters", "characters"),
                        "Strings",
                        "strings.IsAnagram",
                        Arrays.asList("computeWithSorting", "computeWithHashMap", "computeWithArrayCounter",
                                "computeWithStringBuilder"),
                        Arrays.asList("s", "t"),
                        Arrays.asList("String", "String"),
                        new Object[] { "anagram", "nagaram" }),

                new ProblemInfo("Group Anagrams",
                        Arrays.asList("GroupAnagrams", "Anagram Groups"),
                        "Given an array of strings strs, group the anagrams together. You can return the answer in any order.",
                        "Group strings that are anagrams of each other",
                        Arrays.asList("string", "array", "anagram", "group", "sort"),
                        "Strings",
                        "strings.GroupAnagrams",
                        Arrays.asList("computeWithSortedKey", "computeWithStringBuilder", "computeWithCharCount"),
                        Arrays.asList("strs"),
                        Arrays.asList("String[]"),
                        new Object[] { new String[] { "eat", "tea", "tan", "ate", "nat", "bat" } }),

                new ProblemInfo("Ransom Note",
                        Arrays.asList("RansomNote", "Ransom"),
                        "Given two strings ransomNote and magazine, return true if ransomNote can be constructed by using the letters from magazine and false otherwise. Each letter in magazine can only be used once in ransomNote.",
                        "Check if ransom note can be constructed from magazine",
                        Arrays.asList("string", "ransom", "magazine", "construct", "letters"),
                        "Strings",
                        "strings.RansomNote",
                        Arrays.asList("computeWithHashMap", "computeWithArray", "computeWithStringBuilder"),
                        Arrays.asList("ransomNote", "magazine"),
                        Arrays.asList("String", "String"),
                        new Object[] { "aa", "aab" }),

                new ProblemInfo("Isomorphic Strings",
                        Arrays.asList("IsomorphicStrings", "Isomorphic"),
                        "Given two strings s and t, determine if they are isomorphic. Two strings s and t are isomorphic if the characters in s can be replaced to get t.",
                        "Check if two strings are isomorphic",
                        Arrays.asList("string", "isomorphic", "characters", "mapping", "replace"),
                        "Strings",
                        "strings.IsomorphicStrings",
                        Arrays.asList("computeWithTwoHashMaps", "computeWithHashMapHashSet", "computeWithArray"),
                        Arrays.asList("s", "t"),
                        Arrays.asList("String", "String"),
                        new Object[] { "egg", "add" }),

                new ProblemInfo("Word Pattern",
                        Arrays.asList("WordPattern", "Pattern"),
                        "Given a pattern and a string s, find if s follows the same pattern. Here follow means a full match, such that there is a bijection between a letter in pattern and a non-empty word in s.",
                        "Check if string follows word pattern",
                        Arrays.asList("string", "pattern", "word", "bijection", "mapping"),
                        "Strings",
                        "strings.WordPattern",
                        Arrays.asList("computeWithHashMap", "computeWithTwoHashMaps", "computeWithStringBuilder"),
                        Arrays.asList("pattern", "s"),
                        Arrays.asList("String", "String"),
                        new Object[] { "abba", "dog cat cat dog" }),

                new ProblemInfo("First Unique Character",
                        Arrays.asList("FirstUniqueCharacter", "First Unique", "Non-repeating"),
                        "Given a string s, find the first non-repeating character in it and return its index. If it does not exist, return -1.",
                        "Find first non-repeating character index",
                        Arrays.asList("string", "unique", "non-repeating", "character", "index", "first"),
                        "Strings",
                        "strings.FirstUniqueCharacter",
                        Arrays.asList("computeWithHashMap", "computeWithArray", "computeWithStringMethods"),
                        Arrays.asList("s"),
                        Arrays.asList("String"),
                        new Object[] { "leetcode" }),

                new ProblemInfo("Longest Common Prefix",
                        Arrays.asList("LongestCommonPrefix", "Common Prefix", "LCP"),
                        "Write a function to find the longest common prefix string amongst an array of strings. If there is no common prefix, return an empty string.",
                        "Find longest common prefix among strings",
                        Arrays.asList("string", "array", "prefix", "common", "longest"),
                        "Strings",
                        "strings.LongestCommonPrefix",
                        Arrays.asList("computeHorizontal", "computeVertical", "computeWithStringBuilder"),
                        Arrays.asList("strs"),
                        Arrays.asList("String[]"),
                        new Object[] { new String[] { "flower", "flow", "flight" } }),

                new ProblemInfo("Longest Palindromic Substring",
                        Arrays.asList("LongestPalindromicSubstring", "Longest Palindrome", "Palindromic Substring"),
                        "Given a string s, return the longest palindromic substring in s.",
                        "Find longest palindromic substring",
                        Arrays.asList("string", "palindrome", "substring", "longest", "palindromic"),
                        "Strings",
                        "strings.LongestPalindromicSubstring",
                        Arrays.asList("computeExpandAroundCenters", "computeWithStringBuilder", "computeWithDP"),
                        Arrays.asList("s"),
                        Arrays.asList("String"),
                        new Object[] { "babad" }),

                new ProblemInfo("Valid Palindrome II",
                        Arrays.asList("ValidPalindromeII", "Valid Palindrome Two"),
                        "Given a string s, return true if the s can be palindrome after deleting at most one character from it.",
                        "Check if string can be palindrome after deleting one character",
                        Arrays.asList("string", "palindrome", "delete", "character", "valid"),
                        "Strings",
                        "strings.ValidPalindromeII",
                        Arrays.asList("computeWithTwoPointers", "computeWithStringBuilder", "computeWithRecursion"),
                        Arrays.asList("s"),
                        Arrays.asList("String"),
                        new Object[] { "aba" }),

                new ProblemInfo("Reverse String",
                        Arrays.asList("ReverseString", "Reverse"),
                        "Write a function that reverses a string. The input string is given as an array of characters s. You must do this by modifying the input array in-place with O(1) extra memory.",
                        "Reverse a string in-place",
                        Arrays.asList("string", "reverse", "array", "characters", "in-place"),
                        "Strings",
                        "strings.ReverseString",
                        Arrays.asList("computeWithTwoPointers", "computeWithStringBuilder", "computeWithRecursion"),
                        Arrays.asList("s"),
                        Arrays.asList("char[]"),
                        new Object[] { "hello".toCharArray() }),

                new ProblemInfo("Reverse Words in String",
                        Arrays.asList("ReverseWordsInString", "Reverse Words"),
                        "Given an input string s, reverse the order of the words. A word is defined as a sequence of non-space characters. The words in s will be separated by at least one space. Return a string of the words in reverse order concatenated by a single space.",
                        "Reverse the order of words in a string",
                        Arrays.asList("string", "words", "reverse", "order", "space"),
                        "Strings",
                        "strings.ReverseWordsInString",
                        Arrays.asList("computeWithBuiltIn", "computeWithStringBuilder", "computeWithTwoPass"),
                        Arrays.asList("s"),
                        Arrays.asList("String"),
                        new Object[] { "the sky is blue" }),

                new ProblemInfo("Backspace String Compare",
                        Arrays.asList("BackspaceStringCompare", "Backspace Compare"),
                        "Given two strings s and t, return true if they are equal when both are typed into empty text editors. '#' means a backspace character.",
                        "Compare strings with backspace characters",
                        Arrays.asList("string", "backspace", "compare", "character", "#"),
                        "Strings",
                        "strings.BackspaceStringCompare",
                        Arrays.asList("computeWithStack", "computeWithStringBuilder", "computeWithTwoPointers"),
                        Arrays.asList("s", "t"),
                        Arrays.asList("String", "String"),
                        new Object[] { "ab#c", "ad#c" }),

                new ProblemInfo("Find All Anagrams in String",
                        Arrays.asList("FindAllAnagramsInString", "Find Anagrams"),
                        "Given two strings s and p, return an array of all the start indices of p's anagrams in s. You may return the answer in any order.",
                        "Find all start indices of anagram substrings",
                        Arrays.asList("string", "anagram", "indices", "substring", "start"),
                        "Strings",
                        "strings.FindAllAnagramsInString",
                        Arrays.asList("computeWithHashMap", "computeWithArray", "computeWithStringBuilder"),
                        Arrays.asList("s", "p"),
                        Arrays.asList("String", "String"),
                        new Object[] { "cbaebabacd", "abc" }),

                new ProblemInfo("Decode String",
                        Arrays.asList("DecodeString", "Decode"),
                        "Given an encoded string, return its decoded string. The encoding rule is: k[encoded_string], where the encoded_string inside the square brackets is being repeated exactly k times.",
                        "Decode string with number-bracket pattern",
                        Arrays.asList("string", "decode", "brackets", "repeat", "number"),
                        "Strings",
                        "strings.DecodeString",
                        Arrays.asList("computeWithStack", "computeWithRecursion", "computeWithStringBuilder"),
                        Arrays.asList("s"),
                        Arrays.asList("String"),
                        new Object[] { "3[a]2[bc]" }),

                new ProblemInfo("String to Integer (atoi)",
                        Arrays.asList("StringToInteger", "Atoi", "String to Integer"),
                        "Implement the myAtoi(string s) function, which converts a string to a 32-bit signed integer (similar to C/C++'s atoi function).",
                        "Convert string to 32-bit signed integer",
                        Arrays.asList("string", "integer", "conversion", "atoi", "parse", "number"),
                        "Strings",
                        "strings.StringToInteger",
                        Arrays.asList("computeIterative", "computeWithStringBuilder", "computeWithRecursion"),
                        Arrays.asList("s"),
                        Arrays.asList("String"),
                        new Object[] { "42" }),

                new ProblemInfo("Implement strStr()",
                        Arrays.asList("ImplementStrStr", "StrStr", "Find Needle", "Needle in Haystack"),
                        "Given two strings needle and haystack, return the index of the first occurrence of needle in haystack, or -1 if needle is not part of haystack.",
                        "Find first occurrence of needle in haystack",
                        Arrays.asList("string", "strstr", "needle", "haystack", "substring", "index", "kmp"),
                        "Strings",
                        "strings.ImplementStrStr",
                        Arrays.asList("computeWithBruteForce", "computeWithStringMethods", "computeWithStringBuilder",
                                "computeWithKMP"),
                        Arrays.asList("haystack", "needle"),
                        Arrays.asList("String", "String"),
                        new Object[] { "sadbutsad", "sad" }),

                new ProblemInfo("Add Binary",
                        Arrays.asList("AddBinary", "Binary Add", "Binary Sum"),
                        "Given two binary strings a and b, return their sum as a binary string.",
                        "Add two binary strings",
                        Arrays.asList("string", "binary", "add", "sum", "carry", "stringbuilder"),
                        "Strings",
                        "strings.AddBinary",
                        Arrays.asList("computeWithStringBuilder", "computeWithBigInteger", "computeWithArray",
                                "computeRecursive"),
                        Arrays.asList("a", "b"),
                        Arrays.asList("String", "String"),
                        new Object[] { "11", "1" })
        };
    }

    // Trees - 14 problems
    private ProblemInfo[] createTreeProblems() {
        return new ProblemInfo[] {
                new ProblemInfo("Maximum Depth of Binary Tree",
                        Arrays.asList("MaximumDepthOfBinaryTree", "Max Depth", "Tree Depth"),
                        "Given the root of a binary tree, return its maximum depth. A binary tree's maximum depth is the number of nodes along the longest path from the root node down to the farthest leaf node.",
                        "Find the maximum depth of a binary tree",
                        Arrays.asList("tree", "binary tree", "depth", "maximum", "nodes", "root", "leaf", "path"),
                        "Trees",
                        "trees.MaximumDepthOfBinaryTree",
                        Arrays.asList("computeRecursive", "computeWithStack", "computeWithBFS"),
                        Arrays.asList("root"),
                        Arrays.asList("TreeNode"),
                        null),

                new ProblemInfo("Same Tree",
                        Arrays.asList("SameTree", "Identical Trees"),
                        "Given the roots of two binary trees p and q, return true if they are the same tree, and false otherwise. Two binary trees are considered the same if they are structurally identical, and the nodes have the same value.",
                        "Check if two binary trees are identical",
                        Arrays.asList("tree", "binary tree", "same", "identical", "structure", "value", "nodes"),
                        "Trees",
                        "trees.SameTree",
                        Arrays.asList("computeRecursive", "computeWithStack", "computeWithBFS"),
                        Arrays.asList("p", "q"),
                        Arrays.asList("TreeNode", "TreeNode"),
                        null),

                new ProblemInfo("Invert Binary Tree",
                        Arrays.asList("InvertBinaryTree", "Invert Tree", "Mirror Tree"),
                        "Given the root of a binary tree, invert the tree, and return its root.",
                        "Invert a binary tree",
                        Arrays.asList("tree", "binary tree", "invert", "mirror", "root"),
                        "Trees",
                        "trees.InvertBinaryTree",
                        Arrays.asList("computeRecursive", "computeWithStack", "computeWithQueue"),
                        Arrays.asList("root"),
                        Arrays.asList("TreeNode"),
                        null),

                new ProblemInfo("Symmetric Tree",
                        Arrays.asList("SymmetricTree", "Symmetric", "Mirror"),
                        "Given the root of a binary tree, check whether it is a mirror of itself (i.e., symmetric around its center).",
                        "Check if a binary tree is symmetric",
                        Arrays.asList("tree", "binary tree", "symmetric", "mirror", "center"),
                        "Trees",
                        "trees.SymmetricTree",
                        Arrays.asList("computeRecursive", "computeWithQueue", "computeWithStack"),
                        Arrays.asList("root"),
                        Arrays.asList("TreeNode"),
                        null),

                new ProblemInfo("Binary Tree Level Order Traversal",
                        Arrays.asList("BinaryTreeLevelOrderTraversal", "Level Order", "BFS"),
                        "Given the root of a binary tree, return the level order traversal of its nodes' values. (i.e., from left to right, level by level).",
                        "Return level order traversal of binary tree",
                        Arrays.asList("tree", "binary tree", "level order", "traversal", "bfs", "breadth first"),
                        "Trees",
                        "trees.BinaryTreeLevelOrderTraversal",
                        Arrays.asList("computeWithBFS", "computeWithDFS", "computeRecursive"),
                        Arrays.asList("root"),
                        Arrays.asList("TreeNode"),
                        null),

                new ProblemInfo("Validate Binary Search Tree",
                        Arrays.asList("ValidateBinarySearchTree", "Validate BST", "BST Validation"),
                        "Given the root of a binary tree, determine if it is a valid binary search tree (BST). A valid BST is defined as follows: The left subtree of a node contains only nodes with keys less than the node's key. The right subtree of a node contains only nodes with keys greater than the node's key. Both the left and right subtrees must also be binary search trees.",
                        "Check if binary tree is a valid BST",
                        Arrays.asList("tree", "binary tree", "bst", "binary search tree", "validate", "valid"),
                        "Trees",
                        "trees.ValidateBinarySearchTree",
                        Arrays.asList("computeRecursive", "computeWithInorder", "computeWithStack"),
                        Arrays.asList("root"),
                        Arrays.asList("TreeNode"),
                        null),

                new ProblemInfo("Binary Tree Inorder Traversal",
                        Arrays.asList("BinaryTreeInorderTraversal", "Inorder Traversal", "Inorder"),
                        "Given the root of a binary tree, return the inorder traversal of its nodes' values.",
                        "Return inorder traversal of binary tree",
                        Arrays.asList("tree", "binary tree", "inorder", "traversal", "dfs"),
                        "Trees",
                        "trees.BinaryTreeInorderTraversal",
                        Arrays.asList("computeRecursive", "computeWithStack", "computeMorris"),
                        Arrays.asList("root"),
                        Arrays.asList("TreeNode"),
                        null),

                new ProblemInfo("Binary Tree Maximum Path Sum",
                        Arrays.asList("BinaryTreeMaximumPathSum", "Max Path Sum", "Path Sum"),
                        "A path in a binary tree is a sequence of nodes where each pair of adjacent nodes in the sequence has an edge connecting them. A node can only appear in the sequence at most once. Note that the path does not need to pass through the root. The path sum of a path is the sum of the node's values in the path. Given the root of a binary tree, return the maximum path sum of any non-empty path.",
                        "Find maximum path sum in binary tree",
                        Arrays.asList("tree", "binary tree", "path", "sum", "maximum", "dfs"),
                        "Trees",
                        "trees.BinaryTreeMaximumPathSum",
                        Arrays.asList("computeRecursive", "computeWithArray"),
                        Arrays.asList("root"),
                        Arrays.asList("TreeNode"),
                        null),

                new ProblemInfo("Subtree of Another Tree",
                        Arrays.asList("SubtreeOfAnotherTree", "Subtree", "Contains Subtree"),
                        "Given the roots of two binary trees root and subRoot, return true if there is a subtree of root with the same structure and node values of subRoot and false otherwise. A subtree of a binary tree tree is a tree that consists of a node in tree and all of this node's descendants. The tree tree could also be considered as a subtree of itself.",
                        "Check if subRoot is a subtree of root",
                        Arrays.asList("tree", "binary tree", "subtree", "contains", "same structure"),
                        "Trees",
                        "trees.SubtreeOfAnotherTree",
                        Arrays.asList("computeRecursive", "computeWithSerialization"),
                        Arrays.asList("root", "subRoot"),
                        Arrays.asList("TreeNode", "TreeNode"),
                        null),

                new ProblemInfo("Lowest Common Ancestor of Binary Tree",
                        Arrays.asList("LowestCommonAncestorOfBinaryTree", "LCA", "Lowest Common Ancestor"),
                        "Given a binary tree, find the lowest common ancestor (LCA) of two given nodes in the tree. According to the definition of LCA on Wikipedia: 'The lowest common ancestor is defined between two nodes p and q as the lowest node in T that has both p and q as descendants (where we allow a node to be a descendant of itself).'",
                        "Find lowest common ancestor of two nodes",
                        Arrays.asList("tree", "binary tree", "lca", "lowest common ancestor", "nodes"),
                        "Trees",
                        "trees.LowestCommonAncestorOfBinaryTree",
                        Arrays.asList("computeRecursive", "computeWithPath"),
                        Arrays.asList("root", "p", "q"),
                        Arrays.asList("TreeNode", "TreeNode", "TreeNode"),
                        null),

                new ProblemInfo("Binary Tree Right Side View",
                        Arrays.asList("BinaryTreeRightSideView", "Right Side View", "Right View"),
                        "Given the root of a binary tree, imagine yourself standing on the right side of it, return the values of the nodes you can see ordered from top to bottom.",
                        "Return right side view of binary tree",
                        Arrays.asList("tree", "binary tree", "right side", "view", "bfs", "level order"),
                        "Trees",
                        "trees.BinaryTreeRightSideView",
                        Arrays.asList("computeWithBFS", "computeWithDFS", "computeRecursive"),
                        Arrays.asList("root"),
                        Arrays.asList("TreeNode"),
                        null),

                new ProblemInfo("Path Sum",
                        Arrays.asList("PathSum", "Tree Path Sum", "Sum Path"),
                        "Given the root of a binary tree and an integer targetSum, return true if the tree has a root-to-leaf path such that adding up all the values along the path equals targetSum. A leaf is a node with no children.",
                        "Check if tree has path with given sum",
                        Arrays.asList("tree", "binary tree", "path", "sum", "target", "root to leaf", "dfs"),
                        "Trees",
                        "trees.PathSum",
                        Arrays.asList("computeRecursive", "computeWithStack", "computeWithBFS"),
                        Arrays.asList("root", "targetSum"),
                        Arrays.asList("TreeNode", "int"),
                        null),

                new ProblemInfo("Construct Binary Tree from Preorder and Inorder",
                        Arrays.asList("ConstructBinaryTreeFromPreorderAndInorder", "Build Tree", "Construct Tree"),
                        "Given two integer arrays preorder and inorder where preorder is the preorder traversal of a binary tree and inorder is the inorder traversal of the same tree, construct and return the binary tree.",
                        "Construct binary tree from preorder and inorder",
                        Arrays.asList("tree", "binary tree", "construct", "build", "preorder", "inorder", "traversal"),
                        "Trees",
                        "trees.ConstructBinaryTreeFromPreorderAndInorder",
                        Arrays.asList("computeRecursive", "computeRecursiveLinear"),
                        Arrays.asList("preorder", "inorder"),
                        Arrays.asList("int[]", "int[]"),
                        new Object[] { new int[] { 3, 9, 20, 15, 7 }, new int[] { 9, 3, 15, 20, 7 } }),

                new ProblemInfo("Binary Tree Preorder Traversal",
                        Arrays.asList("BinaryTreePreorderTraversal", "Preorder Traversal", "Preorder"),
                        "Given the root of a binary tree, return the preorder traversal of its nodes' values.",
                        "Return preorder traversal of binary tree",
                        Arrays.asList("tree", "binary tree", "preorder", "traversal", "dfs"),
                        "Trees",
                        "trees.BinaryTreePreorderTraversal",
                        Arrays.asList("computeRecursive", "computeWithStack", "computeMorris"),
                        Arrays.asList("root"),
                        Arrays.asList("TreeNode"),
                        null)
        };
    }

    // Linked List - 10 problems
    private ProblemInfo[] createLinkedListProblems() {
        return new ProblemInfo[] {
                new ProblemInfo("Reverse Linked List",
                        Arrays.asList("ReverseLinkedList", "Reverse List"),
                        "Given the head of a singly linked list, reverse the list, and return the reversed list.",
                        "Reverse a singly linked list",
                        Arrays.asList("linked list", "reverse", "singly", "head", "nodes"),
                        "Linked List",
                        "linkedlist.ReverseLinkedList",
                        Arrays.asList("computeIterative", "computeRecursive", "computeWithStack"),
                        Arrays.asList("head"),
                        Arrays.asList("ListNode"),
                        null),

                new ProblemInfo("Merge Two Sorted Lists",
                        Arrays.asList("MergeTwoSortedLists", "Merge Lists"),
                        "You are given the heads of two sorted linked lists list1 and list2. Merge the two lists in a one sorted list. The list should be made by splicing together the nodes of the first two lists. Return the head of the merged linked list.",
                        "Merge two sorted linked lists",
                        Arrays.asList("linked list", "merge", "sorted", "heads", "nodes"),
                        "Linked List",
                        "linkedlist.MergeTwoSortedLists",
                        Arrays.asList("computeIterative", "computeRecursive"),
                        Arrays.asList("list1", "list2"),
                        Arrays.asList("ListNode", "ListNode"),
                        null),

                new ProblemInfo("Linked List Cycle",
                        Arrays.asList("LinkedListCycle", "Cycle Detection"),
                        "Given head, the head of a linked list, determine if the linked list has a cycle in it. There is a cycle in a linked list if there is some node in the list that can be reached again by continuously following the next pointer.",
                        "Detect if a linked list has a cycle",
                        Arrays.asList("linked list", "cycle", "head", "node", "pointer", "detect"),
                        "Linked List",
                        "linkedlist.LinkedListCycle",
                        Arrays.asList("computeWithHashSet", "computeWithTwoPointers"),
                        Arrays.asList("head"),
                        Arrays.asList("ListNode"),
                        null),

                new ProblemInfo("Add Two Numbers",
                        Arrays.asList("AddTwoNumbers", "Sum Lists"),
                        "You are given two non-empty linked lists representing two non-negative integers. The digits are stored in reverse order, and each of their nodes contains a single digit. Add the two numbers and return the sum as a linked list.",
                        "Add two numbers represented by linked lists",
                        Arrays.asList("linked list", "add", "numbers", "sum", "digits", "reverse order"),
                        "Linked List",
                        "linkedlist.AddTwoNumbers",
                        Arrays.asList("computeIterative", "computeRecursive"),
                        Arrays.asList("l1", "l2"),
                        Arrays.asList("ListNode", "ListNode"),
                        null),

                new ProblemInfo("Remove Nth Node From End of List",
                        Arrays.asList("RemoveNthNodeFromEnd", "Remove Nth Node", "Remove Node"),
                        "Given the head of a linked list, remove the nth node from the end of the list and return its head.",
                        "Remove the nth node from end of linked list",
                        Arrays.asList("linked list", "remove", "nth", "node", "end", "head"),
                        "Linked List",
                        "linkedlist.RemoveNthNodeFromEnd",
                        Arrays.asList("computeWithTwoPointers", "computeWithTwoPass", "computeRecursive"),
                        Arrays.asList("head", "n"),
                        Arrays.asList("ListNode", "int"),
                        null),

                new ProblemInfo("Palindrome Linked List",
                        Arrays.asList("PalindromeLinkedList", "Palindrome List", "Linked List Palindrome"),
                        "Given the head of a singly linked list, return true if it is a palindrome or false otherwise.",
                        "Check if linked list is palindrome",
                        Arrays.asList("linked list", "palindrome", "singly", "reverse", "compare"),
                        "Linked List",
                        "linkedlist.PalindromeLinkedList",
                        Arrays.asList("computeWithReversal", "computeWithStack", "computeRecursive"),
                        Arrays.asList("head"),
                        Arrays.asList("ListNode"),
                        null),

                new ProblemInfo("Linked List Cycle II",
                        Arrays.asList("LinkedListCycleII", "Cycle II", "Find Cycle Start"),
                        "Given the head of a linked list, return the node where the cycle begins. If there is no cycle, return null. There is a cycle in a linked list if there is some node in the list that can be reached again by continuously following the next pointer.",
                        "Find the node where cycle begins in linked list",
                        Arrays.asList("linked list", "cycle", "node", "begin", "start", "floyd"),
                        "Linked List",
                        "linkedlist.LinkedListCycleII",
                        Arrays.asList("computeWithTwoPointers", "computeWithHashSet"),
                        Arrays.asList("head"),
                        Arrays.asList("ListNode"),
                        null),

                new ProblemInfo("Reorder List",
                        Arrays.asList("ReorderList", "Reorder", "L0 Ln L1"),
                        "You are given the head of a singly linked-list. The list can be represented as: L0 → L1 → … → Ln - 1 → Ln. Reorder the list to be on the following form: L0 → Ln → L1 → Ln - 1 → L2 → Ln - 2 → …. You may not modify the values in the list's nodes. Only nodes themselves may be changed.",
                        "Reorder linked list: L0→Ln→L1→Ln-1...",
                        Arrays.asList("linked list", "reorder", "middle", "reverse", "merge"),
                        "Linked List",
                        "linkedlist.ReorderList",
                        Arrays.asList("compute", "computeWithStack"),
                        Arrays.asList("head"),
                        Arrays.asList("ListNode"),
                        null),

                new ProblemInfo("Merge k Sorted Lists",
                        Arrays.asList("MergeKSortedLists", "Merge K Lists", "K Sorted Lists"),
                        "You are given an array of k linked-lists lists, each linked-list is sorted in ascending order. Merge all the linked-lists into one sorted linked-list and return it.",
                        "Merge k sorted linked lists into one",
                        Arrays.asList("linked list", "merge", "k", "sorted", "heap", "priority queue",
                                "divide conquer"),
                        "Linked List",
                        "linkedlist.MergeKSortedLists",
                        Arrays.asList("computeWithHeap", "computeWithDivideConquer", "computeIterative"),
                        Arrays.asList("lists"),
                        Arrays.asList("ListNode[]"),
                        null),

                new ProblemInfo("Remove Duplicates from Sorted List",
                        Arrays.asList("RemoveDuplicatesFromSortedList", "Remove Duplicates List", "Unique List"),
                        "Given the head of a sorted linked list, delete all duplicates such that each element appears only once. Return the linked list sorted as well.",
                        "Remove duplicates from sorted linked list",
                        Arrays.asList("linked list", "remove", "duplicates", "sorted", "unique"),
                        "Linked List",
                        "linkedlist.RemoveDuplicatesFromSortedList",
                        Arrays.asList("computeIterative", "computeRecursive", "computeWithHashSet"),
                        Arrays.asList("head"),
                        Arrays.asList("ListNode"),
                        null)
        };
    }

    // Two Pointers - 9 problems
    private ProblemInfo[] createTwoPointersProblems() {
        return new ProblemInfo[] {
                new ProblemInfo("Valid Palindrome",
                        Arrays.asList("ValidPalindrome", "Palindrome"),
                        "A phrase is a palindrome if, after converting all uppercase letters into lowercase letters and removing all non-alphanumeric characters, it reads the same forward and backward.",
                        "Check if string is palindrome",
                        Arrays.asList("string", "palindrome", "alphanumeric", "forward", "backward"),
                        "Two Pointers",
                        "pointers.ValidPalindrome",
                        Arrays.asList("computeWithTwoPointers", "computeWithStringBuilder", "computeWithRecursion"),
                        Arrays.asList("s"),
                        Arrays.asList("String"),
                        new Object[] { "A man a plan a canal Panama" }),

                new ProblemInfo("Two Sum II",
                        Arrays.asList("TwoSumII", "Two Sum Two", "Sorted Two Sum"),
                        "Given a 1-indexed array of integers numbers that is already sorted in non-decreasing order, find two numbers such that they add up to a specific target number. Return the indices of the two numbers (1-indexed).",
                        "Find two numbers that add to target in sorted array",
                        Arrays.asList("array", "sorted", "target", "two", "sum", "indices", "increasing"),
                        "Two Pointers",
                        "pointers.TwoSumII",
                        Arrays.asList("computeWithTwoPointers", "computeWithBinarySearch", "computeWithHashMap"),
                        Arrays.asList("numbers", "target"),
                        Arrays.asList("int[]", "int"),
                        new Object[] { new int[] { 2, 7, 11, 15 }, 9 }),

                new ProblemInfo("Container With Most Water",
                        Arrays.asList("ContainerWithMostWater", "Most Water", "Container"),
                        "You are given an integer array height of length n. There are n vertical lines drawn such that the two endpoints of the ith line are (i, 0) and (i, height[i]). Find two lines that together with the x-axis form a container, such that the container contains the most water.",
                        "Find container that holds most water",
                        Arrays.asList("array", "height", "container", "water", "area", "two pointers"),
                        "Two Pointers",
                        "pointers.ContainerWithMostWater",
                        Arrays.asList("computeWithTwoPointers", "computeWithBruteForce", "computeWithRecursion"),
                        Arrays.asList("height"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { 1, 8, 6, 2, 5, 4, 8, 3, 7 } }),

                new ProblemInfo("3Sum",
                        Arrays.asList("ThreeSum", "3 Sum", "Three Sum"),
                        "Given an integer array nums, return all the triplets [nums[i], nums[j], nums[k]] such that i != j, i != k, and j != k, and nums[i] + nums[j] + nums[k] == 0. The solution set must not contain duplicate triplets.",
                        "Find all triplets that sum to zero",
                        Arrays.asList("array", "three", "sum", "triplets", "zero", "duplicate"),
                        "Two Pointers",
                        "pointers.ThreeSum",
                        Arrays.asList("computeWithTwoPointers", "computeWithHashSet", "computeWithBruteForce"),
                        Arrays.asList("nums"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { -1, 0, 1, 2, -1, -4 } }),

                new ProblemInfo("Trapping Rain Water",
                        Arrays.asList("TrappingRainWater", "Rain Water", "Trapping Water"),
                        "Given n non-negative integers representing an elevation map where the width of each bar is 1, compute how much water it can trap after raining.",
                        "Calculate trapped rainwater",
                        Arrays.asList("array", "height", "water", "trapping", "rain", "elevation"),
                        "Two Pointers",
                        "pointers.TrappingRainWater",
                        Arrays.asList("computeWithTwoPointers", "computeWithDP", "computeWithBruteForce"),
                        Arrays.asList("height"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { 0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1 } }),

                new ProblemInfo("Remove Duplicates from Sorted Array",
                        Arrays.asList("RemoveDuplicatesFromSortedArray", "Remove Duplicates", "Unique Elements"),
                        "Given an integer array nums sorted in non-decreasing order, remove the duplicates in-place such that each unique element appears only once. The relative order of the elements should be kept the same. Then return the number of unique elements in nums.",
                        "Remove duplicates from sorted array in-place",
                        Arrays.asList("array", "duplicates", "sorted", "unique", "in-place", "two pointers"),
                        "Two Pointers",
                        "pointers.RemoveDuplicatesFromSortedArray",
                        Arrays.asList("computeWithTwoPointers", "computeWithArrayList", "computeWithBruteForce"),
                        Arrays.asList("nums"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { 1, 1, 2 } }),

                new ProblemInfo("Move Zeroes",
                        Arrays.asList("MoveZeroes", "Move Zero", "Zeros to End"),
                        "Given an integer array nums, move all 0's to the end of it while maintaining the relative order of the non-zero elements. Note that you must do this in-place without making a copy of the array.",
                        "Move all zeros to end maintaining order",
                        Arrays.asList("array", "zeroes", "zeros", "move", "in-place", "two pointers"),
                        "Two Pointers",
                        "pointers.MoveZeroes",
                        Arrays.asList("computeWithTwoPointers", "computeWithSwapping", "computeWithArrayList"),
                        Arrays.asList("nums"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { 0, 1, 0, 3, 12 } }),

                new ProblemInfo("Sort Colors",
                        Arrays.asList("SortColors", "Dutch National Flag", "Three Colors"),
                        "Given an array nums with n objects colored red, white, or blue, sort them in-place so that objects of the same color are adjacent, with the colors in the order red, white, and blue. We will use the integers 0, 1, and 2 to represent the color red, white, and blue, respectively. You must solve this problem without using the library's sort function.",
                        "Sort array of 0s, 1s, and 2s (Dutch Flag)",
                        Arrays.asList("array", "sort", "colors", "three", "dutch flag", "0 1 2", "in-place"),
                        "Two Pointers",
                        "pointers.SortColors",
                        Arrays.asList("computeWithThreePointers", "computeWithCounting", "computeWithTwoPass"),
                        Arrays.asList("nums"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { 2, 0, 2, 1, 1, 0 } }),

                new ProblemInfo("4Sum",
                        Arrays.asList("FourSum", "4Sum", "Four Sum"),
                        "Given an array nums of n integers, return an array of all the unique quadruplets [nums[a], nums[b], nums[c], nums[d]] such that: 0 <= a, b, c, d < n, a, b, c, and d are distinct, nums[a] + nums[b] + nums[c] + nums[d] == target. You may return the answer in any order.",
                        "Find all unique quadruplets that sum to target",
                        Arrays.asList("array", "four", "sum", "quadruplets", "target", "two pointers", "unique"),
                        "Two Pointers",
                        "pointers.FourSum",
                        Arrays.asList("computeWithTwoPointers", "computeWithHashSet", "computeWithBruteForce"),
                        Arrays.asList("nums", "target"),
                        Arrays.asList("int[]", "int"),
                        new Object[] { new int[] { 1, 0, -1, 0, -2, 2 }, 0 })
        };
    }

    // Sliding Window - 7 problems
    private ProblemInfo[] createSlidingWindowProblems() {
        return new ProblemInfo[] {
                new ProblemInfo("Best Time to Buy and Sell Stock",
                        Arrays.asList("BestTimeToBuySellStock", "Buy Sell Stock", "Stock Price"),
                        "You are given an array prices where prices[i] is the price of a given stock on the ith day. You want to maximize your profit by choosing a single day to buy one stock and choosing a different day in the future to sell that stock. Return the maximum profit you can achieve from this transaction.",
                        "Find maximum profit from buying and selling stock",
                        Arrays.asList("array", "prices", "stock", "profit", "buy", "sell", "maximum"),
                        "Sliding Window",
                        "window.BestTimeToBuySellStock",
                        Arrays.asList("computeWithOnePass", "computeWithTwoPointers", "computeWithBruteForce"),
                        Arrays.asList("prices"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { 7, 1, 5, 3, 6, 4 } }),

                new ProblemInfo("Longest Substring Without Repeating",
                        Arrays.asList("LongestSubstringWithoutRepeating", "Longest Substring", "No Repeating"),
                        "Given a string s, find the length of the longest substring without repeating characters.",
                        "Find longest substring without repeating characters",
                        Arrays.asList("string", "substring", "longest", "repeating", "characters", "unique"),
                        "Sliding Window",
                        "window.LongestSubstringWithoutRepeating",
                        Arrays.asList("computeWithHashSet", "computeWithHashMap", "computeWithStringBuilder",
                                "computeWithArray"),
                        Arrays.asList("s"),
                        Arrays.asList("String"),
                        new Object[] { "abcabcbb" }),

                new ProblemInfo("Longest Repeating Character Replacement",
                        Arrays.asList("LongestRepeatingCharacterReplacement", "Character Replacement",
                                "Repeating Replacement"),
                        "You are given a string s and an integer k. You can choose any character of the string and change it to any other uppercase English letter. You can perform this operation at most k times. Return the length of the longest substring containing the same letter you can get after performing the above operations.",
                        "Find longest substring after replacing k characters",
                        Arrays.asList("string", "substring", "replace", "character", "longest", "k"),
                        "Sliding Window",
                        "window.LongestRepeatingCharacterReplacement",
                        Arrays.asList("computeWithHashMap", "computeWithArray", "computeWithStringBuilder"),
                        Arrays.asList("s", "k"),
                        Arrays.asList("String", "int"),
                        new Object[] { "AABABBA", 1 }),

                new ProblemInfo("Minimum Window Substring",
                        Arrays.asList("MinimumWindowSubstring", "Min Window", "Window Substring"),
                        "Given two strings s and t, return the minimum window substring of s such that every character in t (including duplicates) is included in the window. If there is no such substring, return the empty string \"\".",
                        "Find minimum window substring containing all characters of t",
                        Arrays.asList("string", "substring", "window", "minimum", "sliding window", "contains"),
                        "Sliding Window",
                        "window.MinimumWindowSubstring",
                        Arrays.asList("computeWithHashMap", "computeWithArray"),
                        Arrays.asList("s", "t"),
                        Arrays.asList("String", "String"),
                        new Object[] { "ADOBECODEBANC", "ABC" }),

                new ProblemInfo("Minimum Size Subarray Sum",
                        Arrays.asList("MinimumSizeSubarraySum", "Min Size Subarray", "Subarray Sum"),
                        "Given an array of positive integers nums and a positive integer target, return the minimal length of a contiguous subarray [numsl, numsl+1, ..., numsr-1, numsr] of which the sum is greater than or equal to target. If there is no such subarray, return 0 instead.",
                        "Find minimum length subarray with sum >= target",
                        Arrays.asList("array", "subarray", "sum", "target", "minimum", "length", "sliding window"),
                        "Sliding Window",
                        "window.MinimumSizeSubarraySum",
                        Arrays.asList("computeWithSlidingWindow", "computeWithBinarySearch", "computeWithBruteForce"),
                        Arrays.asList("target", "nums"),
                        Arrays.asList("int", "int[]"),
                        new Object[] { 7, new int[] { 2, 3, 1, 2, 4, 3 } }),

                new ProblemInfo("Permutation in String",
                        Arrays.asList("PermutationInString", "Permutation String", "s2 Contains s1"),
                        "Given two strings s1 and s2, return true if s2 contains a permutation of s1, or false otherwise. In other words, return true if one of s1's permutations is the substring of s2.",
                        "Check if s2 contains permutation of s1",
                        Arrays.asList("string", "permutation", "substring", "contains", "sliding window", "anagram"),
                        "Sliding Window",
                        "window.PermutationInString",
                        Arrays.asList("computeWithArray", "computeWithHashMap", "computeWithBruteForce"),
                        Arrays.asList("s1", "s2"),
                        Arrays.asList("String", "String"),
                        new Object[] { "ab", "eidbaooo" }),

                new ProblemInfo("Substring with Concatenation of All Words",
                        Arrays.asList("SubstringWithConcatenationOfAllWords", "Concatenation", "All Words"),
                        "You are given a string s and an array of strings words. All the strings of words are of the same length. A concatenated substring in s is a substring that contains all the strings of words concatenated in any order. Return the starting indices of all such concatenated substrings in s.",
                        "Find indices of substrings containing all words",
                        Arrays.asList("string", "substring", "concatenation", "words", "indices", "sliding window",
                                "hashmap"),
                        "Sliding Window",
                        "window.SubstringWithConcatenationOfAllWords",
                        Arrays.asList("computeWithHashMap", "computeWithSlidingWindow"),
                        Arrays.asList("s", "words"),
                        Arrays.asList("String", "String[]"),
                        new Object[] { "barfoothefoobarman", new String[] { "foo", "bar" } })
        };
    }

    // Stack - 6 problems
    private ProblemInfo[] createStackProblems() {
        return new ProblemInfo[] {
                new ProblemInfo("Valid Parentheses",
                        Arrays.asList("ValidParentheses", "Parentheses", "Valid Brackets"),
                        "Given a string s containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid. An input string is valid if: 1) Open brackets must be closed by the same type of brackets. 2) Open brackets must be closed in the correct order. 3) Every close bracket has a corresponding open bracket of the same type.",
                        "Check if parentheses/brackets are valid",
                        Arrays.asList("string", "parentheses", "brackets", "valid", "stack", "matching"),
                        "Stack",
                        "stack.ValidParentheses",
                        Arrays.asList("computeWithStack", "computeWithHashMap", "computeWithStringBuilder"),
                        Arrays.asList("s"),
                        Arrays.asList("String"),
                        new Object[] { "()[]{}" }),

                new ProblemInfo("Daily Temperatures",
                        Arrays.asList("DailyTemperatures", "Temperatures", "Next Warmer"),
                        "Given an array of integers temperatures represents the daily temperatures, return an array answer such that answer[i] is the number of days you have to wait after the ith day to get a warmer temperature. If there is no future day for which this is possible, keep answer[i] == 0 instead.",
                        "Find days to wait for warmer temperature",
                        Arrays.asList("array", "temperatures", "warmer", "days", "wait", "next"),
                        "Stack",
                        "stack.DailyTemperatures",
                        Arrays.asList("computeWithStack", "computeWithArray", "computeWithBruteForce"),
                        Arrays.asList("temperatures"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { 73, 74, 75, 71, 69, 72, 76, 73 } }),

                new ProblemInfo("Largest Rectangle in Histogram",
                        Arrays.asList("LargestRectangleInHistogram", "Largest Rectangle", "Histogram"),
                        "Given an array of integers heights representing the histogram's bar height where the width of each bar is 1, return the area of the largest rectangle in the histogram.",
                        "Find largest rectangle area in histogram",
                        Arrays.asList("array", "heights", "histogram", "rectangle", "largest", "area",
                                "monotonic stack"),
                        "Stack",
                        "stack.LargestRectangleInHistogram",
                        Arrays.asList("computeWithStack", "computeWithBruteForce", "computeWithDivideConquer"),
                        Arrays.asList("heights"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { 2, 1, 5, 6, 2, 3 } }),

                new ProblemInfo("Evaluate Reverse Polish Notation",
                        Arrays.asList("EvaluateReversePolishNotation", "RPN", "Polish Notation"),
                        "You are given an array of strings tokens that represents an arithmetic expression in a Reverse Polish Notation. Evaluate the expression and return an integer that represents the value of the expression. Note that: The valid operators are '+', '-', '*', and '/'. Each operand may be an integer or another expression. The division between two integers always truncates toward zero. There will not be any division by zero.",
                        "Evaluate RPN expression",
                        Arrays.asList("array", "tokens", "rpn", "reverse polish notation", "expression", "stack",
                                "evaluate"),
                        "Stack",
                        "stack.EvaluateReversePolishNotation",
                        Arrays.asList("computeWithStack", "computeWithArray", "computeRecursive"),
                        Arrays.asList("tokens"),
                        Arrays.asList("String[]"),
                        new Object[] { new String[] { "2", "1", "+", "3", "*" } }),

                new ProblemInfo("Generate Parentheses",
                        Arrays.asList("GenerateParentheses", "Generate Brackets", "Valid Parentheses Combinations"),
                        "Given n pairs of parentheses, write a function to generate all combinations of well-formed parentheses.",
                        "Generate all valid parentheses combinations",
                        Arrays.asList("parentheses", "brackets", "generate", "combinations", "well-formed",
                                "backtracking", "recursive"),
                        "Stack",
                        "stack.GenerateParentheses",
                        Arrays.asList("computeRecursive", "computeWithRecursion", "computeWithStack"),
                        Arrays.asList("n"),
                        Arrays.asList("int"),
                        new Object[] { 3 }),

                new ProblemInfo("Car Fleet",
                        Arrays.asList("CarFleet", "Fleet", "Cars"),
                        "There are n cars going to the same destination on a one-lane road. The destination is target miles away. You are given two integer array position and speed, both of length n, where position[i] is the position of the ith car and speed[i] is the speed of the ith car (in miles per hour). A car can never pass another car ahead of it, but it can catch up to it and drive bumper to bumper at the same speed. Return the number of car fleets that will arrive at the destination.",
                        "Count number of car fleets reaching destination",
                        Arrays.asList("cars", "fleet", "position", "speed", "target", "stack", "monotonic"),
                        "Stack",
                        "stack.CarFleet",
                        Arrays.asList("computeWithStack", "computeWithArray", "computeWithTreeMap"),
                        Arrays.asList("target", "position", "speed"),
                        Arrays.asList("int", "int[]", "int[]"),
                        new Object[] { 12, new int[] { 10, 8, 0, 5, 3 }, new int[] { 2, 4, 1, 1, 3 } })
        };
    }

    // Search - 7 problems
    private ProblemInfo[] createSearchProblems() {
        return new ProblemInfo[] {
                new ProblemInfo("Search in Rotated Sorted Array",
                        Arrays.asList("SearchInRotatedSortedArray", "Rotated Array", "Search Rotated"),
                        "There is an integer array nums sorted in ascending order (with distinct values). Prior to being passed to your function, nums is possibly rotated at an unknown pivot index k such that the resulting array is [nums[k], nums[k+1], ..., nums[n-1], nums[0], nums[1], ..., nums[k-1]]. Given the array nums after the rotation and an integer target, return the index of target if it is in nums, or -1 if it is not in nums.",
                        "Search target in rotated sorted array",
                        Arrays.asList("array", "rotated", "sorted", "search", "target", "pivot", "binary search"),
                        "Search",
                        "search.SearchInRotatedSortedArray",
                        Arrays.asList("computeWithBinarySearch", "computeWithPivotSearch", "computeWithLinearSearch"),
                        Arrays.asList("nums", "target"),
                        Arrays.asList("int[]", "int"),
                        new Object[] { new int[] { 4, 5, 6, 7, 0, 1, 2 }, 0 }),

                new ProblemInfo("Find Minimum in Rotated Sorted Array",
                        Arrays.asList("FindMinimumInRotatedSortedArray", "Min Rotated Array", "Minimum Rotated"),
                        "Suppose an array of length n sorted in ascending order is rotated between 1 and n times. For example, the array nums = [0,1,2,4,5,6,7] might become [4,5,6,7,0,1,2] if it was rotated 4 times. Given the sorted rotated array nums of unique elements, return the minimum element of this array.",
                        "Find minimum element in rotated sorted array",
                        Arrays.asList("array", "rotated", "sorted", "minimum", "element", "binary search"),
                        "Search",
                        "search.FindMinimumInRotatedSortedArray",
                        Arrays.asList("computeWithBinarySearch", "computeWithLinearSearch", "computeWithRecursion"),
                        Arrays.asList("nums"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { 3, 4, 5, 1, 2 } }),

                new ProblemInfo("Find Peak Element",
                        Arrays.asList("FindPeakElement", "Peak Element", "Peak"),
                        "A peak element is an element that is strictly greater than its neighbors. Given a 0-indexed integer array nums, find a peak element, and return its index. If the array contains multiple peaks, return the index to any of the peaks. You may imagine that nums[-1] = nums[n] = -∞. You must write an algorithm that runs in O(log n) time.",
                        "Find peak element in array",
                        Arrays.asList("array", "peak", "element", "neighbors", "binary search", "log n"),
                        "Search",
                        "search.FindPeakElement",
                        Arrays.asList("computeWithBinarySearch", "computeWithLinearSearch", "computeRecursive"),
                        Arrays.asList("nums"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { 1, 2, 3, 1 } }),

                new ProblemInfo("Search a 2D Matrix",
                        Arrays.asList("SearchA2DMatrix", "2D Matrix Search", "Matrix Search"),
                        "You are given an m x n integer matrix matrix with the following properties: Each row is sorted in non-decreasing order. The first integer of each row is greater than the last integer of the previous row. Given an integer target, return true if target is in matrix or false otherwise.",
                        "Search target in sorted 2D matrix",
                        Arrays.asList("matrix", "2d", "sorted", "target", "binary search", "rows"),
                        "Search",
                        "search.SearchA2DMatrix",
                        Arrays.asList("computeWithBinarySearch", "computeWithTwoBinarySearches",
                                "computeWithLinearSearch"),
                        Arrays.asList("matrix", "target"),
                        Arrays.asList("int[][]", "int"),
                        new Object[] { new int[][] { { 1, 3, 5, 7 }, { 10, 11, 16, 20 }, { 23, 30, 34, 60 } }, 3 }),

                new ProblemInfo("Binary Search",
                        Arrays.asList("BinarySearch", "Search", "Binary"),
                        "Given an array of integers nums which is sorted in ascending order, and an integer target, write a function to search target in nums. If target exists, then return its index. Otherwise, return -1. You must write an algorithm with O(log n) runtime complexity.",
                        "Search target in sorted array using binary search",
                        Arrays.asList("array", "sorted", "target", "binary search", "log n", "search"),
                        "Search",
                        "search.BinarySearch",
                        Arrays.asList("computeIterative", "computeRecursive", "computeWithArrays"),
                        Arrays.asList("nums", "target"),
                        Arrays.asList("int[]", "int"),
                        new Object[] { new int[] { -1, 0, 3, 5, 9, 12 }, 9 }),

                new ProblemInfo("First Bad Version",
                        Arrays.asList("FirstBadVersion", "Bad Version", "Version"),
                        "You are a product manager and currently leading a team to develop a new product. Unfortunately, the latest version of your product fails the quality check. Since each version is developed based on the previous version, all the versions after a bad version are also bad. Suppose you have n versions [1, 2, ..., n] and you want to find out the first bad one, which causes all the following ones to be bad. You are given an API bool isBadVersion(version) which returns whether version is bad. Implement a function to find the first bad version. You should minimize the number of calls to the API.",
                        "Find first bad version using binary search",
                        Arrays.asList("version", "bad", "binary search", "log n", "api", "search"),
                        "Search",
                        "search.FirstBadVersion",
                        Arrays.asList("computeWithBinarySearch", "computeRecursive", "computeWithLinearSearch"),
                        Arrays.asList("n"),
                        Arrays.asList("int"),
                        new Object[] { 5 }),

                new ProblemInfo("Sqrt(x)",
                        Arrays.asList("Sqrt", "Square Root", "Root"),
                        "Given a non-negative integer x, return the square root of x rounded down to the nearest integer. The returned integer should be non-negative as well. You must not use any built-in exponent function or operator.",
                        "Find square root of x (rounded down)",
                        Arrays.asList("math", "sqrt", "square root", "binary search", "newton"),
                        "Search",
                        "search.Sqrt",
                        Arrays.asList("computeWithBinarySearch", "computeWithNewton", "computeWithMath",
                                "computeWithLinearSearch"),
                        Arrays.asList("x"),
                        Arrays.asList("int"),
                        new Object[] { 8 })
        };
    }

    // Dynamic Programming - 6 problems
    private ProblemInfo[] createDPProblems() {
        return new ProblemInfo[] {
                new ProblemInfo("Climbing Stairs",
                        Arrays.asList("ClimbingStairs", "Stairs", "Fibonacci"),
                        "You are climbing a staircase. It takes n steps to reach the top. Each time you can either climb 1 or 2 steps. In how many distinct ways can you climb to the top?",
                        "Find distinct ways to climb n steps (1 or 2 at a time)",
                        Arrays.asList("stairs", "steps", "distinct", "ways", "climbing", "fibonacci", "dp"),
                        "Dynamic Programming",
                        "dp.ClimbingStairs",
                        Arrays.asList("computeWithDP", "computeWithOptimizedSpace", "computeWithRecursion",
                                "computeWithPureRecursion"),
                        Arrays.asList("n"),
                        Arrays.asList("int"),
                        new Object[] { 5 }),

                new ProblemInfo("House Robber",
                        Arrays.asList("HouseRobber", "Robber", "Max Sum No Adjacent"),
                        "You are a professional robber planning to rob houses along a street. Each house has a certain amount of money stashed, the only constraint stopping you from robbing each of them is that adjacent houses have security systems connected and it will automatically contact the police if two adjacent houses were broken into on the same night. Given an integer array nums representing the amount of money of each house, return the maximum amount of money you can rob tonight without alerting the police.",
                        "Find maximum money robbing houses (no two adjacent)",
                        Arrays.asList("houses", "robber", "money", "maximum", "adjacent", "dp", "constraint"),
                        "Dynamic Programming",
                        "dp.HouseRobber",
                        Arrays.asList("computeWithDP", "computeWithOptimizedSpace", "computeWithRecursion",
                                "computeWithPureRecursion"),
                        Arrays.asList("nums"),
                        Arrays.asList("int[]"),
                        new Object[] { new int[] { 2, 7, 9, 3, 1 } }),

                new ProblemInfo("Coin Change",
                        Arrays.asList("CoinChange", "Min Coins", "Change"),
                        "You are given an integer array coins representing coins of different denominations and an integer amount representing a total amount of money. Return the fewest number of coins that you need to make up that amount. If that amount of money cannot be made up by any combination of the coins, return -1. You may assume that you have an infinite number of each kind of coin.",
                        "Find minimum coins to make amount",
                        Arrays.asList("coins", "amount", "minimum", "fewest", "change", "dp", "unbounded"),
                        "Dynamic Programming",
                        "dp.CoinChange",
                        Arrays.asList("computeWithDP", "computeWithRecursion", "computeWithPureRecursion"),
                        Arrays.asList("coins", "amount"),
                        Arrays.asList("int[]", "int"),
                        new Object[] { new int[] { 1, 2, 5 }, 11 }),

                new ProblemInfo("Unique Paths",
                        Arrays.asList("UniquePaths", "Robot Paths", "Grid Paths"),
                        "There is a robot on an m x n grid. The robot is initially located at the top-left corner (grid[0][0]). The robot tries to move to the bottom-right corner (grid[m - 1][n - 1]). The robot can only move either down or right at any point in time. Given the two integers m and n, return the number of possible unique paths that the robot can take to reach the bottom-right corner.",
                        "Count unique paths from top-left to bottom-right",
                        Arrays.asList("grid", "robot", "paths", "unique", "m", "n", "dp", "2d"),
                        "Dynamic Programming",
                        "dp.UniquePaths",
                        Arrays.asList("computeWithDP", "computeWithOptimizedSpace", "computeWithRecursion"),
                        Arrays.asList("m", "n"),
                        Arrays.asList("int", "int"),
                        new Object[] { 3, 7 }),

                new ProblemInfo("Word Break",
                        Arrays.asList("WordBreak", "Break", "Segment"),
                        "Given a string s and a dictionary of strings wordDict, return true if s can be segmented into a space-separated sequence of one or more dictionary words. Note that the same word in the dictionary may be reused multiple times in the segmentation.",
                        "Check if string can be segmented into dictionary words",
                        Arrays.asList("string", "word", "break", "segment", "dictionary", "dp", "recursion"),
                        "Dynamic Programming",
                        "dp.WordBreak",
                        Arrays.asList("computeWithDP", "computeWithRecursion", "computeWithBFS"),
                        Arrays.asList("s", "wordDict"),
                        Arrays.asList("String", "List<String>"),
                        new Object[] { "leetcode", Arrays.asList("leet", "code") }),

                new ProblemInfo("Longest Increasing Subsequence",
                        Arrays.asList("LongestIncreasingSubsequence", "LIS", "Increasing Subsequence"),
                        "Given an integer array nums, return the length of the longest strictly increasing subsequence.",
                        "Find length of longest increasing subsequence",
                        Arrays.asList("array", "subsequence", "increasing", "longest", "dp", "binary search"),
                        "Dynamic Programming",
                        "dp.LongestIncreasingSubsequence",
                        Arrays.asList("computeWithDP", "computeWithBinarySearch", "computeRecursive"),
                        List.of("nums"),
                        List.of("int[]"),
                        new Object[] { new int[] { 10, 9, 2, 5, 3, 7, 101, 18 } })
        };
    }

    // Graphs - 1 problem
    private ProblemInfo[] createGraphProblems() {
        return new ProblemInfo[] {
                new ProblemInfo("Number of Islands",
                        Arrays.asList("NumberOfIslands", "Islands", "Grid Islands"),
                        "Given an m x n 2D binary grid grid which represents a map of '1's (land) and '0's (water), return the number of islands.",
                        "Count number of islands in 2D grid",
                        Arrays.asList("grid", "2d", "islands", "land", "water", "dfs", "bfs", "graph"),
                        "Graphs",
                        "graph.NumberOfIslands",
                        Arrays.asList("computeWithDFS", "computeWithBFS", "computeWithUnionFind"),
                        List.of("grid"),
                        List.of("char[][]"),
                        new Object[] { new char[][] { { '1', '1', '1', '1', '0' }, { '1', '1', '0', '1', '0' },
                                { '1', '1', '0', '0', '0' }, { '0', '0', '0', '0', '0' } } })
        };
    }

    // Heap/Priority Queue - 1 problem
    private ProblemInfo[] createHeapProblems() {
        return new ProblemInfo[] {
                new ProblemInfo("Kth Largest Element in Array",
                        Arrays.asList("KthLargestElementInArray", "Kth Largest", "K Largest"),
                        "Given an integer array nums and an integer k, return the kth largest element in the array.",
                        "Find kth largest element in array",
                        Arrays.asList("array", "kth", "largest", "element", "heap", "priority queue", "quickselect"),
                        "Heap/Priority Queue",
                        "heap.KthLargestElementInArray",
                        Arrays.asList("computeWithHeap", "computeWithMaxHeap", "computeWithQuickSelect",
                                "computeWithSorting"),
                        Arrays.asList("nums", "k"),
                        Arrays.asList("int[]", "int"),
                        new Object[] { new int[] { 3, 2, 1, 5, 6, 4 }, 2 })
        };
    }

    // Backtracking - 1 problem
    private ProblemInfo[] createBacktrackProblems() {
        return new ProblemInfo[] {
                new ProblemInfo("Subsets",
                        Arrays.asList("Subsets", "Power Set", "All Subsets"),
                        "Given an integer array nums of unique elements, return all possible subsets (the power set).",
                        "Find all subsets of array",
                        Arrays.asList("array", "subsets", "power set", "combinations", "backtracking", "recursive"),
                        "Backtracking",
                        "backtrack.Subsets",
                        Arrays.asList("computeRecursive", "computeIterative", "computeIncremental"),
                        List.of("nums"),
                        List.of("int[]"),
                        new Object[] { new int[] { 1, 2, 3 } })
        };
    }

    // Trie - 1 problem
    private ProblemInfo[] createTrieProblems() {
        return new ProblemInfo[] {
                new ProblemInfo("Implement Trie",
                        Arrays.asList("ImplementTrie", "Trie", "Prefix Tree"),
                        "A trie (pronounced as 'try') or prefix tree is a tree data structure used to efficiently store and retrieve keys in a dataset of strings.",
                        "Implement Trie (Prefix Tree) data structure",
                        Arrays.asList("trie", "prefix tree", "data structure", "insert", "search", "startsWith",
                                "autocomplete"),
                        "Trie",
                        "trie.ImplementTrie",
                        Arrays.asList("insert", "search", "startsWith"),
                        Arrays.asList("trie", "word"),
                        Arrays.asList("ImplementTrie", "String"),
                        null)
        };
    }

    // Intervals - 1 problem
    private ProblemInfo[] createIntervalsProblems() {
        return new ProblemInfo[] {
                new ProblemInfo("Merge Intervals",
                        Arrays.asList("MergeIntervals", "Merge", "Overlapping Intervals"),
                        "Given an array of intervals where intervals[i] = [starti, endi], merge all overlapping intervals.",
                        "Merge overlapping intervals",
                        Arrays.asList("intervals", "merge", "overlapping", "array", "sort", "ranges"),
                        "Intervals",
                        "intervals.MergeIntervals",
                        Arrays.asList("computeWithSorting", "computeWithArrayList", "computeRecursive"),
                        List.of("intervals"),
                        List.of("int[][]"),
                        new Object[] { new int[][] { { 1, 3 }, { 2, 6 }, { 8, 10 }, { 15, 18 } } })
        };
    }

    // Math/Geometry - 1 problem
    private ProblemInfo[] createMathProblems() {
        return new ProblemInfo[] {
                new ProblemInfo("Pow(x, n)",
                        Arrays.asList("Pow", "Power", "Exponentiation"),
                        "Implement pow(x, n), which calculates x raised to the power n (i.e., x^n).",
                        "Calculate x raised to power n",
                        Arrays.asList("math", "power", "exponent", "x^n", "fast power", "binary exponentiation"),
                        "Math/Geometry",
                        "math.Pow",
                        Arrays.asList("computeRecursive", "computeIterative", "computeBruteForce"),
                        Arrays.asList("x", "n"),
                        Arrays.asList("double", "int"),
                        new Object[] { 2.0, 10 })
        };
    }

    // Greedy - 1 problem
    private ProblemInfo[] createGreedyProblems() {
        return new ProblemInfo[] {
                new ProblemInfo("Jump Game",
                        Arrays.asList("JumpGame", "Jump", "Can Jump"),
                        "You are given an integer array nums. You are initially positioned at the array's first index, and each element in the array represents your maximum jump length at that position.",
                        "Check if can reach last index by jumping",
                        Arrays.asList("array", "jump", "game", "reach", "last index", "greedy", "dp"),
                        "Greedy",
                        "greedy.JumpGame",
                        Arrays.asList("computeWithGreedy", "computeWithDP", "computeRecursive"),
                        List.of("nums"),
                        List.of("int[]"),
                        new Object[] { new int[] { 2, 3, 1, 1, 4 } })
        };
    }

    // Bit Manipulation - 1 problem
    private ProblemInfo[] createBitManipProblems() {
        return new ProblemInfo[] {
                new ProblemInfo("Number of 1 Bits",
                        Arrays.asList("NumberOf1Bits", "Hamming Weight", "Set Bits"),
                        "Write a function that takes the binary representation of an unsigned integer and returns the number of '1' bits it has.",
                        "Count number of 1 bits in integer",
                        Arrays.asList("bit", "bits", "hamming weight", "count", "ones", "set bits", "bit manipulation"),
                        "Bit Manipulation",
                        "bitmanip.NumberOf1Bits",
                        Arrays.asList("computeWithBuiltIn", "computeWithLoop", "computeWithKernighan",
                                "computeWithShift", "computeRecursive"),
                        List.of("n"),
                        List.of("int"),
                        new Object[] { 11 })
        };
    }

}