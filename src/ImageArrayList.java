import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class ImageArrayList {
    private int rows;
    private int cols;
    public int mode;
    private ArrayList<Integer>[] X;

    @SuppressWarnings({ "unchecked" })
    public ImageArrayList(int mode, BufferedImage im) throws IOException {
        this.rows = im.getHeight();
        this.cols = im.getWidth();
        this.mode = mode;
        switch (mode) {
            case 0:
                X = new ArrayList[rows];
                for (int i = 0; i < rows; i++) {
                    X[i] = new ArrayList<>();
                    for (int j = 0; j < cols; j++) {
                        X[i].add(im.getRGB(j, i));
                    }
                }
                break;
            case 1:
                X = new ArrayList[cols];
                for (int i = 0; i < cols; i++) {
                    X[i] = new ArrayList<>();
                    for (int j = 0; j < rows; j++) {
                        X[i].add(im.getRGB(i, j));
                    }
                }
                break;
            default:
                break;
        }
    }

    public BufferedImage readToImage() {
       
        BufferedImage rImage;
        if (mode == 0) {
            int rs = X.length;
            int cs = X[0].size();
            rImage = new BufferedImage(cs, rs, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < rs; i++) {
                for (int j = 0; j < cs; j++) {
                    rImage.setRGB(j, i, X[i].get(j));
                }
            }
        } else if (mode == 1) {
            int rs = X[0].size();
            int cs = X.length;
            rImage = new BufferedImage(cs, rs, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < rs; i++) {
                for (int j = 0; j < cs; j++) {
                    rImage.setRGB(j, i, X[j].get(i));
                }
            }
        } else {
            rImage = null;
        }

        return rImage;
    }

    public BufferedImage drawNodes(boolean[][] seamMap_1, boolean [][] seamMap_2, BufferedImage ori) {
        int rs = seamMap_1.length;
        int cs = seamMap_1[0].length;
        BufferedImage rImage = new BufferedImage(cs, rs, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < ori.getHeight(); y++) {
            for (int x = 0; x < ori.getWidth(); x++) {
                int rgb = ori.getRGB(x, y); // 获取原始图像的像素数据
                rImage.setRGB(x, y, rgb); // 将像素数据设置到新图像中
            }
        }

        for (int i = 0; i < rs; i++) {
            for (int j = 0; j < cs; j++) {
                if(seamMap_1[i][j]) rImage.setRGB(j, i, Color.BLACK.getRGB());
            }
        }
        for (int i = 0; i < rs; i++) {
            for (int j = 0; j < cs; j++) {
                if(seamMap_2[i][j]) rImage.setRGB(j, i, Color.BLACK.getRGB());
            }
        }

        return rImage;
    }

    public void deleteAllNodes(boolean[][] seamMap) {
        if (mode == 0) {
            for (int i = X.length - 1; i >= 0; i--) {
                ArrayList<Integer> row = X[i];
                for (int j = row.size() - 1; j >= 0; j--) {
                    if (seamMap[i][j]) {
                        row.remove(j);
                    }
                }
            }
        } else if (mode == 1) {
            for (int i = X[0].size() - 1; i >= 0; i--) {
                for (int j = X.length - 1; j >= 0; j--) {
                    if (seamMap[i][(int)(j/X.length*j)]) {
                        X[j].remove(i);
                    }
                }
            }
        }
        
    }

    
    public void addAllNodes(boolean[][] seamMap) {
        if (mode == 0) {
            for (int i = X.length - 1; i >= 0; i--) {
                ArrayList<Integer> row = X[i];
                for (int j = row.size() - 1; j >= 0; j--) {
                    if (seamMap[i][j]) {
                        row.add(j,row.get(j));
                    }
                }
            }
        } else if (mode == 1) {
            for (int i = X.length - 1; i >= 0; i--) {
                ArrayList<Integer> col = X[i];
                for (int j = col.size() - 1; j >= 0; j--) {
                    if (seamMap[j][(int)(i/X.length*i)]) {
                        col.add(j,col.get(j));
                    }
                }
            }
        }
        
    }
}
