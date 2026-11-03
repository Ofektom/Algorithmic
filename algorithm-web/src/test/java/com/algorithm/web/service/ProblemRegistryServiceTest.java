package com.algorithm.web.service;

import com.algorithm.web.model.ProblemInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProblemRegistryServiceTest {

    private ProblemRegistryService registry;
    private AlgorithmExecutionService executionService;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        registry = new ProblemRegistryService();
        registry.initialize();
        executionService = new AlgorithmExecutionService();
    }

    private static final Map<String, String> NEW_PROBLEM_CATEGORIES = Map.of(
            "Diagonal Difference", "Arrays & Hashing",
            "Plus Minus", "Arrays & Hashing",
            "Mini-Max Sum", "Arrays & Hashing",
            "Birthday Cake Candles", "Arrays & Hashing",
            "Sales by Match", "Arrays & Hashing",
            "Time Conversion", "Strings",
            "Counting Valleys", "Strings",
            "Repeated String", "Strings",
            "Caesar Cipher", "Strings",
            "Jumping on the Clouds", "Greedy");

    @Test
    void problemsAreGroupedByAlgorithmTopic() {
        NEW_PROBLEM_CATEGORIES.forEach((name, category) -> {
            assertTrue(registry.getCategories().contains(category), category);
            assertTrue(registry.getProblemsForCategory(category).stream().anyMatch(p -> p.getName().equals(name)),
                    name + " should be listed under " + category);
        });
    }

    @Test
    void problemsAreSearchableByAlias() {
        assertEquals("Sales by Match", registry.searchProblems("sock merchant").get(0).getName());
        assertEquals("Time Conversion", registry.searchProblems("military time").get(0).getName());
    }

    @Test
    void spokenCommandsResolveToProblems() {
        assertMatch("Two Sum", "solve two sum");
        assertMatch("Two Sum", "Two Sum");
        assertMatch("Merge Intervals", "show me merge intervals");
        assertMatch("Sales by Match", "please solve sock merchant");
        assertMatch("Climbing Stairs", "open the problem climbing stairs");
        // Filler words inside the name don't matter
        assertMatch("Maximum Depth of Binary Tree", "find the maximum depth of a binary tree");
    }

    @Test
    void shortVariantsOfBuiltInProblemsFallBackToAi() {
        // Each contains a built-in problem's name or most of its words, but asks something different
        assertNoMatch("reverse a linked list from position left to position right");
        assertNoMatch("count how many palindromic substrings a string has");
        assertNoMatch("given a string s find the longest palindromic subsequence length");
        assertNoMatch("find the kth smallest element in an array");
        assertMatch("Longest Palindromic Substring", "find the longest palindromic substring in a string");
    }

    @Test
    void spokenCategoriesResolveToCategories() {
        assertEquals("Strings", registry.resolve("go to strings").category());
        assertEquals("Arrays & Hashing", registry.resolve("arrays and hashing").category());
        assertEquals("Heap/Priority Queue", registry.resolve("heap priority queue").category());
    }

    @Test
    void readOutQuestionsResolveToProblems() {
        // Statements as a user would read them aloud: no punctuation, examples included, own wording
        assertMatch("Two Sum", "Given an array of integers nums and an integer target return indices of the two "
                + "numbers such that they add up to target you may assume that each input would have exactly one "
                + "solution and you may not use the same element twice example 1 input nums equals 2 7 11 15 "
                + "target equals 9 output 0 1");
        assertMatch("Diagonal Difference", "Given a square matrix calculate the absolute difference between the "
                + "sums of its diagonals for example the square matrix arr is shown below 1 2 3 4 5 6 9 8 9 the "
                + "left to right diagonal equals 1 plus 5 plus 9 equals 15 the right to left diagonal equals 3 plus "
                + "5 plus 9 equals 17 their absolute difference is 2");
        assertMatch("Sales by Match", "There is a large pile of socks that must be paired by color given an array "
                + "of integers representing the color of each sock determine how many pairs of socks with matching "
                + "colors there are");
        assertMatch("Counting Valleys", "An avid hiker keeps meticulous records of their hikes hikes always start "
                + "and end at sea level and each step up or down represents a 1 unit change in altitude a valley is "
                + "a sequence of consecutive steps below sea level starting with a step down from sea level and "
                + "ending with a step up to sea level given the sequence of up and down steps during a hike find "
                + "and print the number of valleys walked through");
        assertMatch("Climbing Stairs", "You are climbing a staircase it takes n steps to reach the top each time "
                + "you can either climb 1 or 2 steps in how many distinct ways can you climb to the top");
        assertMatch("Valid Parentheses", "Given a string s containing just the characters parentheses brackets and "
                + "braces determine if the input string is valid an input string is valid if open brackets must be "
                + "closed by the same type of brackets and open brackets must be closed in the correct order");
        assertMatch("Number of Islands", "Given an m by n 2D binary grid which represents a map of ones land and "
                + "zeros water return the number of islands an island is surrounded by water and is formed by "
                + "connecting adjacent lands horizontally or vertically");
    }

    @Test
    void unknownQuestionsFallBackToAi() {
        assertNoMatch("The n queens puzzle is the problem of placing n queens on an n by n chessboard such that "
                + "no two queens attack each other given an integer n return all distinct solutions");
        assertNoMatch("Given two sorted arrays nums1 and nums2 of size m and n respectively return the median of "
                + "the two sorted arrays the overall run time complexity should be o log m plus n");
        assertNoMatch("Find the shortest path from a source node to every other node in a weighted directed "
                + "graph with non negative edge weights");
        assertNoMatch("Design a data structure that follows the constraints of a least recently used cache "
                + "implement the get and put operations in constant time");
        assertNoMatch("");
    }

    @Test
    void nearMissesSuggestTheSimilarProblemInsteadOfMatching() {
        // Different problems that share most of their wording with a built-in one
        var palindromes = registry.resolve("Given a string s return the number of palindromic substrings in it "
                + "a string is a palindrome when it reads the same backward as forward a substring is a contiguous "
                + "sequence of characters within the string");
        assertNull(palindromes.match());
        assertEquals("Longest Palindromic Substring", palindromes.suggestion().getName());

        var coinChangeTwo = registry.resolve("You are given an integer array coins representing coins of "
                + "different denominations and an integer amount representing a total amount of money return the "
                + "number of combinations that make up that amount");
        assertNull(coinChangeTwo.match());
        assertEquals("Coin Change", coinChangeTwo.suggestion().getName());
    }

    @Test
    void phraseInsideSentenceNeedsMatchingDescription() {
        // "the player can jump" contains Jump Game's alias "Can Jump" but describes Jumping on the Clouds
        var clouds = registry.resolve("There is a new mobile game that starts with consecutively numbered clouds "
                + "some of the clouds are thunderheads and others are cumulus the player can jump on any cumulus "
                + "cloud having a number that is equal to the number of the current cloud plus 1 or 2 determine "
                + "the minimum number of jumps it will take to jump from the starting position to the last cloud");
        assertNotEquals("Jump Game", clouds.match() == null ? null : clouds.match().getName());
    }

    private void assertMatch(String expected, String text) {
        var resolution = registry.resolve(text);
        assertNotNull(resolution.match(), "no match for: " + text);
        assertEquals(expected, resolution.match().getName());
    }

    private void assertNoMatch(String text) {
        assertNull(registry.resolve(text).match(), "unexpected match for: " + text);
    }

    @Test
    void everyMethodRunsWithItsExampleInput() throws Exception {
        for (String name : NEW_PROBLEM_CATEGORIES.keySet()) {
            ProblemInfo p = registry.findProblem(name);
            // Round-trip through JSON the way the browser submits inputs
            Object[] inputs = mapper.readValue(mapper.writeValueAsString(p.getExampleInputs()), List.class).toArray();
            for (String method : p.getMethods()) {
                Map<String, Object> result = executionService.execute(p.getClassName(), method, inputs);
                assertEquals(true, result.get("success"), p.getName() + "." + method + ": " + result.get("error"));
            }
        }
    }

    @Test
    void longParametersAndResultsAreHandled() {
        Map<String, Object> repeated = executionService.execute("strings.RepeatedString", "computeWithMath",
                new Object[] { "aba", 10 });
        assertEquals("7", repeated.get("result"));

        Map<String, Object> miniMax = executionService.execute("arrays.MiniMaxSum", "computeWithSinglePass",
                new Object[] { List.of(1, 2, 3, 4, 5) });
        assertEquals("[10, 14]", miniMax.get("result"));
    }
}
