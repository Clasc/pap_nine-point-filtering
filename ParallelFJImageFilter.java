import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ParallelFJImageFilter {
    private static final int NRSTEPS = 100;
    private static final int THRESHOLD = 100;
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
            ProcessBlock task = new ProcessBlock(1, width - 2);
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

    public class ProcessBlock extends RecursiveAction {
        private final int startX;
        private final int blockSize;

        public ProcessBlock(int x, int blockSize) {
            startX = x;
            this.blockSize = blockSize;
        }

        @Override
        protected void compute() {
            if (blockSize <= THRESHOLD) {
                setPixelColorInDestination();
                return;
            }

            separateToSubTasks();
        }

        private void setPixelColorInDestination() {
            int pixel;
            for (int y = 1; y < height - 1; y++) {
                for (int x = startX; x < startX + blockSize; x++) {
                    float rt = 0, gt = 0, bt = 0;
                    for (int k = y - 1; k <= y + 1; k++) {
                        if ((x - 1) < 0 || (x + 1) > width) {
                            continue;
                        }

                        pixel = src[index(x - 1, k)];
                        rt += (float) ((pixel & 0x00ff0000) >> 16);
                        gt += (float) ((pixel & 0x0000ff00) >> 8);
                        bt += (float) ((pixel & 0x000000ff));

                        pixel = src[index(x, k)];
                        rt += (float) ((pixel & 0x00ff0000) >> 16);
                        gt += (float) ((pixel & 0x0000ff00) >> 8);
                        bt += (float) ((pixel & 0x000000ff));

                        pixel = src[index(x + 1, k)];
                        rt += (float) ((pixel & 0x00ff0000) >> 16);
                        gt += (float) ((pixel & 0x0000ff00) >> 8);
                        bt += (float) ((pixel & 0x000000ff));
                    }
                    dst[index(x, y)] = (0xff000000) | (((int) rt / 9) << 16) | (((int) gt / 9) << 8) | (((int) bt / 9));
                }
            }
        }

        private void separateToSubTasks() {
            int firstHalf = blockSize / 2;

            invokeAll(
                    new ProcessBlock(startX, firstHalf),
                    new ProcessBlock(startX + firstHalf, blockSize - firstHalf)
            );
        }


        private int index(int x, int y) {
            return y * width + x;
        }
    }
}
