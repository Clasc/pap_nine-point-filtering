import java.util.concurrent.ForkJoinPool;

public class ParallelFJImageFilter {
    private static final int MAX_TASKS_PER_THREAD = 80;
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
        taskPool = new ForkJoinPool(threads);
        for (int steps = 0; steps < NRSTEPS; steps++) {
            ProcessBlock task = new ProcessBlock(src, dst, width, height, 1, 1, width - 2, height - 2, calculateThreshold(threads));
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
        return ((width + height) / 2) / (threads * MAX_TASKS_PER_THREAD);
    }
}
