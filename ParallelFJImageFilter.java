import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class ParallelFJImageFilter extends RecursiveTask<Integer> {
    private int[] src;
    private int[] dst;
    private  int width;
    private int height;

    private final int NRSTEPS = 100;

    public ParallelFJImageFilter(int[] src, int[] dst, int w, int h) {
        this.src = src;
        this.dst = dst;
        width = w;
        height = h;
    }

    public void apply(int threads) {
    }

    @Override
    protected Integer compute() {
        return 4;
    }
}
