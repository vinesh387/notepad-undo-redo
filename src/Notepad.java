import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import javax.swing.filechooser.*;
import javax.swing.text.*;
import java.util.*;
 

public class Notepad extends JFrame implements ActionListener {

    private JTextArea area;
    private JScrollPane scpane;
    private String currentFileName = null;
    private boolean isUndoRedoOperation = false;
    private boolean isModified = false;
    private javax.swing.Timer typingTimer;
    private Stack stack = new Stack();

    // Undo mode: true => Word mode, false => Character mode
    private boolean wordUndoMode = false; // default = Character mode

    // Highlighter for Find
    private Highlighter.HighlightPainter myHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);

    // UI menu items for toggling modes
    private JRadioButtonMenuItem charModeItem;
    private JRadioButtonMenuItem wordModeItem;

    public Notepad() {
        super("Notepad");

        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        // File Menu
        JMenu file = new JMenu("File");
        file.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JMenuItem NewPad = createMenuItem("New", KeyEvent.VK_N, "icons/new.png");
        JMenuItem openPad = createMenuItem("Open", KeyEvent.VK_O, "icons/open.png");
        JMenuItem savePad = createMenuItem("Save", KeyEvent.VK_S, "icons/save.png");
        JMenuItem saveAsPad = createMenuItem("Save As", KeyEvent.VK_S, KeyEvent.SHIFT_DOWN_MASK, "icons/saveas.png");
        JMenuItem exitPad = createMenuItem("Exit", KeyEvent.VK_ESCAPE, 0, "icons/exit.png");
        file.add(NewPad); file.add(openPad); file.addSeparator();
        file.add(savePad); file.add(saveAsPad); file.addSeparator();
        file.add(exitPad);

        // Edit Menu
        JMenu editPad = new JMenu("Edit");
        editPad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JMenuItem undoText = createMenuItem("Undo", KeyEvent.VK_Z, "icons/undo.png");
        JMenuItem redoText = createMenuItem("Redo", KeyEvent.VK_Y, "icons/redo.png");
        JMenuItem cutText = createMenuItem("Cut", KeyEvent.VK_X, "icons/cut.png");
        JMenuItem copyText = createMenuItem("Copy", KeyEvent.VK_C, "icons/copy.png");
        JMenuItem pasteText = createMenuItem("Paste", KeyEvent.VK_V, "icons/paste.png");
        JMenuItem selectAllText = createMenuItem("Select All", KeyEvent.VK_A, "icons/selectall.png");
        JMenuItem find = createMenuItem("Find", KeyEvent.VK_F, "icons/find.png");
        JMenuItem replace = createMenuItem("Find and Replace", KeyEvent.VK_H, "icons/replace.png");
        editPad.add(undoText); editPad.add(redoText); editPad.addSeparator();
        editPad.add(cutText); editPad.add(copyText); editPad.add(pasteText); editPad.addSeparator();
        editPad.add(selectAllText); editPad.addSeparator(); editPad.add(find); editPad.add(replace);

        // Undo Mode submenu (Option B)
        JMenu undoModeMenu = new JMenu("Undo Mode");
        undoModeMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ButtonGroup bg = new ButtonGroup();
        charModeItem = new JRadioButtonMenuItem("Character");
        wordModeItem = new JRadioButtonMenuItem("Word");

        charModeItem.setSelected(true); // default
        charModeItem.addActionListener(e -> setUndoMode(false));
        wordModeItem.addActionListener(e -> setUndoMode(true));

        bg.add(charModeItem);
        bg.add(wordModeItem);
        undoModeMenu.add(charModeItem);
        undoModeMenu.add(wordModeItem);

        editPad.addSeparator();
        editPad.add(undoModeMenu);

        // Tools Menu
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JMenuItem wordFreq = createMenuItem("Word Frequency Analysis", -1, "icons/analysis.png");
        toolsMenu.add(wordFreq);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JMenuItem aboutPad = createMenuItem("About Notepad", -1, "icons/about.png");
        helpMenu.add(aboutPad);

        menuBar.add(file); menuBar.add(editPad); menuBar.add(toolsMenu); menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // Text Area
        area = new JTextArea();
        area.setFont(new Font("Consolas", Font.PLAIN, 16));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(Color.WHITE);
        area.setCaretColor(Color.BLACK);
        area.setMargin(new Insets(10,10,10,10));

        scpane = new JScrollPane(area);
        scpane.setBorder(BorderFactory.createEmptyBorder());
        scpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scpane, BorderLayout.CENTER);

        // Initialize stack with empty state
        stack.push("");

        // Timer used for Word mode batching: after user stops typing for 500ms, push snapshot
        typingTimer = new javax.swing.Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isUndoRedoOperation && wordUndoMode) {
                    stack.push(area.getText());
                }
            }
        });
        typingTimer.setRepeats(false);

        // Document Listener
        area.getDocument().addDocumentListener(new DocumentListener() {
            private void handleInsert(DocumentEvent e) {
                if (isUndoRedoOperation) return;

                try {
                    int offset = e.getOffset();
                    int len = e.getLength();
                    String inserted = area.getText(offset, len);

                    if (!wordUndoMode) {
                        // Character mode
                        // If single-char insert -> push immediately
                        if (len == 1) {
                            stack.push(area.getText());
                        } else {
                            // multi-char (paste) -> push once
                            stack.push(area.getText());
                        }
                    } else {
                        // Word mode
                        // If inserted char contains whitespace or punctuation, push immediately.
                        if (containsWordBoundary(inserted)) {
                            stack.push(area.getText());
                            typingTimer.stop();
                        } else {
                            // restart idle timer; when user pauses, we push the current snapshot
                            if (typingTimer.isRunning()) typingTimer.restart();
                            else typingTimer.start();
                        }
                    }

                    if (!isUndoRedoOperation) {
                        isModified = true;
                        updateTitle();
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }

            private void handleRemove(DocumentEvent e) {
                if (isUndoRedoOperation) return;

                int len = e.getLength();
                if (!wordUndoMode) {
                    // Character mode: push when a single char is removed (backspace/delete)
                    if (len == 1) {
                        stack.push(area.getText());
                    } else {
                        // multiple characters removed -> push once
                        stack.push(area.getText());
                    }
                } else {
                    // Word mode: if the removal likely removed a space or punctuation, treat as word boundary
                    // For simplicity push on any removal and also restart idle timer
                    stack.push(area.getText());
                    if (typingTimer.isRunning()) typingTimer.restart();
                    else typingTimer.start();
                }

                if (!isUndoRedoOperation) {
                    isModified = true;
                    updateTitle();
                }
            }

            public void insertUpdate(DocumentEvent e) { handleInsert(e); }
            public void removeUpdate(DocumentEvent e) { handleRemove(e); }
            public void changedUpdate(DocumentEvent e) { /* not used for plain text */ }
        });

        // Window Closing
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { handleExit(); }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }

    // helper to detect spaces/newlines/punct as word boundaries
    private boolean containsWordBoundary(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(c)) return true;
            if (".,;:!?()[]{}".indexOf(c) >= 0) return true;
        }
        return false;
    }

    // --- Menu Item Helper ---
    private JMenuItem createMenuItem(String text, int keyCode, String iconPath) {
        return createMenuItem(text, keyCode, ActionEvent.CTRL_MASK, iconPath);
    }

    private JMenuItem createMenuItem(String text, int keyCode, int modifiers, String iconPath) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (keyCode != -1 && keyCode != KeyEvent.VK_ESCAPE) item.setAccelerator(KeyStroke.getKeyStroke(keyCode, modifiers));
        item.addActionListener(this);
        item.setBackground(Color.WHITE);
        return item;
    }

    // Overload for items without icons/accelerators
    private JMenuItem createMenuItem(String text, int keyCode, int modifiers) {
        return createMenuItem(text, keyCode, modifiers, null);
    }

    // Set undo mode (Character/Word)
    private void setUndoMode(boolean wordMode) {
        this.wordUndoMode = wordMode;
        // When switching modes, it's a good time to create a new baseline snapshot
        stack.push(area.getText());
    }

    // --- Action Events ---
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        try {
            switch (command) {
                case "New": if (checkUnsavedChanges()) newFile(); break;
                case "Open": if (checkUnsavedChanges()) openFile(); break;
                case "Save": saveFile(); break;
                case "Save As": saveAsFile(); break;
                case "Exit": handleExit(); break;
                case "Cut": area.cut(); stack.push(area.getText()); break;
                case "Copy": area.copy(); break;
                case "Paste": area.paste(); stack.push(area.getText()); break;
                case "Select All": area.selectAll(); break;
                case "Undo": performUndo(); break;
                case "Redo": performRedo(); break;
                case "Find": openFindDialog(); break;
                case "Find and Replace": openReplaceDialog(); break;
                case "Word Frequency Analysis": showWordFrequency(); break;
                case "About Notepad": new About().setVisible(true); break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- File Operations ---
    private void newFile() {
        area.setText("");
        stack.clear();
        stack.push("");
        currentFileName = null;
        isModified = false;
        updateTitle();
    }

    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Open File");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");
        chooser.setFileFilter(filter);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            currentFileName = file.getAbsolutePath();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                area.read(reader, null);
                stack.clear();
                stack.push(area.getText());
                isModified = false;
                updateTitle();
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    private boolean saveFile() {
        if (currentFileName != null) return saveToFile(new File(currentFileName));
        else return saveAsFile();
    }

    private boolean saveAsFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save As");
        if (currentFileName != null) chooser.setSelectedFile(new File(currentFileName));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().contains(".")) file = new File(file.getAbsolutePath() + ".txt");
            if (file.exists() && JOptionPane.showConfirmDialog(this, "Replace existing file?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
                return false;
            if (saveToFile(file)) { currentFileName = file.getAbsolutePath(); return true; }
        }
        return false;
    }

    private boolean saveToFile(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            area.write(writer);
            isModified = false;
            updateTitle();
            JOptionPane.showMessageDialog(this, "File saved successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving file!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // --- Undo/Redo ---
    private void performUndo() {
        if (stack.canUndo()) {
            isUndoRedoOperation = true;
            String text = stack.pop();
            if (text != null) area.setText(text);
            isUndoRedoOperation = false;
        }
    }

    private void performRedo() {
        if (stack.canRedo()) {
            isUndoRedoOperation = true;
            String text = stack.redoPop();
            if (text != null) area.setText(text);
            isUndoRedoOperation = false;
        }
    }

    // --- Utilities ---
    private void updateTitle() {
        String title = "Notepad - " + (currentFileName != null ? new File(currentFileName).getName() : "Untitled");
        if (isModified) title += " *";
        setTitle(title + (wordUndoMode ? " [Word Mode]" : " [Character Mode]"));
    }

    private boolean checkUnsavedChanges() {
        if (!isModified) return true;
        int option = JOptionPane.showConfirmDialog(this, "Save changes?", "Notepad", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (option == JOptionPane.YES_OPTION) return saveFile();
        else return option != JOptionPane.CANCEL_OPTION;
    }

    private void handleExit() {
        if (checkUnsavedChanges()) dispose();
    }

    // --- Find & Highlight ---
    private void highlight(JTextComponent textcomp, String pattern) {
        try {
            Highlighter hilite = textcomp.getHighlighter();
            hilite.removeAllHighlights();
            String text = textcomp.getText();
            int pos = 0;
            while ((pos = text.toUpperCase().indexOf(pattern.toUpperCase(), pos)) >= 0) {
                hilite.addHighlight(pos, pos + pattern.length(), myHighlightPainter);
                pos += pattern.length();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void openFindDialog() {
        JDialog findDialog = new JDialog(this, "Find", false);
        findDialog.setSize(450, 160); findDialog.setLayout(null); findDialog.setLocationRelativeTo(this); findDialog.setResizable(false);

        JLabel findLabel = new JLabel("Find what:"); findLabel.setBounds(20,25,80,25);
        JTextField textField = new JTextField(); textField.setBounds(100,25,220,28);
        JLabel resultLabel = new JLabel(""); resultLabel.setBounds(100,60,220,25);

        JButton findBtn = new JButton("Find Next"); findBtn.setBounds(335,25,95,28);
        findBtn.setBackground(new Color(70,130,180)); findBtn.setForeground(Color.WHITE);
        findBtn.setFocusPainted(false);

        findBtn.addActionListener(e -> {
            String searchText = textField.getText();
            if (searchText.isEmpty()) { resultLabel.setText("Enter text"); resultLabel.setForeground(Color.RED); return; }
            String content = area.getText();
            if (content.contains(searchText)) { highlight(area, searchText); resultLabel.setText("Found"); resultLabel.setForeground(new Color(46,204,113)); }
            else { resultLabel.setText("Not found"); resultLabel.setForeground(Color.RED); area.getHighlighter().removeAllHighlights(); }
        });

        findDialog.add(findLabel); findDialog.add(textField); findDialog.add(resultLabel); findDialog.add(findBtn);
        findDialog.setVisible(true);
    }

    private void openReplaceDialog() {
        JDialog replaceDialog = new JDialog(this,"Find and Replace",false);
        replaceDialog.setSize(450,200); replaceDialog.setLayout(null); replaceDialog.setLocationRelativeTo(this); replaceDialog.setResizable(false);

        JLabel findLabel = new JLabel("Find what:"); findLabel.setBounds(20,25,80,25);
        JTextField findText = new JTextField(); findText.setBounds(110,25,210,28);
        JLabel replaceLabel = new JLabel("Replace with:"); replaceLabel.setBounds(20,65,90,25);
        JTextField replaceText = new JTextField(); replaceText.setBounds(110,65,210,28);
        JLabel resultLabel = new JLabel(); resultLabel.setBounds(110,100,210,25);

        JButton replaceBtn = new JButton("Replace All"); replaceBtn.setBounds(335,25,95,68);
        replaceBtn.setBackground(new Color(70,130,180)); replaceBtn.setForeground(Color.WHITE); replaceBtn.setFocusPainted(false);

        replaceBtn.addActionListener(e -> {
            String findStr = findText.getText();
            String replaceStr = replaceText.getText();
            if (findStr.isEmpty()) { resultLabel.setText("Enter text"); resultLabel.setForeground(Color.RED); return; }
            String content = area.getText();
            if (content.contains(findStr)) {
                String newContent = content.replace(findStr, replaceStr); area.setText(newContent); stack.push(newContent);
                resultLabel.setText("Replaced"); resultLabel.setForeground(new Color(46,204,113));
            } else { resultLabel.setText("Not found"); resultLabel.setForeground(Color.RED); }
        });

        replaceDialog.add(findLabel); replaceDialog.add(findText); replaceDialog.add(replaceLabel); replaceDialog.add(replaceText);
        replaceDialog.add(resultLabel); replaceDialog.add(replaceBtn); replaceDialog.setVisible(true);
    }

    // --- Word Frequency Analysis ---
    // NOTE: as requested, only show: total words and unique words.
    private void showWordFrequency() {
        String text = area.getText();
        if (text.trim().isEmpty()) { JOptionPane.showMessageDialog(this,"No text to analyze","Word Frequency",JOptionPane.INFORMATION_MESSAGE); return; }

        String[] words = text.toLowerCase().split("\\s+");
        int total = 0;
        HashSet<String> unique = new HashSet<>();
        for (String w : words) {
            String clean = w.replaceAll("[^a-z0-9]", "");
            if (!clean.isEmpty()) {
                total++;
                unique.add(clean);
            }
        }

        String msg = "Total words: " + total + "\nUnique words: " + unique.size();
        JOptionPane.showMessageDialog(this, msg, "Word Frequency ", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- Main ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Notepad::new);
    }
}