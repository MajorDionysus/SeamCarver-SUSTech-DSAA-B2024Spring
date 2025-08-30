import javax.swing.SwingUtilities;

public class Run {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ImageEditorGUI gui = new ImageEditorGUI();
                gui.setVisible(true);
            }
        });
    }
    
}
