import java.util.concurrent.ForkJoinPool;

public class ParallelFJImageFilter {
    private int[] src;
    private int[] dst;
    private  int width;
    private int height;
    private ForkJoinPool taskPool = ForkJoinPool.commonPool();
    private final int NRSTEPS = 100;

    public ParallelFJImageFilter(int[] src, int[] dst, int w, int h) {
        this.src = src;
        this.dst = dst;
        width = w;
        height = h;
    }

    public void apply(int threads) {
        for (int steps = 0; steps < NRSTEPS; steps++) {
            ProcessBlock task = new ProcessBlock(src, dst, width, height, 1,1, width, height);
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
}
