import javax.swing.*;
import java.awt.*;

public class RestrictedNumberField extends JTextField {
    private final int minValue = 0;
    private int maxValue;
    private int v = -1;

    public void setMaxValue(int m){
        this.maxValue = m;
    }

    public RestrictedNumberField(int columns) {
        super(columns);
        this.maxValue = 2000;
        setOpaque(true);
        setFont(new Font("Cascadia Code", Font.BOLD, 18));
        setForeground(Color.WHITE);
        setBackground(Color.DARK_GRAY);
    }

    @Override
    public void replaceSelection(String text) {
        // 重写 replaceSelection 方法，确保替换的文本符合要求
        StringBuilder currentText = new StringBuilder(getText());
        int start = getSelectionStart();
        int end = getSelectionEnd();
        currentText.replace(start, end, text);
        if (isValidNumber(currentText.toString())) {
            super.replaceSelection(text);
        }
    }

    // 验证输入是否为合法的数字
    private boolean isValidNumber(String text) {
        try {
            int value = Integer.parseInt(text);
            return value >= minValue && value <= maxValue;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    // 更新 zoomX 的方法
    public void updateV() {
        String text = getText();
        try {
            int value = Integer.parseInt(text);
            if (value >= minValue && value <= maxValue) {
                v = value;
            } else {
                // 如果输入值不在范围内，恢复为原来的值
                setText(String.valueOf((int) v));
            }
        } catch (NumberFormatException ex) {
            // 如果输入不是一个合法的整数，则恢复为原来的值
            setText(String.valueOf((int) v));
        }
    }

    // 获取当前的 zoomX 值
    public int getV() {
        return v;
    }

    public void initialV() {
       this.v = -1;
    }
}
