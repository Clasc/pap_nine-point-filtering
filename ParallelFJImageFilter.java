import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class ParallelFJImageFilter {
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
        for (int steps = 0; steps < NRSTEPS; steps++) {
            applyStep();
            swapDestAndSrc();
        }
    }

    private void swapDestAndSrc() {
        int[] help;
        help = src;
        src = dst;
        dst = help;
    }

    private void applyStep() {
        int index;
        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                AvgNeighbours task = new AvgNeighbours(src, width, i, j);
                int res = task.compute();
                // Re-assemble destination pixel.
                index = yIndex(i) + j;
                dst[index] = res;
            }
        }
    }

    private int yIndex(int row){
        return row * width;
    }
}
