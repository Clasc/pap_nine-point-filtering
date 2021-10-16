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
		int pixel;
		for (int i = 1; i < height - 1; i++) {
			for (int j = 1; j < width - 1; j++) {
				PixelColor px = new PixelColor();
				for (int k = i - 1; k <= i + 1; k++) {
					index = k * width + j - 1;
					pixel = src[index];
					calculate(pixel, px);

					index = k * width + j;
					pixel = src[index];
					calculate(pixel, px);

					index = k * width + j + 1;
					pixel = src[index];
					calculate(pixel, px);
				}
				// Re-assemble destination pixel.
				index = i * width + j;
				int dpixel = (0xff000000) | (((int) px.rt / 9) << 16) | (((int) px.gt / 9) << 8) | (((int) px.bt / 9));
				dst[index] = dpixel;
			}
		}
	}

	private void calculate(int pixel, PixelColor px) {
		px.rt += (float) ((pixel & 0x00ff0000) >> 16);
		px.gt += (float) ((pixel & 0x0000ff00) >> 8);
		px.bt += (float) ((pixel & 0x000000ff));
	}
}