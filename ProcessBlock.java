import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class ProcessBlock extends RecursiveAction {
    private int[] src;
    private int[] dst;
    private  int width;
    private int height;
    private int startX;
    private int startY;
    private int blockSizeX;
    private int blockSizeY;
    private int threshold;

    public ProcessBlock(int[] src, int[] dst, int w, int h, int x, int y , int blockSizeX, int blockSizeY, int threshold) {
        this.src = src;
        this.dst = dst;
        width = w;
        height = h;
        startX = x;
        startY = y;
        this.blockSizeX = blockSizeX;
        this.blockSizeY = blockSizeY;
        this.threshold = threshold;
    }

    @Override
    protected void compute() {
        if (blockSizeY <= threshold || blockSizeX <= threshold) {
            setPixelColorInDestination();
            return;
        }

        ForkJoinTask.invokeAll(createSubtasks());
    }

    private void setPixelColorInDestination() {
        int index;
        for (int i = startY; i < startY + blockSizeY; i++) {
            for (int j = startX; j < startX + blockSizeX; j++) {
                PixelColor px = calculateAvgPixelColor(i, j);
                index = index(j, i);
                dst[index] = px.convertToSingleValue();
            }
        }
    }

    private PixelColor calculateAvgPixelColor(int y, int x) {
        PixelColor px = new PixelColor();
        for (int k = y - 1; k <= y + 1; k++) {
            calculateAverage(x, k, px);
        }
        return px;
    }

    private void calculateAverage(int x, int y, PixelColor px) {
        if ((x - 1) < 0 || (x + 1) > width){
            return;
        }

        applyTransformationForIndex(px, index(x - 1, y));
        applyTransformationForIndex(px, index(x, y));
        applyTransformationForIndex(px, index(x + 1, y));
    }

    private List<ProcessBlock> createSubtasks() {
        List<ProcessBlock> subtasks = new ArrayList<>();
        int firstHalfX = blockSizeX / 2;
        int firstHalfY = blockSizeY / 2;
        int secondHalfX = blockSizeX - firstHalfX;
        int secondHalfY = blockSizeY - firstHalfY ;

        subtasks.add(new ProcessBlock(src, dst, width, height, startX, startY, firstHalfX , firstHalfY, threshold));
        subtasks.add(new ProcessBlock(src, dst, width, height, startX + firstHalfX, startY, secondHalfX, firstHalfY, threshold));
        subtasks.add(new ProcessBlock(src, dst, width, height, startX , startY + firstHalfY, firstHalfX, secondHalfY, threshold));
        subtasks.add(new ProcessBlock(src, dst, width, height, startX + firstHalfX, startY + firstHalfY, secondHalfX, secondHalfY, threshold));
        return subtasks;
    }

    private void applyTransformationForIndex(PixelColor px, int index) {
        int pixel = src[index];
        px.apply(pixel);
    }

    private int index(int x, int y){
        return y * width + x;
    }
}
