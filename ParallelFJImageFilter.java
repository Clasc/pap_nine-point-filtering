import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ParallelFJImageFilter {
    private static final int NRSTEPS = 100;
    private static final int THRESHOLD = 1000;
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
            ProcessBlock task = new ProcessBlock(1, 1, width - 2, height - 2);
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
        private final int startY;
        private final int blockSizeX;
        private final int blockSizeY;

        public ProcessBlock(int x, int y, int blockSizeX, int blockSizeY) {
            startX = x;
            startY = y;
            this.blockSizeX = blockSizeX;
            this.blockSizeY = blockSizeY;
        }

        @Override
        protected void compute() {
            if (blockSizeX <= THRESHOLD || blockSizeY <= THRESHOLD) {
                setPixelColorInDestination();
                return;
            }

            separateToSubTasks();
        }

        private void setPixelColorInDestination() {
            int index, pixel;
            for (int steps = 0; steps < NRSTEPS; steps++) {
                for (int i = 1; i < height - 1; i++) {
                    for (int j = 1; j < width - 1; j++) {
                        float rt = 0, gt = 0, bt = 0;
                        for (int k = i - 1; k <= i + 1; k++) {
                            index = k * width + j - 1;
                            pixel = src[index];
                            rt += (float) ((pixel & 0x00ff0000) >> 16);
                            gt += (float) ((pixel & 0x0000ff00) >> 8);
                            bt += (float) ((pixel & 0x000000ff));

                            index = k * width + j;
                            pixel = src[index];
                            rt += (float) ((pixel & 0x00ff0000) >> 16);
                            gt += (float) ((pixel & 0x0000ff00) >> 8);
                            bt += (float) ((pixel & 0x000000ff));

                            index = k * width + j + 1;
                            pixel = src[index];
                            rt += (float) ((pixel & 0x00ff0000) >> 16);
                            gt += (float) ((pixel & 0x0000ff00) >> 8);
                            bt += (float) ((pixel & 0x000000ff));
                        }
                        // Re-assemble destination pixel.
                        index = i * width + j;
                        int dpixel = (0xff000000) | (((int) rt / 9) << 16) | (((int) gt / 9) << 8) | (((int) bt / 9));
                        dst[index] = dpixel;
                    }
                }
            }
        }

        private void separateToSubTasks() {
            int firstHalfX = blockSizeX / 2;
            int firstHalfY = blockSizeY / 2;
            int secondHalfX = blockSizeX - firstHalfX;
            int secondHalfY = blockSizeY - firstHalfY;

            invokeAll(
                    new ProcessBlock[]{
                            new ProcessBlock(startX, startY, firstHalfX, firstHalfY),
                            new ProcessBlock(startX + firstHalfX, startY, secondHalfX, firstHalfY),
                            new ProcessBlock(startX, startY + firstHalfY, firstHalfX, secondHalfY),
                            new ProcessBlock(startX + firstHalfX, startY + firstHalfY, secondHalfX, secondHalfY)
                    });
        }
    }
}
