import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * About Dialog class for Notepad application.
 * Displays application information, features, and credits.
 */
public class About extends JFrame implements ActionListener {

    private JButton okButton;

    public About() {
        super("About Notepad");

        // Window properties
        setSize(550, 500);
        setResizable(false);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(550, 100));
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 30));

        JLabel titleLabel = new JLabel("Notepad Application");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // --- Center Panel (Scrollable) ---
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Version
        addInfoSection(contentPanel, "Version", "1.0");
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Data Structures section
        JLabel dsTitle = new JLabel("Data Structures Used:");
        dsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(dsTitle);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        String[] dataStructures = {
                "• Doubly-Linked List (Undo/Redo Stack)",
                "• HashMap (Word Analysis)"
        };

        for (String ds : dataStructures) {
            JLabel dsLabel = new JLabel(ds);
            dsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            dsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(dsLabel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Project Credits
        addInfoSection(contentPanel, "Developed By",
                "Ali Ahmed Memon");

        addInfoSection(contentPanel, "Institution",
                "Sukkur IBA University (IET)");

        addInfoSection(contentPanel, "Year", "2025");

        // Scroll Pane for center content
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Bottom panel with OK button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 20, 15));
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(120, 38));
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okButton.setBackground(new Color(70, 130, 180));
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.setBorderPainted(false);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.addActionListener(this);

        okButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                okButton.setBackground(new Color(55, 110, 160));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                okButton.setBackground(new Color(70, 130, 180));
            }
        });

        bottomPanel.add(okButton);

        // Add Panels
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Helper method to add info section.
     */
    private void addInfoSection(JPanel panel, String title, String content) {
        JLabel titleLabel = new JLabel(title + ":");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        JLabel contentLabel = new JLabel(content);
        contentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentLabel.setForeground(new Color(80, 80, 80));
        contentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(contentLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            this.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            About about = new About();
            about.setVisible(true);
            about.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        });
    }
}