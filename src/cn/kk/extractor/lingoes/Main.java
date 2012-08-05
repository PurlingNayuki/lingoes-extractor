package cn.kk.extractor.lingoes;

/*  Copyright (c) 2012 Xiaoyun Zhu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy  
 *  of this software and associated documentation files (the "Software"), to deal  
 *  in the Software without restriction, including without limitation the rights  
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell  
 *  copies of the Software, and to permit persons to whom the Software is  
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in  
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN  
 *  THE SOFTWARE.  
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

public class Main extends JFrame {
    private final static String CFG_FILE = System.getProperty("user.home") + File.separator + "lingoes-extractor.cfg";
    private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private final LingoesLd2Extractor extractor;

    private static class ValidationDocumentListener implements DocumentListener {

        JTextField tf;
        boolean output;

        private ValidationDocumentListener(JTextField tf, boolean output) {
            this.tf = tf;
            this.output = output;
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            Main.check(this.tf, this.output);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            Main.check(this.tf, this.output);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            Main.check(this.tf, this.output);
        }
    }

    public final static boolean check(JTextField tf, boolean output) {
        String v = tf.getText();
        boolean valid = false;
        File file = new File(v);
        if (!output) {
            valid = file.isFile();
        } else {
            valid = !file.isDirectory() && (!file.exists() || file.canWrite());
        }
        if (valid) {
            tf.setBackground(Color.GREEN);
            return true;
        } else {
            tf.setBackground(Color.RED);
            return false;
        }
    }

    /**
     * Creates new form Tester
     * 
     * @throws IOException
     */
    public Main() throws IOException {
        setIconImage(ImageIO.read(getClass().getResource("/ld2.png")));
        setTitle("Lingoes Dictionary Extractor");
        initComponents();
        setLocation((Main.SCREEN_SIZE.width - getWidth()) / 2, (Main.SCREEN_SIZE.height - getHeight()) / 2);
        this.extractor = new LingoesLd2Extractor(this);

        this.tfInput.getDocument().addDocumentListener(new ValidationDocumentListener(this.tfInput, false));
        this.tfOutput.getDocument().addDocumentListener(new ValidationDocumentListener(this.tfOutput, true));

        try {
            final Properties props = new Properties();
            props.load(new FileReader(Main.CFG_FILE));

            String val = props.getProperty("input");
            if (Helper.isNotEmptyOrNull(val)) {
                this.tfInput.setText(val);
            }
            val = props.getProperty("output");
            if (Helper.isNotEmptyOrNull(val)) {
                this.tfOutput.setText(val);
            }

            System.out.println("成功读取设置文件'" + Main.CFG_FILE + "'。");
        } catch (final Throwable t) {
            // ignore
        }

        reset();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        this.pnlExample = new JPanel();
        this.lblExample = new JLabel();
        this.lblStatus = new JLabel();
        this.pnlInOut = new JPanel();
        this.lblInput = new JLabel();
        this.tfInput = new JTextField();
        this.lblOutput = new JLabel();
        this.tfOutput = new JTextField();
        this.btnStart = new JButton();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.pnlExample.setBorder(BorderFactory.createTitledBorder(null, "Status", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(51, 51, 51))); // NOI18N

        this.lblExample.setBackground(new java.awt.Color(255, 255, 235));
        this.lblExample.setHorizontalAlignment(SwingConstants.CENTER);
        this.lblExample.setText("#0: ?");
        this.lblExample.setOpaque(true);

        this.lblStatus.setBackground(new java.awt.Color(255, 255, 235));
        this.lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        this.lblStatus.setText("lines: 0/?, speed: ?/sec");
        this.lblStatus.setOpaque(true);

        GroupLayout pnlExampleLayout = new GroupLayout(this.pnlExample);
        this.pnlExample.setLayout(pnlExampleLayout);
        pnlExampleLayout.setHorizontalGroup(pnlExampleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                        pnlExampleLayout
                                .createSequentialGroup()
                                .addContainerGap()
                                .addGroup(
                                        pnlExampleLayout
                                                .createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(this.lblStatus, GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(this.lblExample, GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addContainerGap()));
        pnlExampleLayout.setVerticalGroup(pnlExampleLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                GroupLayout.Alignment.TRAILING,
                pnlExampleLayout
                        .createSequentialGroup()
                        .addComponent(this.lblStatus, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(this.lblExample).addContainerGap()));

        this.pnlInOut.setBorder(BorderFactory.createEtchedBorder());

        this.lblInput.setLabelFor(this.tfInput);
        this.lblInput.setText("Dictionary:");

        this.tfInput.setText("D:\\test.ld2");
        this.tfInput.setToolTipText("Input Dictionary File Location");
        this.tfInput.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tfInputMouseClicked(evt);
            }
        });

        this.lblOutput.setLabelFor(this.tfOutput);
        this.lblOutput.setText("Export File:");

        this.tfOutput.setText("D:\\output.txt");
        this.tfOutput.setToolTipText("Export File Location");
        this.tfOutput.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tfOutputMouseClicked(evt);
            }
        });

        GroupLayout pnlInOutLayout = new GroupLayout(this.pnlInOut);
        this.pnlInOut.setLayout(pnlInOutLayout);
        pnlInOutLayout.setHorizontalGroup(pnlInOutLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                pnlInOutLayout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                                pnlInOutLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(this.lblInput).addComponent(this.lblOutput))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(
                                pnlInOutLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(this.tfOutput, GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                                        .addComponent(this.tfInput)).addContainerGap()));
        pnlInOutLayout.setVerticalGroup(pnlInOutLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                pnlInOutLayout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                                pnlInOutLayout
                                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(this.lblInput)
                                        .addComponent(this.tfInput, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(
                                pnlInOutLayout
                                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(this.tfOutput, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(this.lblOutput))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        this.btnStart.setText("Start Export");
        this.btnStart.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(this.pnlInOut, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(this.pnlExample, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(this.btnStart, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup()
                        .addComponent(this.pnlInOut, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(this.pnlExample, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(this.btnStart)));

        this.btnStart.getAccessibleContext().setAccessibleName("btnStart");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void reset() {
        this.btnStart.setText("Start Export");
        this.tfInput.setEnabled(true);
        this.tfOutput.setEnabled(true);
        Main.check(this.tfInput, false);
        Main.check(this.tfOutput, true);
        pack();
    }

    private void tfInputMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_tfInputMouseClicked
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory()
                        || (f.isFile() && (f.getName().toLowerCase().endsWith(".ld2") || f.getName().toLowerCase()
                                .endsWith(".ldf")));
            }

            @Override
            public String getDescription() {
                return "Lingoes Dictionary (*.ld2, *.ldf)";
            }
        });
        int rVal = fc.showOpenDialog(this);
        if (rVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fc.getSelectedFile();
            if (selectedFile != null) {
                this.tfInput.setText(selectedFile.getAbsolutePath());
            }
            Main.check(this.tfInput, false);
        }
    }// GEN-LAST:event_tfInputMouseClicked

    private void tfOutputMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_tfOutputMouseClicked
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.isDirectory() || (f.isFile() && f.getName().toLowerCase().endsWith(".txt"));
            }

            @Override
            public String getDescription() {
                return "Export File (*.txt)";
            }
        });
        int rVal = fc.showSaveDialog(this);
        if (rVal == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".txt")) {
                f = new File(f.getAbsolutePath() + ".txt");
            }
            this.tfOutput.setText(f.getAbsolutePath());
            final File d = f.getParentFile();
            d.mkdirs();
            if (f.exists()) {
                JOptionPane.showMessageDialog(this,
                        "Output File already exists. It will be overwritten during export!", "Output File",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                try {
                    OutputStream out = new FileOutputStream(f);
                    out.write('\n');
                    out.close();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Output File cannot be created! " + e, "Output File",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            Main.check(this.tfOutput, true);
        }
    }// GEN-LAST:event_tfOutputMouseClicked

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnStartActionPerformed
        this.btnStart.setEnabled(false);
        try {
            if ("Cancel Export".equals(this.btnStart.getText())) {
                this.extractor.cancel();
            } else {
                this.tfInput.setEnabled(false);
                this.tfOutput.setEnabled(false);
                boolean valid = true;
                valid &= Main.check(this.tfInput, false);
                valid &= Main.check(this.tfOutput, true);
                if (valid) {
                    new Thread("LingoesLd2Extractor") {
                        @Override
                        public void run() {
                            try {
                                Main.this.extractor.extractLd2ToFile(new File(Main.this.tfInput.getText()), new File(
                                        Main.this.tfOutput.getText()));

                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(Main.this, "Extraction failed! " + e.toString(),
                                        "Input/Output File Error", JOptionPane.ERROR_MESSAGE);
                            } finally {
                                reset();
                            }
                        }
                    }.start();
                    this.btnStart.setText("Cancel Export");
                    pack();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid input or output file!", "Input/Output File Error",
                            JOptionPane.ERROR_MESSAGE);
                    reset();
                }

            }
        } finally {
            this.btnStart.setEnabled(true);
        }

    }// GEN-LAST:event_btnStartActionPerformed

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame.setDefaultLookAndFeelDecorated(true);
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        // </editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame frm;
                try {
                    frm = new Main();
                    frm.setResizable(false);
                    frm.setVisible(true);
                } catch (IOException e) {
                    System.err.println("Program error! Please reinstall the application!");
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnStart;
    private JLabel lblExample;
    private JLabel lblInput;
    private JLabel lblOutput;
    private JLabel lblStatus;
    private JPanel pnlExample;
    private JPanel pnlInOut;
    private JTextField tfInput;
    private JTextField tfOutput;

    // End of variables declaration//GEN-END:variables

    public void save() {
        final Properties props = new Properties();
        props.put("input", this.tfInput.getText());
        props.put("output", this.tfOutput.getText());
        try {
            final FileWriter writer = new FileWriter(Main.CFG_FILE, false);
            props.store(writer, null);
            writer.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private long lastRun = -1;

    public void setStatus(long total, long finished, int numPerSecond) {
        if ((System.currentTimeMillis() - this.lastRun) > 1000) {
            setStatusDirect(total, finished, numPerSecond);
        }
    }

    public void setStatusDirect(final long total, final long finished, final int numPerSecond) {
        this.lastRun = System.currentTimeMillis();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Main.this.lblStatus.setText("lines: " + finished + "/" + total + ", speed: " + numPerSecond + "/sec");
            }
        });
    }

    private long lastRunExample = -1;

    public void setExample(long num, String example) {
        if ((System.currentTimeMillis() - this.lastRunExample) > 1000) {
            setExampleDirect(num, example);
        }
    }

    private void setExampleDirect(final long num, final String example) {
        this.lastRunExample = System.currentTimeMillis();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Main.this.lblExample.setText("#" + num + ": " + example);
            }
        });
    }

}
