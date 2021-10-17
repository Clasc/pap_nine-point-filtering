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

    private static final int THRESHOLD = 9;

    public ProcessBlock(int[] src, int[] dst, int w, int h, int x, int y , int blockSizeX, int blockSizeY) {
        this.src = src;
        this.dst = dst;
        width = w;
        height = h;
        startX = x;
        startY=y;
        this.blockSizeX = blockSizeX;
        this.blockSizeY = blockSizeY;
    }

    @Override
    protected void compute() {
        if (blockSizeY <= THRESHOLD || blockSizeX <= THRESHOLD) {
            setPixelColorInDestination();
            return;
        }

        ForkJoinTask.invokeAll(createSubtasks());
    }

    private void setPixelColorInDestination() {
        int index;
        for (int i = startY; i < startY + blockSizeY - 1; i++) {
            for (int j = startX; j < startX + blockSizeX - 1; j++) {
                PixelColor px = calculateAvgPixelColor(i, j);
                index = yIndex(i) + j;
                dst[index] = px.convertToSingleValue();
            }
        }
    }

    private PixelColor calculateAvgPixelColor(int i, int j) {
        PixelColor px = new PixelColor();
        for (int k = i - 1; k <= i + 1; k++) {
            int y = yIndex(k);
            applyTransformationForIndex(px, y + j - 1);
            applyTransformationForIndex(px, y + j);
            applyTransformationForIndex(px, y + j + 1);
        }
        return px;
    }

    private List<ProcessBlock> createSubtasks() {
        List<ProcessBlock> subtasks = new ArrayList<>();
        int newBlockSizeX = blockSizeX / 2;
        int newBlockSizeY = blockSizeY / 2;
        // split into 4 blocks for tasks
        subtasks.add(new ProcessBlock(src, dst, width, height, startX, startY, newBlockSizeX, newBlockSizeY));
        subtasks.add(new ProcessBlock(src, dst, width, height, startX + newBlockSizeX, startY, newBlockSizeX, newBlockSizeY));
        subtasks.add(new ProcessBlock(src, dst, width, height, startX , startY + newBlockSizeY, newBlockSizeX, newBlockSizeY));
        subtasks.add(new ProcessBlock(src, dst, width, height, startX + newBlockSizeX, startY + newBlockSizeY, newBlockSizeX, newBlockSizeY));
        return subtasks;
    }

    private void applyTransformationForIndex(PixelColor px, int index) {
        int pixel = src[index];
        px.apply(pixel);
    }

    private int yIndex(int row){
        return row * width;
    }
}
