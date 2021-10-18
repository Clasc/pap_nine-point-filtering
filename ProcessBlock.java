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

    private static final int THRESHOLD = 10;

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
        for (int i = startY; i < startY + blockSizeY; i++) {
            for (int j = startX; j < startX + blockSizeX; j++) {
                PixelColor px = calculateAvgPixelColor(i, j);
                index = index(j, i);
                dst[index] = px.convertToSingleValue();
            }
        }
    }

    private PixelColor calculateAvgPixelColor(int i, int j) {
        PixelColor px = new PixelColor();
        for (int k = i - 1; k <= i + 1; k++) {
            if(index(j-1,k) < 0 || index(j+1,k) >= src.length) {
                continue;
            }
            applyTransformationForIndex(px, index(j - 1,k ));
            applyTransformationForIndex(px, index(j,k));
            applyTransformationForIndex(px, index(j + 1, k));
        }
        return px;
    }

    private List<ProcessBlock> createSubtasks() {
        List<ProcessBlock> subtasks = new ArrayList<>();
        int firstHalfX = blockSizeX / 2;
        int firstHalfY = blockSizeY / 2;
        int secondHalfX = firstHalfX + blockSizeX % 2;
        int secondHalfY = firstHalfY + blockSizeY % 2;

        subtasks.add(new ProcessBlock(src, dst, width, height, startX, startY, firstHalfX , firstHalfY));
        subtasks.add(new ProcessBlock(src, dst, width, height, startX + firstHalfX, startY, secondHalfX, firstHalfY));
        subtasks.add(new ProcessBlock(src, dst, width, height, startX , startY + firstHalfY, firstHalfX, secondHalfY));
        subtasks.add(new ProcessBlock(src, dst, width, height, startX + firstHalfX, startY + firstHalfY, secondHalfX, secondHalfY));
        return subtasks;
    }

    private void applyTransformationForIndex(PixelColor px, int index) {
        int pixel = src[index];
        px.apply(pixel);
    }

    private int index(int x, int y){
        return y * width + x;
    }

    private boolean indexOutOfBounds(int index){
        return index >= src.length || index < 1 ;
    }
}
