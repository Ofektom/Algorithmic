package graph;

/**
 * Number of Islands - LeetCode 200
 * Given an m x n 2D binary grid grid which represents a map of '1's (land) and '0's (water),
 * return the number of islands. An island is surrounded by water and is formed by connecting
 * adjacent lands horizontally or vertically. You may assume all four edges of the grid are
 * all surrounded by water.
 */
public class NumberOfIslands {

    /**
     * Solution 1: DFS (Depth-First Search)
     * Time Complexity: O(m * n)
     * Space Complexity: O(m * n)
     */
    public static int computeWithDFS(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;

        int m = grid.length;
        int n = grid[0].length;
        int count = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    count++;
                    dfs(grid, i, j, m, n);
                }
            }
        }

        return count;
    }

    private static void dfs(char[][] grid, int i, int j, int m, int n) {
        if (i < 0 || i >= m || j < 0 || j >= n || grid[i][j] == '0') {
            return;
        }

        grid[i][j] = '0'; // Mark as visited

        // Explore all 4 directions
        dfs(grid, i + 1, j, m, n);
        dfs(grid, i - 1, j, m, n);
        dfs(grid, i, j + 1, m, n);
        dfs(grid, i, j - 1, m, n);
    }

    /**
     * Solution 2: BFS (Breadth-First Search)
     * Time Complexity: O(m * n)
     * Space Complexity: O(min(m, n))
     */
    public static int computeWithBFS(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;

        int m = grid.length;
        int n = grid[0].length;
        int count = 0;
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    count++;
                    java.util.Queue<int[]> queue = new java.util.LinkedList<>();
                    queue.offer(new int[]{i, j});
                    grid[i][j] = '0';

                    while (!queue.isEmpty()) {
                        int[] current = queue.poll();
                        for (int[] dir : directions) {
                            int x = current[0] + dir[0];
                            int y = current[1] + dir[1];
                            if (x >= 0 && x < m && y >= 0 && y < n && grid[x][y] == '1') {
                                grid[x][y] = '0';
                                queue.offer(new int[]{x, y});
                            }
                        }
                    }
                }
            }
        }

        return count;
    }

    /**
     * Solution 3: Union-Find (Disjoint Set Union)
     * Time Complexity: O(m * n * α(m * n))
     * Space Complexity: O(m * n)
     */
    public static int computeWithUnionFind(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;

        int m = grid.length;
        int n = grid[0].length;
        UnionFind uf = new UnionFind(grid);

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    if (i + 1 < m && grid[i + 1][j] == '1') {
                        uf.union(i * n + j, (i + 1) * n + j);
                    }
                    if (j + 1 < n && grid[i][j + 1] == '1') {
                        uf.union(i * n + j, i * n + j + 1);
                    }
                }
            }
        }

        return uf.count;
    }

    static class UnionFind {
        int[] parent;
        int[] rank;
        int count;

        UnionFind(char[][] grid) {
            int m = grid.length;
            int n = grid[0].length;
            parent = new int[m * n];
            rank = new int[m * n];
            count = 0;

            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (grid[i][j] == '1') {
                        parent[i * n + j] = i * n + j;
                        count++;
                    }
                }
            }
        }

        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            if (rootX != rootY) {
                if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;
                } else if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;
                }
                count--;
            }
        }
    }
}

