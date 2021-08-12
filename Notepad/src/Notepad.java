import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;

public class Notepad extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            new Notepad();
        });
    }
    private final JTextArea editor = new JTextArea(11, 30);
    private String initialText = "";
    private String nowText;
    private final JPanel statusPanel;
    private final JCheckBoxMenuItem chckbxmntmStatusBar;
    private String fileName = "";
    private String filePath;
    private boolean isFileSaved = false;

    public Notepad() {
        setIconImage(Toolkit.getDefaultToolkit().getImage("G:\\VS programs\\Java 2021\\Swing Programs\\src\\Image\\icons8_magazine_32.png"));
        setTitle("Text Editor - Untitled");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                if (!isFileSaved) {
                    int i = JOptionPane.showConfirmDialog(Notepad.this, "Do you want to save this file?");
                    if (i == JOptionPane.YES_OPTION) {
                        saveDialogBox();
                    }
                    else if(i == JOptionPane.NO_OPTION) {
                        Notepad.this.dispose();
                    }
                }
            }
        });
        setBounds(100, 100, 800, 500);
        setVisible(true);
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorderPainted(false);
        menuBar.setForeground(SystemColor.inactiveCaptionText);
        menuBar.setBackground(new Color(248, 248, 255));
        setJMenuBar(menuBar);

        JMenu mnFile = new JMenu("File");
        mnFile.setForeground(SystemColor.desktop);
        menuBar.add(mnFile);

        JMenuItem mntmNew = new JMenuItem("New");
        mntmNew.addActionListener(event -> {
            if (!editor.getText().equals("") && !isFileSaved) {
                int m = JOptionPane.showConfirmDialog(Notepad.this, "Do you want to save this file.?", "New File",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (m == JOptionPane.YES_OPTION) {
                    isFileSaved = true;
                    saveDialogBox();
                } else if (m == JOptionPane.NO_OPTION) {
                    isFileSaved = false;
                    editor.setText("");
                    setTitle("Text Editor - Untitled");
                }
            }
        });
        mntmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        mnFile.add(mntmNew);

        JMenuItem mntmOpen = new JMenuItem("Open");
        mntmOpen.addActionListener(e -> openFile());
        mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        mnFile.add(mntmOpen);

        JMenuItem mntmSave = new JMenuItem("Save");
        mntmSave.addActionListener(e -> saveDialogBox());

        mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        mnFile.add(mntmSave);

        JMenuItem mntmSaveAs = new JMenuItem("Save As...");
        mntmSaveAs.addActionListener(e -> saveDialogBox());

        mnFile.add(mntmSaveAs);

        JSeparator separator = new JSeparator();
        mnFile.add(separator);

        JMenuItem mntmNewMenuItem = new JMenuItem("Page Setpup...");
        mnFile.add(mntmNewMenuItem);

        JMenuItem mntmPrint = new JMenuItem("Print...");
        mntmPrint.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        mnFile.add(mntmPrint);

        JSeparator separator_1 = new JSeparator();
        mnFile.add(separator_1);

        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.addActionListener(e -> dispose());

        mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        mnFile.add(mntmExit);

        JMenu mnEdit = new JMenu("Edit");
        mnEdit.setForeground(SystemColor.desktop);
        menuBar.add(mnEdit);

        JMenuItem mntmUndo = new JMenuItem("Undo");
        mntmUndo.addActionListener(new ActionListener() {
            boolean b = false;
            public void actionPerformed(ActionEvent e) {
                if (!b) {
                    editor.setText(initialText);
                    b = true;
                } else {
                    editor.setText(nowText);
                    b = false;
                }
            }
        });
        mntmUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        mnEdit.add(mntmUndo);

        JSeparator separator_2 = new JSeparator();
        mnEdit.add(separator_2);

        JMenuItem mntmCut = new JMenuItem("Cut");
        mntmCut.addActionListener(e -> {
            initialText = editor.getText();
            editor.cut();
            nowText = editor.getText();
        });
        mntmCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        mnEdit.add(mntmCut);

        JMenuItem mntmCopy = new JMenuItem("Copy");
        mntmCopy.addActionListener(e -> editor.copy());

        mntmCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        mnEdit.add(mntmCopy);

        JMenuItem mntmPaste = new JMenuItem("Paste");
        mntmPaste.addActionListener(e -> editor.paste());

        mntmPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        mnEdit.add(mntmPaste);

        JMenuItem mntmDelete = new JMenuItem("Delete");
        mntmDelete.addActionListener(e -> deleteText());

        mntmDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        mnEdit.add(mntmDelete);

        JSeparator separator_3 = new JSeparator();
        mnEdit.add(separator_3);

        JMenuItem mntmFind = new JMenuItem("Find");
        mntmFind.setEnabled(false);

        // Find menu button
        mntmFind.addActionListener(e -> {
            JDialog dialog = new JDialog(Notepad.this, "Find");
            dialog.setResizable(false);
            dialog.setVisible(true);
            dialog.setBounds(Notepad.this.getBounds().x + 60,
                    Notepad.this.getBounds().y + Notepad.this.getSize().height / 2, 370, 140);
            JLabel f = new JLabel("Find what: ");
            JTextField tf = new JTextField(15);
            JButton findBtn = new JButton("Find sext");
            JButton cancelBtn = new JButton("Cancel");
            findBtn.setBackground(Color.white);
            cancelBtn.setBackground(Color.white);

            JCheckBox c1 = new JCheckBox("Match Case");

            JRadioButton r1 = new JRadioButton("Up");
            JRadioButton r2 = new JRadioButton("Down", true);
            ButtonGroup bg = new ButtonGroup();
            bg.add(r1);
            bg.add(r2);
            JPanel direction = new JPanel();
            direction.setBorder(BorderFactory.createTitledBorder("Direction"));
            direction.add(r1);
            direction.add(r2);

            dialog.getContentPane().setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;
            dialog.getContentPane().add(f, gbc);
            gbc.gridx = 1;
            gbc.gridy = 0;
            dialog.getContentPane().add(tf, gbc);
            gbc.gridx = 2;
            gbc.gridy = 0;
            dialog.getContentPane().add(findBtn, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            dialog.getContentPane().add(c1, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            dialog.getContentPane().add(direction, gbc);
            // Add Cancel Button
            gbc.gridx = 2;
            gbc.gridy = 1;
            dialog.getContentPane().add(cancelBtn, gbc);

//            Lambda
            cancelBtn.addActionListener(event -> dialog.dispose());

            findBtn.addActionListener(event ->{
                int index = editor.getCaretPosition();
                String str = editor.getText(),
                        findStr = tf.getText();
                // Down searching
                if(!c1.isSelected()) {
                    str = str.toLowerCase();
                    findStr = findStr.toLowerCase();
                }
                if(r2.isSelected()) {
                    if(str.indexOf(findStr, index) != -1) {
                        editor.select(index, index + findStr.length());
                    }else {
                        JOptionPane.showMessageDialog(dialog," Cannot find \""+tf.getText()+"\"", "Notepad", JOptionPane.INFORMATION_MESSAGE);
                    }
                }else if(r1.isSelected()) { // up searching
                    int index1 = str.lastIndexOf(findStr, index);
                    if(index1 != -1) {
                        editor.select(index1, index1 + findStr.length());
                    }else {
                        JOptionPane.showMessageDialog(dialog," Cannot find \""+tf.getText()+"\"", "Notepad", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
            dialog.validate();
            dialog.pack();
        });
        mntmFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        mnEdit.add(mntmFind);

        JMenuItem mntmFindNext = new JMenuItem("Find Next");
        mntmFindNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        mnEdit.add(mntmFindNext);

        JMenuItem mntmReplace = new JMenuItem("Replace...");
        mntmReplace.addActionListener(e -> {
            JDialog d1 = new JDialog(Notepad.this, "Replace");
            d1.setVisible(true);
            d1.setBounds(Notepad.this.getBounds().x + 60,
                    Notepad.this.getBounds().y + Notepad.this.getSize().height / 2, 370, 140);
            JLabel f = new JLabel("Find what: ");
            JTextField tf = new JTextField(15);

            JLabel rLabel = new JLabel("Replace with: ");
            JTextField rField = new JTextField(15);

            JButton findBtn = new JButton("Find Next");
            JButton replaceBtn = new JButton("Replace");
            JButton replaceAllBtn = new JButton("Replace All");
            JButton cancelBtn = new JButton("Cancel");
            findBtn.setBackground(Color.white);
            replaceBtn.setBackground(Color.white);
            replaceAllBtn.setBackground(Color.white);
            cancelBtn.setBackground(Color.white);

            JCheckBox c1 = new JCheckBox("Match Case");

            d1.getContentPane().setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(2, 2, 2, 2);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;
            d1.getContentPane().add(f, gbc);
            gbc.gridx = 1;
            gbc.gridy = 0;
            d1.getContentPane().add(tf, gbc);
            gbc.gridx = 2;
            gbc.gridy = 0;
            d1.getContentPane().add(findBtn, gbc);
            //
            gbc.gridx = 0;
            gbc.gridy = 1;
            d1.getContentPane().add(rLabel, gbc);
            gbc.gridx = 1;
            gbc.gridy = 1;
            d1.getContentPane().add(rField, gbc);
            gbc.gridx = 2;
            gbc.gridy = 1;
            d1.getContentPane().add(replaceBtn, gbc);

            gbc.gridx = 2;
            gbc.gridy = 2;
            d1.getContentPane().add(replaceAllBtn, gbc);

            gbc.gridx = 2;
            gbc.gridy = 3;
            d1.getContentPane().add(cancelBtn, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            d1.getContentPane().add(c1, gbc);

            cancelBtn.addActionListener(event -> d1.dispose());
            d1.validate();
            d1.pack();
        });
        mntmReplace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
        mnEdit.add(mntmReplace);

        JMenuItem mntmGoTo = new JMenuItem("Go To...");
        mntmGoTo.addActionListener(e -> new GotoDialog().setVisible(true));
        mntmGoTo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
        mnEdit.add(mntmGoTo);

        JSeparator separator_4 = new JSeparator();
        mnEdit.add(separator_4);

        JMenuItem mntmSelectAll = new JMenuItem("Select All");
        mntmSelectAll.addActionListener(e -> editor.selectAll());

        mntmSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        mnEdit.add(mntmSelectAll);
        // Creating Date object
        final LocalDateTime t1 = LocalDateTime.now();
        JMenuItem mntmTimeDate = new JMenuItem("Time/ Date");
        mntmTimeDate.addActionListener(e -> {
            String t = editor.getSelectedText();
            StringBuilder sb = new StringBuilder(editor.getText());
            if (t != null) {
                sb.delete(sb.indexOf(t), (sb.indexOf(t) + t.length()));
            }
            editor.setText(sb + " " + t1.toString().substring(11, 16) + " " + t1.toString().substring(0, 10));
        });

        mntmTimeDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        mnEdit.add(mntmTimeDate);

        JMenu mnFormat = new JMenu("Format");
        mnFormat.setForeground(SystemColor.desktop);
        menuBar.add(mnFormat);

        JCheckBoxMenuItem wordWrapMenuItem = new JCheckBoxMenuItem("Word Wrap");
        wordWrapMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(wordWrapMenuItem.isSelected()) {
                    chckbxmntmStatusBar.setEnabled(false);
                    statusPanel.setVisible(false);
                    editor.setLineWrap(true);
                }else {
                    editor.setLineWrap(false);
                    statusPanel.setVisible(true);
                    chckbxmntmStatusBar.setEnabled(true);
                }
            }
        });
        mnFormat.add(wordWrapMenuItem);

        JMenuItem mntmFont = new JMenuItem("Font...");
        mntmFont.addActionListener((e)-> {
                FontDialog dialog = new FontDialog(editor);
                dialog.setVisible(true);
        });
        mnFormat.add(mntmFont);

        JMenu mnView = new JMenu("View");
        mnView.setForeground(SystemColor.desktop);
        menuBar.add(mnView);

        chckbxmntmStatusBar = new JCheckBoxMenuItem("Status Bar");
        chckbxmntmStatusBar.setSize(new Dimension(0, 20));
        chckbxmntmStatusBar.setSelected(true);
        chckbxmntmStatusBar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusPanel.setVisible(chckbxmntmStatusBar.isSelected());
            }
        });
        mnView.add(chckbxmntmStatusBar);

        JMenu mnHelp = new JMenu("Help");
        mnHelp.setForeground(SystemColor.desktop);
        menuBar.add(mnHelp);

        JMenuItem mntmAboutDeveloper = new JMenuItem("About Developer");
        mntmAboutDeveloper.addActionListener(event -> {
                AboutDeveloper dialog = new AboutDeveloper();
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setVisible(true);
        });
        mnHelp.add(mntmAboutDeveloper);

        statusPanel = new JPanel();
        statusPanel.setPreferredSize(new Dimension(10, 25));
        getContentPane().add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setLayout(new FlowLayout(FlowLayout.TRAILING, 0, 0));

        JPanel statusLeftPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) statusLeftPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        statusLeftPanel.setBorder(new MatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
        statusLeftPanel.setPreferredSize(new Dimension(250, 25));
        statusPanel.add(statusLeftPanel);

        JSeparator separator_5 = new JSeparator();
        separator_5.setBackground(Color.GRAY);
        statusLeftPanel.add(separator_5);
        separator_5.setPreferredSize(new Dimension(0, 18));
        separator_5.setOrientation(SwingConstants.VERTICAL);

        JLabel lblLnCols = new JLabel("Ln : 1, Col  : 1");
        statusLeftPanel.add(lblLnCols);
        lblLnCols.setHorizontalAlignment(SwingConstants.RIGHT);
        lblLnCols.setFont(new Font("Tahoma", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(new LineBorder(SystemColor.textHighlight));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        getContentPane().add(scrollPane, BorderLayout.CENTER);
        editor.setBorder(new EmptyBorder(0, 8, 0, 0));
        editor.addCaretListener(e -> {
            // Mouse click on editor to change the lines and columns number
            Point point = editor.getCaret().getMagicCaretPosition();
            if(point != null) {
                int cols =(int) point.getX() / 10 + 1;
                int lines =((int) point.getY() / 22) + 1;
                lblLnCols.setText("Ln : "+lines+", Col : "+cols);
            }
        });

        editor.setFont(new Font("Consoles", Font.BOLD | Font.ITALIC, 18));
        editor.setAutoscrolls(true);
        editor.setSelectedTextColor(Color.white);
        editor.setSelectionColor(new Color(30, 144, 255));
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                Point point = editor.getCaret().getMagicCaretPosition();
                if(point != null) {
                    int cols =(int) point.getX() / 10 + 1;
                    int lines =((int) point.getY() / 22)+ 1;
                    lblLnCols.setText("Ln : "+lines+", Col : "+cols);
                }
            }
            @Override
            public void keyPressed(KeyEvent e) {
                mntmFind.setEnabled(editor.getText() != null);
                isFileSaved = false;
            }
        });
        editor.setFocusable(true);
        scrollPane.setViewportView(editor);

        JPopupMenu popupMenu = new JPopupMenu();
        addPopup(editor, popupMenu);

        JMenuItem popupUndu = new JMenuItem("Undu");
        popupUndu.addActionListener(new ActionListener() {
            boolean b = false;
            public void actionPerformed(ActionEvent e) {
                if (!b) {
                    editor.setText(initialText);
                    b = true;
                } else {
                    editor.setText(nowText);
                    b = false;
                }
            }
        });
        popupMenu.add(popupUndu);

        JSeparator separator_6 = new JSeparator();
        popupMenu.add(separator_6);

        JMenuItem popupCut = new JMenuItem("Cut");
        popupCut.addActionListener(e -> {
            initialText = editor.getText();
            editor.cut();
            nowText = editor.getText();
        });
        popupMenu.add(popupCut);

        JMenuItem popupCopy = new JMenuItem("Copy");
        popupCopy.addActionListener(e -> editor.copy());
        popupMenu.add(popupCopy);

        JMenuItem popupPaste = new JMenuItem("Paste");
        popupPaste.addActionListener(e -> editor.paste());
        popupMenu.add(popupPaste);

        JMenuItem popupDelete = new JMenuItem("Delete");
        popupDelete.addActionListener(e -> deleteText());
        popupMenu.add(popupDelete);

        JSeparator separator_7 = new JSeparator();
        popupMenu.add(separator_7);

        JMenuItem popupSelectAll = new JMenuItem("Select All");
        popupSelectAll.addActionListener(e -> {
            editor.selectAll();
            mntmCut.setEnabled(true);
        });
        popupMenu.add(popupSelectAll);

        JSeparator separator_8 = new JSeparator();
        popupMenu.add(separator_8);

        JMenuItem mntmNewMenuItem_2 = new JMenuItem("Right to Left reading Order");
        popupMenu.add(mntmNewMenuItem_2);

        JMenuItem mntmShowUnicodeCharacterd = new JMenuItem("Show Unicode control characters");
        popupMenu.add(mntmShowUnicodeCharacterd);

        JMenu mnNewMenu = new JMenu("Insert Unicode control character");
        popupMenu.add(mnNewMenu);

        JMenuItem mntmLtrLeftTo = new JMenuItem("LRM             left_-to-right-mark");
        mnNewMenu.add(mntmLtrLeftTo);

        JMenuItem mntmRlmRighttoleftmark = new JMenuItem("RLM             Right-to-left-mark");
        mnNewMenu.add(mntmRlmRighttoleftmark);

        JMenuItem mntmZwjZeroWith = new JMenuItem("ZWJ             Zero width joiner-mark");
        mnNewMenu.add(mntmZwjZeroWith);

        JMenuItem mntmZwnjZeroWidth = new JMenuItem("ZWNJ          Zero width non-joiner-mark");
        mnNewMenu.add(mntmZwnjZeroWidth);

        JMenuItem mntmLreStartLeft = new JMenuItem("LRE              Start of left-to-right  embedding");
        mnNewMenu.add(mntmLreStartLeft);

        JMenuItem mntmRleStartOf = new JMenuItem("RLE              Start of Left-to-right  embedding");
        mnNewMenu.add(mntmRleStartOf);

        JSeparator separator_9 = new JSeparator();
        popupMenu.add(separator_9);

        JMenuItem mntmNewMenuItem_3 = new JMenuItem("Close IME");
        popupMenu.add(mntmNewMenuItem_3);

        validate();
    }

    private void openFile() {
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("text files", "java", "txt", "c", "cpp");
        fc.setFileFilter(filter);
        int i = fc.showOpenDialog(Notepad.this);
        if (i == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            filePath = f.getPath();
            isFileSaved = true;
            try {
                BufferedReader br = new BufferedReader(new FileReader(filePath));
                String s1;
                StringBuilder s2 = new StringBuilder();
                while ((s1 = br.readLine()) != null)
                    s2.append(s1).append("\n");
                editor.setText(s2.toString());
                br.close();
            } catch (Exception er) {
                er.printStackTrace();
            }
        fileName = filePath.substring(filePath.lastIndexOf('\\') + 1);
        setTitle("Text Editor - " + fileName);
        }
    }

    private void saveDialogBox() {
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("text files", "java", "txt", "c", "cpp");
        fc.setFileFilter(filter);
        int i = fc.showSaveDialog(Notepad.this);
        if (i == JFileChooser.APPROVE_OPTION) {
            isFileSaved = true;
            File f = fc.getSelectedFile();
            filePath = f.getPath();
            filePath += ".txt";
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));
                String s2;
                s2 = editor.getText();
                bw.write(s2);
                bw.close();
            } catch (Exception er) {
                er.printStackTrace();
            }
        }
        if(filePath != null) {
            fileName = filePath.substring(filePath.lastIndexOf('\\') + 1);
            setTitle("Text Editor - " + fileName);
        }
    }
    // Pop up menu
    private static void addPopup(Component component, final JPopupMenu popup) {
        component.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showMenu(e);
                }
            }
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showMenu(e);
                }
            }
            private void showMenu(MouseEvent e) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });
    }
    private void deleteText() {
        StringBuilder sb = new StringBuilder(editor.getText());
        String sText = editor.getSelectedText();
        sb.delete(sb.indexOf(sText), sb.indexOf(sText) + sText.length());
        editor.setText(sb + "");
    }
}
