package src;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GameStartScreen extends JFrame {
    private Image backgroundImage;

    public GameStartScreen() {
        setTitle("Game Start"); // ตั้งชื่อหน้าต่าง
        setExtendedState(JFrame.MAXIMIZED_BOTH); // เต็มจอ
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ปิดโปรแกรมเมื่อปิดหน้าต่าง
        setLocationRelativeTo(null); // จัดกลางหน้าต่าง
        setUndecorated(true); // ลบการตกแต่งของหน้าต่าง

        // โหลดภาพพื้นหลัง
        try {
            backgroundImage = ImageIO.read(new File("images/st.jpg")); // ปรับเส้นทางนี้ตามที่ต้องการ
        } catch (IOException e) {
            System.err.println("ไม่สามารถโหลดภาพพื้นหลัง: " + e.getMessage());
        }

        // สร้างพาเนลสำหรับปุ่ม
        JPanel panel = new StartScreenPanel();
        panel.setLayout(new GridBagLayout()); // ใช้ GridBagLayout สำหรับการจัดตำแหน่ง
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE; // ปรับขนาดตามเนื้อหาของปุ่ม
        gbc.anchor = GridBagConstraints.CENTER; // จัดตำแหน่งกึ่งกลาง

        // โหลดและปรับขนาดภาพสำหรับปุ่มเริ่มเกม
        ImageIcon startIcon = loadScaledIcon("images/start.png", 300, 250); // โหลดภาพและปรับขนาด
        if (startIcon != null) {
            JButton startButton = new JButton(startIcon); // ใช้เฉพาะไอคอนภาพ
            startButton.setContentAreaFilled(false); // ทำให้พื้นหลังปุ่มโปร่งใส
            startButton.setBorderPainted(false); // ลบขอบของปุ่ม
            startButton.setFocusPainted(false); // ลบกรอบที่ปรากฏเมื่อคลิก
            startButton.setPreferredSize(new Dimension(300, 200)); // ตั้งขนาดปุ่มให้ตรงกับภาพ
            startButton.addActionListener(new StartGameAction()); // เพิ่ม ActionListener
            gbc.gridx = 0;
            gbc.gridy = 0; // วางปุ่มเริ่มเกมที่ตำแหน่งแรก
            gbc.insets = new Insets(400, 0, -100, 0); // ปรับระยะห่างให้เหมาะสม
            panel.add(startButton, gbc);
        }

        // โหลดและปรับขนาดภาพสำหรับปุ่มออก
        ImageIcon exitIcon = loadScaledIcon("images/Exit.png", 270, 200); // โหลดภาพและปรับขนาด
        if (exitIcon != null) {
            JButton exitButton = new JButton(exitIcon); // ใช้เฉพาะไอคอนภาพ
            exitButton.setContentAreaFilled(false); // ทำให้พื้นหลังปุ่มโปร่งใส
            exitButton.setBorderPainted(false); // ลบขอบของปุ่ม
            exitButton.setFocusPainted(false); // ลบกรอบที่ปรากฏเมื่อคลิก
            exitButton.setPreferredSize(new Dimension(270, 200)); // ตั้งขนาดปุ่มให้ตรงกับปุ่มเริ่มเกม
            exitButton.addActionListener(e -> System.exit(0)); // ออกจากโปรแกรมเมื่อคลิก
            gbc.gridx = 0;
            gbc.gridy = 1; // วางอยู่ด้านล่างปุ่มเริ่มเกม
            gbc.insets = new Insets(20, 0, 50, 0); // ปรับระยะห่างระหว่างปุ่มให้น้อยที่สุด
            panel.add(exitButton, gbc);
        }

        // เพิ่มพาเนลลงในหน้าต่าง
        add(panel);
        setVisible(true); // ทำให้หน้าต่างมองเห็น
    }

    // ฟังก์ชันสำหรับโหลดและปรับขนาดภาพ
    private ImageIcon loadScaledIcon(String filePath, int width, int height) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(filePath)); // โหลดภาพต้นฉบับ
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH); // ปรับขนาดภาพ
            return new ImageIcon(scaledImage); // สร้าง ImageIcon ใหม่จากภาพที่ปรับขนาด
        } catch (IOException e) {
            System.err.println("ไม่สามารถโหลดภาพ: " + filePath + " - " + e.getMessage());
            return null; // ส่งคืนค่า null ถ้าภาพโหลดไม่สำเร็จ
        }
    }

    // พาเนลที่กำหนดเองเพื่อวาดพื้นหลัง
    private class StartScreenPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // วาดภาพพื้นหลัง
            }
        }
    }

    // Action สำหรับเริ่มเกม
    private class StartGameAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // แสดงหน้า Character โดยไม่ต้องรับ IP
            SwingUtilities.invokeLater(() -> {
                Character characterPage = new Character(); // สร้างหน้า Character
                characterPage.setVisible(true); // แสดงหน้าต่าง Character
                dispose(); // ปิดหน้าจอเริ่มเกม
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameStartScreen startScreen = new GameStartScreen(); // สร้างหน้าจอเริ่มเกม
            startScreen.setVisible(true); // ทำให้หน้าจอเริ่มเกมมองเห็น
        });
    }
}
