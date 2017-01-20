package SplashScreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

@SuppressWarnings("serial")
public class SplashScreen extends JWindow {

	public boolean visible = true;

    public SplashScreen() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException |
                    InstantiationException |
                    IllegalAccessException |
                    UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
                showSplash();
            }
        });
    }

    public void showSplash() {
    	JPanel content = (JPanel) getContentPane();
    	content.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));

        setSize(450, 250);
    	setLocationRelativeTo(null);

        JLabel label = new JLabel(new ImageIcon(getClass().getResource("splash.png")));
        content.add(label, BorderLayout.CENTER);
        label.setLayout(new GridBagLayout());

        setVisible(true);
        toFront();

        new ResourceLoader().execute();
    }

    public class ResourceLoader extends SwingWorker<Object, Object> {

        @Override
        protected Object doInBackground() throws Exception {
            try {
                Thread.sleep(1500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void done() {
        	visible = false;
            setVisible(false);
        }
    }
}