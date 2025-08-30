import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageEditorGUI extends JFrame {
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 800;
    private static final int IMAGE_PANEL_WIDTH = FRAME_WIDTH;
    private static final int IMAGE_PANEL_HEIGHT = 600;
    private static final int CONTROL_PANEL_WIDTH = FRAME_WIDTH;
    private static final int CONTROL_PANEL_HEIGHT = 200;
    private static final int LEFT_PANEL_WIDTH = 200;
    private static final int LEFT_PANEL_HEIGHT = CONTROL_PANEL_HEIGHT;
    private static final int BUTTON_WIDTH = 50;
    private static final int BUTTON_HEIGHT = 50;
    private static final int CENTER_PANEL_WIDTH = 450;
    private static final int CENTER_PANEL_HEIGHT = CONTROL_PANEL_HEIGHT;
    private static final int RIGHT_PANEL_WIDTH = 150;
    private static final int RIGHT_PANEL_HEIGHT = CONTROL_PANEL_HEIGHT;
    private static final int CONFIRM_BUTTON_HEIGHT = 50;

    private static double scale;
    private static int x;
    private static int y;
    private static String whichAixScale;

    private JLabel resolutionIndicator;
    private JPanel imagePanel;
    private JPanel controlPanel;
    private JButton importButton1;
    private JButton importButton2;
    private JSlider horizontalSlider;
    private JSlider verticalSlider;
    private JButton confirmButton;
    private JSlider brushSizeSlider;
    private BufferedImage image;
    private BufferedImage originalImage;
    private BufferedImage mask;
    private BufferedImage eImage;
    private BufferedImage Limage;
    private int[] imageSize = { 100, 100 };
    private Point lastPoint;
    private Color currentColor = Color.RED;
    private double zoomX = 1;
    private double zoomY = 1;
    private int brushSize = 5;
    private RestrictedNumberField textField;
    private RestrictedNumberField textField1;
    private ImagePreprocessor ip;

    public ImageEditorGUI() {

        // 设置窗口标题
        setTitle("Image Editor");

        // 设置窗口大小并禁止调整大小
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);

        // 设置应用程序的全局外观为 Nimbus
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // 添加一个用于关闭的按钮
        JButton closeButton = new JButton("Close", new ImageIcon(
                new ImageIcon("res\\delete.png").getImage().getScaledInstance(24, 24, Image.SCALE_REPLICATE)));
        closeButton.setFont(new Font("Ocr a extended", Font.BOLD, 18));
        closeButton.setContentAreaFilled(false); // 设置按钮内容区域透明
        closeButton.setBounds(FRAME_WIDTH - 120, 4, 120, 26); // 设置按钮位置和大小
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(null, "Confirm exit?", "Exiting...",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    // 退出
                    System.exit(0);
                }

            }
        });
        closeButton.setBackground(Color.WHITE);
        setBackground(Color.BLACK);

        // 设置窗口为无装饰样式（Undecorated）
        setUndecorated(true);

        setLocationRelativeTo(null);
        // 使用空布局
        setLayout(null);

        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        // 创建文件菜单
        JMenu fileMenu = new JMenu("Menu");
        fileMenu.setFont(new Font("Ocr a extended", Font.BOLD, 18)); // 设置菜单项字体

        JMenuItem tutorialItem = new JMenuItem("Tutorial");
        tutorialItem.setFont(new Font("Ocr a extended", Font.ITALIC, 16));
        tutorialItem.setIcon(new ImageIcon(
                new ImageIcon("res\\tutorial.png").getImage().getScaledInstance(18, 18, Image.SCALE_DEFAULT)));

        // 创建打开菜单项
        JMenuItem openItem = new JMenuItem("Upload");
        openItem.setFont(new Font("Ocr a extended", Font.ITALIC, 16)); // 设置菜单项字体
        openItem.setIcon(new ImageIcon(
                new ImageIcon("res\\open-folder.png").getImage().getScaledInstance(16, 16, Image.SCALE_REPLICATE))); // 添加图标

        // 创建Mask菜单项
        JMenuItem maskItem = new JMenuItem("Mask View");
        maskItem.setFont(new Font("Ocr a extended", Font.ITALIC, 16)); // 设置菜单项字体
        maskItem.setIcon(new ImageIcon(
                new ImageIcon("res\\masking.png").getImage().getScaledInstance(18, 18, Image.SCALE_REPLICATE))); // 添加图标
        maskItem.addActionListener(e -> {

            JFrame picView = new JFrame();
            if (mask != null) {
                picView.setTitle("Mask");
                picView.setSize(mask.getWidth(), mask.getHeight());
                JPanel vPanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);

                        double imgWidth = mask.getWidth();
                        double imgHeight = mask.getHeight();
                        double scaleX = picView.getWidth() / imgWidth;
                        double scaleY = picView.getHeight() / imgHeight;
                        scale = Math.min(scaleX, scaleY);

                        g.drawImage(mask, 0, 0, (int) (imgWidth * scale), (int) (imgHeight * scale), null);
                    }
                };
                picView.add(vPanel);
                picView.setLocationRelativeTo(null);
                picView.setVisible(true);

            } else {
                JOptionPane.showMessageDialog(null, "No mask produced!!!");
            }
        });

        tutorialItem.addActionListener(e -> {
            JFrame picView = new JFrame();
            picView.setTitle("Tutorial");
            picView.setSize(800, 600); // Keep the same size
            JPanel vPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);

                    double imgWidth = 800;
                    double imgHeight = 600;
                    double scaleX = picView.getWidth() / imgWidth;
                    double scaleY = picView.getHeight() / imgHeight;
                    scale = Math.min(scaleX, scaleY);

                    g.drawImage(new ImageIcon("res\\tutorial_content.png").getImage(), 0, 0, (int) (imgWidth * scale),
                            (int) (imgHeight * scale), null);
                }
            };

            // JTextArea for tutorial instructions
            JTextArea instructionsArea = new JTextArea();
            instructionsArea.setEditable(false);
            instructionsArea.setLineWrap(true);
            instructionsArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(instructionsArea);
            scrollPane.setPreferredSize(new Dimension(600, 200));

            instructionsArea.setText("""
                    事先说明：
                        1. 笔刷请在初始图片上或 reset 后使用

                        2. 出现卡顿有可能是如下原因：

                            a. 画笔涂抹区域大于目标图片的尺寸，已死机，请重启
                            b. 可能在处理中，请等待...
                            c. 暂不支持处理的带 alpha 图层的透明 png 图片
                            （待更新）

                        3. seam 有一定的随机性如遇出图不理想可试试连续点击 'confirm', 进行抽卡

                        4. 输入框有数值限制，过大过小会导致输不进去

                        5. 对象保护和删除功能均是基于标记实现的（被动的），因而，保护效果可以较好实现但删除效果不佳

                        6. E-Map 和 Mask 图片（对 Seam 的可视化）暂时没做 Donwload 功能， 只能截图保存

                        待补充
                        ...

                        """);

            // Show JOptionPane with the image panel and instructions area
            JOptionPane.showMessageDialog(picView, scrollPane, "Tutorial", JOptionPane.PLAIN_MESSAGE);
            picView.add(vPanel);
            picView.setLocationRelativeTo(null);
            picView.setVisible(true);
        });

        // 创建Mask菜单项
        JMenuItem eItem = new JMenuItem("E-Map View");
        eItem.setFont(new Font("Ocr a extended", Font.ITALIC, 16)); // 设置菜单项字体
        eItem.setIcon(new ImageIcon(
                new ImageIcon("res\\tool.png").getImage().getScaledInstance(16, 20, Image.SCALE_REPLICATE))); // 添加图标
        eItem.addActionListener(e -> {

            JFrame picView = new JFrame();
            if (eImage != null) {
                picView.setTitle("E-Map");
                picView.setSize(eImage.getWidth(), eImage.getHeight());
                JPanel vPanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);

                        double imgWidth = eImage.getWidth();
                        double imgHeight = eImage.getHeight();
                        double scaleX = picView.getWidth() / imgWidth;
                        double scaleY = picView.getHeight() / imgHeight;
                        scale = Math.min(scaleX, scaleY);

                        g.drawImage(eImage, 0, 0, (int) (imgWidth * scale), (int) (imgHeight * scale), null);
                    }
                };
                picView.add(vPanel);
                picView.setLocationRelativeTo(null);
                picView.setVisible(true);

            } else {
                JOptionPane.showMessageDialog(null, "No E-Map produced!!!");
            }
        });

        // 创建保存菜单项
        JMenuItem saveItem = new JMenuItem("Download");
        saveItem.setFont(new Font("Ocr a extended", Font.ITALIC, 16)); // 设置菜单项字体
        saveItem.setIcon(new ImageIcon(
                new ImageIcon("res\\diskette.png").getImage().getScaledInstance(16, 16, Image.SCALE_REPLICATE))); // 添加图标

        openItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setPreferredSize(new Dimension(400, 400));
            fileChooser.setCurrentDirectory(new File("res"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Image Files", "jpg", "jpeg", "png");
            fileChooser.setFileFilter(filter);
            int result = fileChooser.showOpenDialog(ImageEditorGUI.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    image = ImageIO.read(selectedFile);
                    originalImage = ImageIO.read(selectedFile);
                    imageSize[0] = image.getWidth();
                    imageSize[1] = image.getHeight();
                    Limage = new BufferedImage(imageSize[0], imageSize[1], BufferedImage.TYPE_INT_RGB);
                    intialize();
                    repaintImage();
                    repaint();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        saveItem.addActionListener(e -> {
            if (image != null) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setPreferredSize(new Dimension(400, 400));
                fileChooser.setCurrentDirectory(new File("G:/BME专业课/Dsaa-B/ProjectSeamCarving/out"));
                int result = fileChooser.showSaveDialog(ImageEditorGUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        ImageIO.write(image, "jpg", file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "No image produced!!!");
            }
        });

        fileMenu.add(tutorialItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(eItem);
        fileMenu.add(maskItem);
        menuBar.add(fileMenu);
        add(closeButton);
        setJMenuBar(menuBar);

        imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                int panelWidth = getWidth();
                int panelHeight = getHeight();

                if (image != null) {
                    double imgWidth = image.getWidth();
                    double imgHeight = image.getHeight();
                    double scaleX = panelWidth / imgWidth;
                    double scaleY = panelHeight / imgHeight;
                    scale = Math.min(scaleX, scaleY);

                    if (scaleX < scaleY) {
                        whichAixScale = "x";
                        scale = scaleX;
                    } else {
                        whichAixScale = "y";
                        scale = scaleY;
                    }

                    x = (int) ((panelWidth - imgWidth * scale) / 2);
                    y = (int) ((panelHeight - imgHeight * scale) / 2);
                    g2d.drawImage(image, x, y, (int) (imgWidth * scale), (int) (imgHeight * scale), this);
                    g2d.scale(1, 1);
                }
            }
        };
        imagePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (whichAixScale == "x") {
                    lastPoint = new Point(e.getX(), e.getY() - y);
                } else if (whichAixScale == "y") {
                    lastPoint = new Point(e.getX() - x, e.getY());
                }
            }
        });
        imagePanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastPoint != null) {

                    Graphics2D g2d = image.createGraphics();
                    g2d.setColor(currentColor);
                    g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.scale(1 / scale, 1 / scale);

                    Graphics2D g2d2 = Limage.createGraphics();
                    g2d2.setColor(currentColor);
                    g2d2.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d2.scale(1 / scale, 1 / scale);

                    if (whichAixScale == "x") {
                        g2d.draw(new Line2D.Double(lastPoint, new Point(e.getX(), e.getY() - y)));
                        g2d2.draw(new Line2D.Double(lastPoint, new Point(e.getX(), e.getY() - y)));
                        lastPoint = new Point(e.getX(), e.getY() - y);
                    } else if (whichAixScale == "y") {
                        g2d.draw(new Line2D.Double(lastPoint, new Point(e.getX() - x, e.getY())));
                        g2d2.draw(new Line2D.Double(lastPoint, new Point(e.getX() - x, e.getY())));
                        lastPoint = new Point(e.getX() - x, e.getY());
                    }
                    repaintImage();
                }
            }
        });

        imagePanel.setLayout(new BorderLayout());
        imagePanel.setBounds(0, 0, IMAGE_PANEL_WIDTH, IMAGE_PANEL_HEIGHT);

        controlPanel = new JPanel();
        controlPanel.setLayout(null);
        controlPanel.setBounds(0, IMAGE_PANEL_HEIGHT, CONTROL_PANEL_WIDTH, CONTROL_PANEL_HEIGHT);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(null);
        leftPanel.setBounds(0, 0, LEFT_PANEL_WIDTH, LEFT_PANEL_HEIGHT);
        leftPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        ImageIcon icon_1 = new ImageIcon("res\\pen.png");
        Image image_1 = icon_1.getImage().getScaledInstance(BUTTON_WIDTH, BUTTON_HEIGHT, Image.SCALE_REPLICATE);
        importButton1 = new JButton(new ImageIcon(image_1));
        importButton1.addActionListener(e -> {
            currentColor = Color.RED;
            repaintImage();
        });
        importButton1.setContentAreaFilled(false); // 设置按钮内容区域透明
        importButton1.setBounds(25, 25, BUTTON_WIDTH, BUTTON_HEIGHT);

        ImageIcon icon_2 = new ImageIcon("res\\pencil.png");
        Image image_2 = icon_2.getImage().getScaledInstance(BUTTON_WIDTH, BUTTON_HEIGHT, Image.SCALE_REPLICATE);
        importButton2 = new JButton(new ImageIcon(image_2));
        importButton2.addActionListener(e -> {
            currentColor = Color.BLUE;
            repaintImage();
        });
        importButton2.setContentAreaFilled(false); // 设置按钮内容区域透明
        importButton2.setBounds(125, 25, BUTTON_WIDTH, BUTTON_HEIGHT);

        leftPanel.add(importButton1);
        leftPanel.add(importButton2);

        brushSizeSlider = new JSlider(JSlider.HORIZONTAL, 1, 20, 10);
        brushSizeSlider.addChangeListener(e -> brushSize = brushSizeSlider.getValue());
        brushSizeSlider.setBounds(10, 80, 180, 50);
        brushSizeSlider.setMajorTickSpacing(5);
        brushSizeSlider.setMinorTickSpacing(1);
        brushSizeSlider.setPaintTicks(true);
        brushSizeSlider.setPaintLabels(true);
        leftPanel.add(brushSizeSlider);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(null);
        centerPanel.setBounds(LEFT_PANEL_WIDTH, 0, CENTER_PANEL_WIDTH, CENTER_PANEL_HEIGHT);
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel widthLabel = new JLabel("ZoomX:");
        JLabel heightLabel = new JLabel("ZoomY:");
        widthLabel.setFont(new Font("arialblack", Font.BOLD, 16));
        heightLabel.setFont(new Font("arialblack", Font.BOLD, 16));
        widthLabel.setBounds(2, 25, 80, 20);
        heightLabel.setBounds(2, 85, 80, 20);

        horizontalSlider = new JSlider(JSlider.HORIZONTAL, 50, 200, 100);
        horizontalSlider.setBounds(60, 20, 270, 50);
        horizontalSlider.setMajorTickSpacing(50);
        horizontalSlider.setMinorTickSpacing(10);
        horizontalSlider.setPaintTicks(true);
        horizontalSlider.setPaintLabels(true);

        verticalSlider = new JSlider(JSlider.HORIZONTAL, 50, 200, 100);
        verticalSlider.setBounds(60, 80, 270, 50);
        verticalSlider.setMajorTickSpacing(50);
        verticalSlider.setMinorTickSpacing(10);
        verticalSlider.setPaintTicks(true);
        verticalSlider.setPaintLabels(true);

        resolutionIndicator = new JLabel(String.format("          %d * %d px", imageSize[0], imageSize[1]));
        resolutionIndicator.setBounds(12, 132, 300, 40);
        resolutionIndicator.setFont(new Font("arialblack", Font.BOLD, 24));
        ImageIcon icon_3 = new ImageIcon("res\\pixel.png");
        Image image_3 = icon_3.getImage().getScaledInstance(30, 30, Image.SCALE_REPLICATE);
        resolutionIndicator.setIcon(new ImageIcon(image_3));

        centerPanel.add(widthLabel);
        centerPanel.add(heightLabel);
        centerPanel.add(resolutionIndicator);
        centerPanel.add(horizontalSlider);
        centerPanel.add(verticalSlider);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setBounds(CENTER_PANEL_WIDTH + LEFT_PANEL_WIDTH, 0, RIGHT_PANEL_WIDTH, RIGHT_PANEL_HEIGHT);
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            image = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < originalImage.getHeight(); y++) {
                for (int x = 0; x < originalImage.getWidth(); x++) {
                    int rgb = originalImage.getRGB(x, y); // 获取原始图像的像素数据
                    image.setRGB(x, y, rgb); // 将像素数据设置到新图像中
                }
            }
            intialize();
            repaintImage();
        });

        resetButton.setBounds(16, 25, 120, CONFIRM_BUTTON_HEIGHT);
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.setBackground(Color.WHITE);
        rightPanel.add(resetButton);

        confirmButton = new JButton("Confirm");
        confirmButton.setBackground(Color.WHITE);
        confirmButton.setBounds(16, 100, 120, CONFIRM_BUTTON_HEIGHT);
        confirmButton.setFont(new Font("Arial", Font.BOLD, 14));
        confirmButton.addActionListener(e -> {
            try {
                int[] seamNum;
                image = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(),
                        BufferedImage.TYPE_INT_RGB);
                for (int y = 0; y < originalImage.getHeight(); y++) {
                    for (int x = 0; x < originalImage.getWidth(); x++) {
                        int rgb = originalImage.getRGB(x, y); // 获取原始图像的像素数据
                        image.setRGB(x, y, rgb); // 将像素数据设置到新图像中
                    }
                }
                ip = new ImagePreprocessor(image);
                ip.setLimage(Limage);

                if (textField.getV() != -1) {
                    if (textField1.getV() == -1) {
                        seamNum = new int[] { imageSize[0] - Integer.parseInt(textField.getText()),
                                imageSize[1] - (int) (zoomY * imageSize[1]) };
                    } else {
                        seamNum = new int[] { imageSize[0] - Integer.parseInt(textField.getText()),
                                imageSize[1] - Integer.parseInt(textField1.getText()) };
                    }
                } else if (textField1.getV() != -1) {
                    seamNum = new int[] { imageSize[0] - (int) (zoomX * imageSize[0]),
                            imageSize[1] - Integer.parseInt(textField1.getText()) };
                } else {
                    seamNum = new int[] { imageSize[0] - (int) (zoomX * imageSize[0]),
                            imageSize[1] - (int) (zoomY * imageSize[1]) };
                }

                BufferedImage[] images = ip.carv(seamNum);
                eImage = images[0];
                mask = images[1];
                image = images[2];
                repaintImage();

            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        });

        rightPanel.add(confirmButton);

        controlPanel.add(leftPanel);
        controlPanel.add(centerPanel);
        controlPanel.add(rightPanel);

        horizontalSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                zoomX = (horizontalSlider.getValue() - 1) / 100.0;
                repaintImage();
            }
        });
        horizontalSlider.setFont(new Font("Arial", Font.BOLD, 14));

        verticalSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                zoomY = (verticalSlider.getValue() - 1) / 100.0;
                repaintImage();
            }
        });
        verticalSlider.setFont(new Font("Arial", Font.BOLD, 14));

        textField = new RestrictedNumberField(5);
        textField.setBounds(350, 40, 75, 30);
        textField.addActionListener(e -> {
            textField.updateV();
            zoomX = (double) textField.getV() / imageSize[0];
            horizontalSlider.setValue((int) (100 * zoomX));
            repaintImage();
        });
        centerPanel.add(textField);

        textField1 = new RestrictedNumberField(5);
        textField1.setBounds(350, 100, 75, 30);
        textField1.addActionListener(e -> {
            textField1.updateV();
            zoomY = (double) textField1.getV() / imageSize[1];
            verticalSlider.setValue((int) (100 * zoomY));
            repaintImage();
        });
        centerPanel.add(textField1);

        add(imagePanel);
        add(controlPanel);
    }

    private void repaintImage() {
        if (textField.getV() != -1) {
            if (textField1.getV() == -1) {
                this.resolutionIndicator
                        .setText("          " + textField.getText() + " * " + (int) (zoomY * imageSize[1]) + " px");
            } else {
                this.resolutionIndicator
                        .setText("          " + textField.getText() + " * " + textField1.getText() + " px");
            }
        } else if (textField1.getV() != -1) {
            this.resolutionIndicator
                    .setText("          " + (int) (zoomX * imageSize[0]) + " * " + textField1.getText() + " px");
        } else {
            this.resolutionIndicator
                    .setText(
                            "          " + (int) (zoomX * imageSize[0]) + " * " + (int) (zoomY * imageSize[1]) + " px");
        }
        this.resolutionIndicator.setForeground(currentColor);
        if (image != null) {
            int width = image.getWidth();
            int height = image.getHeight();
            imagePanel.setPreferredSize(new Dimension((int) (width * zoomX), (int) (height * zoomY)));
            imagePanel.revalidate();
            imagePanel.repaint();
        }
    }

    private void intialize() {
        zoomX = 1;
        zoomY = 1;
        horizontalSlider.setValue(100);
        verticalSlider.setValue(100);
        textField.initialV();
        textField1.initialV();
        textField.setText(null);
        textField1.setText(null);
        textField.setMaxValue(image.getWidth() * 2);
        textField1.setMaxValue(image.getHeight() * 2);
        Limage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    }
}
