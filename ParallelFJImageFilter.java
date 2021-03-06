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
            int pixel;
            int endX = startX + blockSizeX, endY = startY + blockSizeY;

            for (int y = startY; y < endY; y++) {
                int kBegin = y - 1, kEnd = y + 1;

                for (int x = startX; x < endX; x++) {
                    float rt = 0, gt = 0, bt = 0;
                    for (int k = kBegin; k <= kEnd; k++) {
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


        private int index(int x, int y) {
            return y * width + x;
        }
    }
}
