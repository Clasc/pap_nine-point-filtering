import java.util.concurrent.ForkJoinPool;

public class ParallelFJImageFilter {
    private static final int MAX_TASKS_PER_THREAD = 5;
    private final int NRSTEPS = 100;

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
        int threshold = calculateThreshold(threads);
        taskPool = new ForkJoinPool(threads);
        for (int steps = 0; steps < NRSTEPS; steps++) {
            ProcessBlock task = new ProcessBlock(src, dst, width, height, 1, 1, width - 2, height - 2, threshold);
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

    private int calculateThreshold(int threads) {
        return ((width + height) / 2) / (threads * threads * MAX_TASKS_PER_THREAD);
    }
}
