import java.util.concurrent.ForkJoinPool;

public class ParallelFJImageFilter {
    private int[] src;
    private int[] dst;
    private  int width;
    private int height;
    private ForkJoinPool taskPool;
    private final int NRSTEPS = 100;

    public ParallelFJImageFilter(int[] src, int[] dst, int w, int h) {
        this.src = src;
        this.dst = dst;
        width = w;
        height = h;
    }

    public void apply(int threads) {
        taskPool = new ForkJoinPool(threads);
        System.out.println("Number of threads: " + taskPool.getActiveThreadCount());
        for (int steps = 0; steps < NRSTEPS; steps++) {
            ProcessBlock task = new ProcessBlock(src, dst, width, height, 1,1, width-1, height-1);
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
