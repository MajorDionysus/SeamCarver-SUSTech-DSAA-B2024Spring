import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImagePreprocessor {
    private static BufferedImage originImage;
    private static BufferedImage eImage;
    private static int[][] energy;
    final int width;
    final int height;
    private int[][] dp;

    public ImagePreprocessor(BufferedImage Image) {
        originImage = Image;
        this.width = originImage.getWidth();
        this.height = originImage.getHeight();
        energy = new int[height][width];
        this.applySobelFilter();
    }

    private void applySobelFilter() {

        eImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        int[][] sobelX = {
                { -1, 0, 1 },
                { -2, 0, 2 },
                { -1, 0, 1 }
        };
        int[][] sobelY = {
                { -1, -2, -1 },
                { 0, 0, 0 },
                { 1, 2, 1 }
        };

        // sobel 卷积核名没改，实际这里用的普通的梯度核

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int sumXR = 0, sumXG = 0, sumXB = 0;
                int sumYR = 0, sumYG = 0, sumYB = 0;

                // 二维卷积,based on three tunnals
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int rgb = originImage.getRGB(x + kx, y + ky);

                        int r = (rgb >> 16) & 0xFF; // rgb(0~2^8) every color 8 bit, totaly 32 bit (plus alpha?)
                        int g = (rgb >> 8) & 0xFF; // "rgb >> #" means rightshift # step to make selected color head
                        int b = rgb & 0xFF;

                        // 0xFF是一个十六进制数，表示为11111111，即8位全为1的二进制数。
                        // rgb & 0xFF表示将RGB颜色值与0xFF进行按位与操作。
                        // 按位与操作的规则是：如果两个对应位都为1，则结果为1，否则为0.
                        // 由于0xFF的低8位全为1，按位与操作会保留RGB颜色值的低8位，而将其他位清零。
                        // 这样就提取出了RGB颜色值中的蓝色通道值。

                        sumXR += sobelX[ky + 1][kx + 1] * r;
                        sumXG += sobelX[ky + 1][kx + 1] * g;
                        sumXB += sobelX[ky + 1][kx + 1] * b;

                        sumYR += sobelY[ky + 1][kx + 1] * r;
                        sumYG += sobelY[ky + 1][kx + 1] * g;
                        sumYB += sobelY[ky + 1][kx + 1] * b;
                    }
                }

                // 分别取模长
                int gradientMagnitudeR = (int) (Math.sqrt(sumXR * sumXR + sumYR * sumYR) * 0.299);
                int gradientMagnitudeG = (int) (Math.sqrt(sumXG * sumXG + sumYG * sumYG) * 0.587);
                int gradientMagnitudeB = (int) (Math.sqrt(sumXB * sumXB + sumYB * sumYB) * 0.114);

                // 将梯度大小限制在 [0, 255] 之间
                gradientMagnitudeR = Math.min(255, Math.max(0, gradientMagnitudeR));
                gradientMagnitudeG = Math.min(255, Math.max(0, gradientMagnitudeG));
                gradientMagnitudeB = Math.min(255, Math.max(0, gradientMagnitudeB));

                // 设置结果图像中的像素值
                int finalPixel = (gradientMagnitudeR << 16) + (gradientMagnitudeG << 8) + gradientMagnitudeB;
                eImage.setRGB(x, y, finalPixel);
                energy[y][x] = (int) (finalPixel);
            }
        }
        energy[0] = energy[1];
        energy[height - 1] = energy[height - 2];
        for (int i = 0; i < width; i++) {
            eImage.setRGB(i, 0, energy[1][i]);
            eImage.setRGB(i, height - 1, energy[height - 2][i]);
        }
        for (int i = 0; i < height; i++) {
            energy[i][0] = energy[i][1];
            eImage.setRGB(0, i, energy[i][1]);
            energy[i][width - 1] = energy[i][width - 2];
            eImage.setRGB(width - 1, i, energy[i][width - 2]);
        }

    }

    private boolean[][] findKMinPaths(int[] seamNum, int mode) {

        // 结果矩阵，用于存储最小路径的标记
        boolean[][] result = new boolean[height][width];

        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                result[i][j] = false;
            }
        }
        if (mode == 0) {
            result = findVerticalPaths(Math.abs(seamNum[0]), result);
        } else {
            result = findHorizontalPaths(Math.abs(seamNum[1]), result);
        }
        {

        }
        return result;

    }

    private boolean[][] findVerticalPaths(int seamNum, boolean[][] marked) {
        // 动态规划数组，用于存储最小路径的权值
        dp = new int[height][width];

        // 动态规划初始化：从起点开始到每个位置的最小路径
        for (int j = 0; j < width; j++) {
            dp[0][j] = energy[0][j];
        }

        // 动态规划迭代：从第二行开始计算每个位置的最小路径权值
        for (int i = 1; i < height; i++) {
            dp[i][0] = energy[i][0] + Math.min(dp[i - 1][0], dp[i - 1][1]);
            dp[i][width - 1] = energy[i][0] + Math.min(dp[i - 1][width - 1], dp[i - 1][width - 2]);
            for (int j = 1; j < width - 1; j++) {
                dp[i][j] = energy[i][j] + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i - 1][j + 1]));
            }
        }

        specialCarve();
        for (int p = 0; p < seamNum; p++) {

            int minPath = Integer.MAX_VALUE;
            int endCol = 0;

            for (int j = 0; j < width; j++) {
                if (dp[height - 1][j] < minPath) {
                    minPath = dp[height - 1][j];
                    endCol = j;
                }
            }

            dp[height - 1][endCol] = Integer.MAX_VALUE;

            // Backtrack the minimum path from the last row
            int row = height - 1;
            int count = 0;
            while (row >= 0) {
                if (!marked[row][endCol] && !doublemarked[row][endCol]) {
                    marked[row][endCol] = true; // Mark the path point in the result matrix

                    dp[row][endCol] = Integer.MAX_VALUE;
                    count = 0;
                } else {
                    row++;
                    count++;
                    if (endCol > width - 4) {
                        endCol-=3;
                    } else {
                        endCol+=3;
                    }
                    if (count > 2) {
                        endCol = (int) (Math.random() * (width-endCol));
                        count=0;
                    }
                }
                if (endCol > 0 && row > 0 && !triblemarked[row][endCol]) {
                    int minCol = endCol;
                    if (endCol > 0 && row > 0 && dp[row - 1][endCol - 1] < dp[row - 1][minCol]) {
                        minCol = endCol - 1;
                    }
                    if (endCol < width - 1 && row > 0 && dp[row - 1][endCol + 1] < dp[row - 1][minCol]) {
                        minCol = endCol + 1;
                    }
                    endCol = minCol;
                }
                row--;
            }
        }
        return marked;
    }

    private boolean[][] findHorizontalPaths(int seamNum, boolean[][] marked) {

        // 动态规划数组，用于存储最小路径的权值
        dp = new int[height][width];

        // 动态规划初始化：从起点开始到每个位置的最小路径
        for (int j = 0; j < height; j++) {
            dp[j][0] = energy[j][0];
        }

        // 动态规划迭代：从第二column开始计算每个位置的最小路径权值
        for (int i = 1; i < width; i++) {
            dp[0][i] = energy[0][i] + Math.min(dp[0][i - 1], dp[1][i - 1]);
            dp[height - 1][i] = energy[0][i] + Math.min(dp[height - 1][i - 1], dp[height - 2][i - 1]);
            for (int j = 1; j < height - 1; j++) {
                dp[j][i] = energy[j][i] + Math.min(dp[j - 1][i], Math.min(dp[j][i - 1], dp[j + 1][i - 1]));
            }
        }
        specialCarve();

        for (int p = 0; p < seamNum; p++) {
            int minPath = Integer.MAX_VALUE;
            int endRow = 0;

            for (int j = 0; j < height; j++) {
                if (dp[j][width - 1] < minPath) {
                    minPath = dp[j][width - 1];
                    endRow = j;
                }
            }
            dp[endRow][width - 1] = Integer.MAX_VALUE;

            // Backtrack the minimum path from the last row
            int col = width - 1;
            int count = 0;
            while (col >= 0) {
                if (!marked[endRow][col] && !doublemarked[endRow][col]) {
                    marked[endRow][col] = true; // Mark the path point in the result matrix
                    dp[endRow][col] = Integer.MAX_VALUE;
                    count = 0;
                } else {
                    col++;
                    count++;
                    if (endRow == height - 1) {
                        endRow--;
                    } else {
                        endRow++;
                    }
                    if (count > 2) {
                        endRow = (int) (Math.random() * (height-endRow));
                    }
                }
                if (endRow < height - 1 && col > 0 && !triblemarked[endRow][col]) {
                    int minRow = endRow;
                    // Choose the next path point from the adjacent positions
                    if (endRow > 0 && col > 0 && dp[endRow - 1][col - 1] < dp[minRow][col]) {
                        minRow = endRow - 1;
                    }
                    if (endRow < height - 1 && col > 0 && dp[endRow + 1][col - 1] < dp[minRow][col]) {
                        minRow = endRow + 1;
                    }
                    endRow = minRow;
                }
                col--;
            }
        }
        return marked;

    }

    public BufferedImage[] carv(int[] seamNum) throws IOException {

        boolean[][] paths_1 = findKMinPaths(seamNum, 0);
        ImageArrayList ia_1 = new ImageArrayList(0, originImage);
        if (seamNum[0] >= 0) {
            ia_1.deleteAllNodes(paths_1);
        } else {
            ia_1.addAllNodes(paths_1);
        }
        BufferedImage afImage = ia_1.readToImage();

        boolean[][] paths_2 = findKMinPaths(seamNum, 1);
        ImageArrayList ia_2 = new ImageArrayList(1, afImage);
        if (seamNum[1] >= 0) {
            ia_2.deleteAllNodes(paths_2);
        } else {
            ia_2.addAllNodes(paths_2);
        }
        BufferedImage fImage = ia_2.readToImage();

        BufferedImage Musk = ia_2.drawNodes(paths_1, paths_2, originImage);
        return new BufferedImage[] { eImage, Musk, fImage };
    }

    private BufferedImage Limage;

    public void setLimage(BufferedImage limage) {
        this.Limage = limage;
    }

    Boolean[][] doublemarked;
    Boolean[][] triblemarked;

    public void specialCarve() {
        doublemarked = new Boolean[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                doublemarked[i][j] = false;
            }
        }
        triblemarked = new Boolean[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                triblemarked[i][j] = false;
            }
        }
        // for (int i = 300; i < 433; i++) {
        //     for (int j = 300; j < 500; j++) {
        //         triblemarked[i][j] = true;
        //     }
        // }
        if (Limage != null) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (Limage.getRGB(i, j) == Color.RED.getRGB()) {
                        dp[j][i] = 0;
                        triblemarked[j][i] = true;
                    } else if (Limage.getRGB(i, j) == Color.BLUE.getRGB()) {
                        dp[j][i] = Integer.MAX_VALUE;
                        doublemarked[j][i] = true;
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File("res//input_original(1).jpg"));
        ImagePreprocessor ip = new ImagePreprocessor(image);
        BufferedImage[] images = ip.carv(new int[] { 300, 250 });

        File output0 = new File("out/Energy.png");
        ImageIO.write(images[0], "png", output0);
        File output = new File("out/Musk.png");
        ImageIO.write(images[1], "png", output);
        File output1 = new File("out/Carved.jpg");
        ImageIO.write(images[2], "jpg", output1);

        System.out.println("Image edge enhancement successful.");

    }

}
