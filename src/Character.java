package src;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Character extends JFrame {

    // Character and basic info
    private static final String[] characters = { "Viper", "Shadowshot", "Bulletstorm" };
    private static final int[] health = { 100, 80, 70 };
    private static final int[] attack = { 80, 100, 90 };
    private static final int[] defense = { 90, 60, 70 };

    // Character images (update with actual file paths)
    private static final String[] characterImages = {
            "images/page1.png",
            "images/page2.png",
            "images/page3.png"
    };

    // Background image
    private static final String backgroundImage = "images/bk1.jpg"; // อัพเดทเส้นทางไฟล์รูปภาพ

    // Selected character info
    private static JLabel selectedCharacterLabel;
    private static JLabel selectedCharacterStats;

    public Character() {
        // Create main window settings
        setTitle("Character Selection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create a panel with background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon bgIcon = new ImageIcon(backgroundImage);
                Image bgImage = bgIcon.getImage();
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout()); // Layout for background panel

        // Create a panel for displaying selected character info
        JPanel selectedPanel = new JPanel();
        selectedPanel.setOpaque(false); // Transparent background
        selectedPanel.setLayout(new GridLayout(2, 1));
        selectedCharacterLabel = new JLabel("Selected Character: None");
        selectedCharacterLabel.setFont(new Font("Bodoni MT Condensed", Font.BOLD, 24));
        selectedCharacterStats = new JLabel("Stats: ");
        selectedCharacterStats.setFont(new Font("Aptos Black", Font.PLAIN, 20));
        selectedPanel.add(selectedCharacterLabel);
        selectedPanel.add(selectedCharacterStats);

        // Create panel for character selection buttons using GridBagLayout to center
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Transparent background
        GridBagLayout gbl = new GridBagLayout();
        buttonPanel.setLayout(gbl);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20); // Set padding around buttons
        gbc.anchor = GridBagConstraints.CENTER; // Center alignment

        // Create buttons for each character with images
        for (int i = 0; i < characters.length; i++) {
            ImageIcon originalIcon = new ImageIcon(characterImages[i]);
            Image scaledImage = originalIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            JButton characterButton = new JButton(scaledIcon); // No text, image only
            characterButton.setPreferredSize(new Dimension(450, 450)); // Set same size as the image
            characterButton.setMaximumSize(new Dimension(450, 450)); // Ensure max size is fixed
            characterButton.setVerticalTextPosition(SwingConstants.BOTTOM);
            characterButton.setHorizontalTextPosition(SwingConstants.CENTER);
            characterButton.setBackground(new Color(0, 0, 0, 0)); // Set background to fully transparent
            characterButton.setForeground(Color.WHITE);

            // Make button transparent
            characterButton.setContentAreaFilled(false);
            characterButton.setBorderPainted(false);
            characterButton.setFocusPainted(false); // Remove focus border

            final int index = i;
            characterButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectCharacter(index);
                }
            });

            gbc.gridx = i; // Position buttons horizontally
            buttonPanel.add(characterButton, gbc); // Add buttons to GridBagLayout
        }

        // Panel สำหรับปุ่ม Confirm
        JPanel confirmPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // ใช้ FlowLayout สำหรับปุ่ม Confirm
        confirmPanel.setOpaque(false); // ตั้งเป็นโปร่งใส
        confirmPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 0)); // เพิ่มระยะขอบด้านล่าง 100px

        JButton confirmButton = new JButton("CONFIRM");
        confirmButton.setPreferredSize(new Dimension(200, 60)); // ปรับขนาดปุ่มให้สั้นลง
        confirmButton.setFont(new Font("Bodoni MT Condensed", Font.BOLD, 24));
        confirmButton.setBackground(Color.GREEN);
        confirmButton.setForeground(Color.BLACK);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(Character.this, "You have selected: " + selectedCharacterLabel.getText());
            }
        });

        // เพิ่มปุ่มใน confirmPanel
        confirmPanel.add(confirmButton);

        // Add components to the main window
        backgroundPanel.add(selectedPanel, BorderLayout.NORTH);
        backgroundPanel.add(buttonPanel, BorderLayout.CENTER); // Add button panel to center
        backgroundPanel.add(confirmPanel, BorderLayout.SOUTH);

        // Set the frame to full screen
        setContentPane(backgroundPanel);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen mode
        setVisible(true);
    }

    // Function to select character
    private static void selectCharacter(int index) {
        selectedCharacterLabel.setText("Selected Character: " + characters[index]);
        selectedCharacterStats.setText(
                "Stats: Health: " + health[index] + ", Attack: " + attack[index] + ", Defense: " + defense[index]);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Character(); // สร้างหน้า Character
        });
    }
}
