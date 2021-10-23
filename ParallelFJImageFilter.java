import java.util.concurrent.ForkJoinPool;

public class ParallelFJImageFilter {
    /**
     * The maximal number of allowed recursions.
     * Be careful setting this too high will take very long in execution
     * For every recursion there will be 4 new tasks.
     * So for 5 recursions there will be 4^4 = 1024 tasks.
     */
    private static final int MAX_RECURSIONS = 5;
    private static final int NRSTEPS = 100;

    private final int width;
    private final int height;
    private int[] src;
    private int[] dst;
    private ForkJoinPool taskPool;

    public ParallelFJImageFilter(int[] src, int[] dst, int w, int h) {
        this.src = src;
        this.dst = dst;
        width = w;
        height = h;
    }

    public void apply(int threads) {
        int recursions = recursions(threads);
        taskPool = new ForkJoinPool(threads);
        for (int steps = 0; steps < NRSTEPS; steps++) {
            ProcessBlock task = new ProcessBlock(src, dst, width, height, 1, 1, width - 2, height - 2, recursions);
            taskPool.invoke(task);
            swapDestAndSrc();
        }
    }

    private void swapDestAndSrc() {
        int[] help;
        help = src;
        src = dst;
        dst = help;
    }

    private int recursions(int threads) {
        return Math.min(threads, MAX_RECURSIONS);
    }
}
