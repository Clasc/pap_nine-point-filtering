/**
 * Iterative nine-point image convolution filter working on linearized image. 
 * In each of the NRSTEPS iteration steps, the average RGB-value of each pixel 
 * in the source array is computed taking into account the pixel and its 8 neighbor 
 * pixels (in 2D) and written to the destination array.
 */
public class ImageFilter {
	private int[] src;
	private int[] dst;
	private int width;
	private int height;

	private final int NRSTEPS = 100;  

	public ImageFilter(int[] src, int[] dst, int w, int h) {
		this.src = src;
		this.dst = dst;
		width = w;
		height = h;
	}

	public void apply() {
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
				PixelColor px = new PixelColor();
				for (int k = i - 1; k <= i + 1; k++) {
					applyTransformationForIndex(px, k * width + j - 1);
					applyTransformationForIndex(px, k * width + j);
					applyTransformationForIndex(px, k * width + j + 1);
				}
				// Re-assemble destination pixel.
				index = i * width + j;
				dst[index] = px.convertToSingleValue();
			}
		}
	}

	private void applyTransformationForIndex(PixelColor px, int index) {
		int pixel = src[index];
		px.apply(pixel);
	}
}