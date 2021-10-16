import java.util.concurrent.RecursiveTask;

public class AvgNeighbours extends RecursiveTask<Integer> {
    int[] src;
    int width;
    int i;
    int j;
    PixelColor px ;

    public AvgNeighbours(int[] src, int width, int i, int j) {
        this.src = src;
        this.width = width;
        this.i = i;
        this.j = j;
        this.px = new PixelColor();
    }

    @Override
    protected Integer compute() {
        return  getPixelColor();
    }

    private int getPixelColor() {
        for (int k = i - 1; k <= i + 1; k++) {
            int y = yIndex(k);
            applyTransformationForIndex(px, y + j - 1);
            applyTransformationForIndex(px, y + j);
            applyTransformationForIndex(px, y + j + 1);
        }
        return px.convertToSingleValue();
    }

    private void applyTransformationForIndex(PixelColor px, int index) {
        int pixel = src[index];
        px.apply(pixel);
    }

    private int yIndex(int row){
        return row * width;
    }
}
