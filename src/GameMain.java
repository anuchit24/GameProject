package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

class Bullet {
    int x, y; // ตำแหน่งของกระสุน
    int width = 10; // ความกว้างของกระสุน
    int height = 5; // ความสูงของกระสุน
    boolean facingRight;

    Bullet(int startX, int startY, boolean facingRight) {
        this.x = startX;
        this.y = startY;
        this.facingRight = facingRight;
    }

    void update() {
        if (facingRight) {
            x += 10; // กระสุนเคลื่อนที่ไปทางขวา
        } else {
            x -= 10; // กระสุนเคลื่อนที่ไปทางซ้าย
        }
    }
}

class Platform {
    int x, y, width, height; // ตำแหน่งและขนาดของแพลตฟอร์ม

    Platform(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    void draw(Graphics g) {
        g.setColor(Color.GRAY); // สีของแพลตฟอร์ม
        g.fillRect(x, y, width, height);
    }
}

public class GameMain extends JFrame {
    private Image backgroundImage;
    private Image characterImage;
    private BufferedImage bufferedImage; // BufferedImage สำหรับการวาด
    private Graphics2D g2d; // Graphics2D สำหรับวาดใน BufferedImage
    private boolean platformMovable = true;

    private int characterX = 100; // ตำแหน่ง X ของตัวละคร
    private int characterY = 650; // ตำแหน่ง Y ของตัวละคร
    private int characterWidth = 150; // ความกว้างที่ต้องการของตัวละคร
    private int characterHeight = 150; // ความสูงที่ต้องการของตัวละคร
    private boolean facingRight = true; // ทิศทางของตัวละคร (หันหน้าไปทางขวาหรือซ้าย)

    private boolean isJumping = false; // สถานะการกระโดด
    private int jumpStrength = 15; // ความแรงในการกระโดด
    private int gravity = 1; // ค่าแรงโน้มถ่วง
    private int verticalSpeed = 0; // ความเร็วในแนวตั้ง

    private boolean movingLeft = false; // ตรวจสอบว่ากำลังเคลื่อนที่ไปทางซ้าย
    private boolean movingRight = false; // ตรวจสอบว่ากำลังเคลื่อนที่ไปทางขวา

    // รายการกระสุน
    private List<Bullet> bullets = new ArrayList<>();
    // รายการแพลตฟอร์ม
    private List<Platform> platforms = new ArrayList<>();

    public GameMain() {
        // โหลดภาพพื้นหลังและตัวละคร
        backgroundImage = new ImageIcon("images/main.jpg").getImage();
        characterImage = new ImageIcon("images/egg.png").getImage();

        // ตั้งค่า JFrame
        setTitle("Pixel Game");
        setSize(backgroundImage.getWidth(null), backgroundImage.getHeight(null));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // สร้าง JPanel สำหรับการวาด
        JPanel gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // วาดภาพใน BufferedImage
                g2d = bufferedImage.createGraphics();
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

                // วาดแพลตฟอร์ม
                for (Platform platform : platforms) {
                    platform.draw(g2d);
                }

                // วาดตัวละครโดยกำหนดขนาด
                if (facingRight) {
                    g2d.drawImage(characterImage, characterX, characterY, characterWidth, characterHeight, null);
                } else {
                    // ถ้าหันหน้าไปทางซ้าย ต้องใช้การวาดภาพกลับ
                    g2d.drawImage(characterImage, characterX + characterWidth, characterY, -characterWidth,
                            characterHeight, null);
                }

                // วาดกระสุน
                for (Bullet bullet : bullets) {
                    g2d.fillRect(bullet.x, bullet.y, bullet.width, bullet.height); // วาดกระสุนเป็นสี่เหลี่ยม
                }

                g2d.dispose();
                g.drawImage(bufferedImage, 0, 0, null); // วาด BufferedImage
            }
        };

        bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB); // สร้าง BufferedImage

        // เพิ่ม KeyListener สำหรับควบคุมทิศทาง
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        movingLeft = true; // เริ่มเคลื่อนที่ไปทางซ้าย
                        facingRight = false; // หันหน้าซ้าย
                        break;
                    case KeyEvent.VK_RIGHT:
                        movingRight = true; // เริ่มเคลื่อนที่ไปทางขวา
                        facingRight = true; // หันหน้าขวา
                        break;
                    case KeyEvent.VK_UP:
                        if (!isJumping) { // ตรวจสอบว่าตัวละครไม่ได้กระโดดอยู่
                            isJumping = true; // เริ่มกระโดด
                            verticalSpeed = -jumpStrength; // กำหนดความเร็วเริ่มต้นในการกระโดด
                        }
                        break;
                    case KeyEvent.VK_SPACE: // กดปุ่ม SPACE เพื่อยิง
                        shoot(); // เรียกใช้ฟังก์ชันยิง
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        movingLeft = false; // หยุดเคลื่อนที่ไปทางซ้าย
                        break;
                    case KeyEvent.VK_RIGHT:
                        movingRight = false; // หยุดเคลื่อนที่ไปทางขวา
                        break;
                }
            }
        });

        // สร้างแพลตฟอร์ม
        platforms.add(new Platform(200, 700, 200, 20)); // ตัวอย่างแพลตฟอร์ม

        // เพิ่ม JPanel ไปยัง JFrame
        add(gamePanel);

        // เริ่ม Thread สำหรับการอัปเดตตำแหน่งตัวละคร
        new Thread(this::gameLoop).start();
    }

    private void shoot() {
        int bulletStartX;
        int bulletStartY = characterY + characterHeight / 2 - 11; // ปรับให้ออกสูงขึ้นนิดนึง

        // หากตัวละครหันหน้าขวา
        if (facingRight) {
            bulletStartX = characterX + characterWidth; // ลูกกระสุนออกจากด้านขวาของตัวละคร
        } else {
            bulletStartX = characterX; // ลูกกระสุนออกจากด้านซ้ายของตัวละคร
        }

        // สร้างกระสุนใหม่และเพิ่มไปยังลิสต์
        Bullet bullet = new Bullet(bulletStartX, bulletStartY, facingRight);
        bullets.add(bullet);
    }

    private void gameLoop() {
        while (true) {
            update(); // อัปเดตตำแหน่งตัวละครและกระสุน
            repaint(); // อัปเดตการวาดใหม่
            try {
                Thread.sleep(20); // รอ 20 ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        // อัปเดตตำแหน่งตัวละคร
        if (movingLeft) {
            characterX -= 5; // เคลื่อนที่ไปทางซ้าย
        }
        if (movingRight) {
            characterX += 5; // เคลื่อนที่ไปทางขวา
        }

        // การกระโดด
        if (isJumping) {
            characterY += verticalSpeed; // อัปเดตตำแหน่ง Y
            verticalSpeed += gravity; // เพิ่มความเร็วในแนวตั้ง

            // ตรวจสอบการชนกับแพลตฟอร์ม
            for (Platform platform : platforms) {
                if (characterX + characterWidth > platform.x && characterX < platform.x + platform.width &&
                        characterY + characterHeight >= platform.y
                        && characterY + characterHeight <= platform.y + verticalSpeed) {
                    characterY = platform.y - characterHeight; // ยืนบนแพลตฟอร์ม
                    isJumping = false; // หยุดกระโดด
                    verticalSpeed = 0; // ตั้งความเร็วในแนวตั้งเป็น 0
                    break; // ออกจากลูปเมื่อชนกับแพลตฟอร์ม
                }
            }

            // ตรวจสอบว่าตัวละครลงมาถึงพื้น
            if (characterY >= 650) { // ถ้าตัวละครถึงพื้น
                characterY = 650; // ตำแหน่ง Y ที่ถูกต้อง
                isJumping = false; // หยุดกระโดด
                verticalSpeed = 0; // ตั้งความเร็วในแนวตั้งเป็น 0
            }
        } else { // ถ้าไม่กระโดด
            // ตรวจสอบว่าตัวละครอยู่เหนือแพลตฟอร์มหรือไม่
            boolean isOnPlatform = false; // ตัวแปรตรวจสอบว่าตัวละครอยู่บนแพลตฟอร์มหรือไม่
            for (Platform platform : platforms) {
                if (characterX + characterWidth > platform.x && characterX < platform.x + platform.width &&
                        characterY + characterHeight <= platform.y) {
                    isOnPlatform = true; // ตัวละครอยู่บนแพลตฟอร์ม
                    characterY = platform.y - characterHeight; // ให้ตัวละครอยู่บนแพลตฟอร์ม
                    verticalSpeed = 0; // ตั้งความเร็วในแนวตั้งเป็น 0
                    break; // ออกจากลูปเมื่ออยู่บนแพลตฟอร์ม
                }
            }

            // ถ้าตัวละครไม่ได้อยู่บนแพลตฟอร์ม ให้ตกลงมา
            if (!isOnPlatform) {
                verticalSpeed += gravity; // เพิ่มความเร็วในแนวตั้งสำหรับการตก
                characterY += verticalSpeed; // ให้ตัวละครตกลงมา

                // ตรวจสอบว่าตัวละครถึงพื้น
                if (characterY >= 650) { // ถ้าตัวละครถึงพื้น
                    characterY = 650; // ตำแหน่ง Y ที่ถูกต้อง
                    verticalSpeed = 0; // ตั้งความเร็วในแนวตั้งเป็น 0
                }
            }
        }

        // อัปเดตตำแหน่งกระสุน
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            bullet.update(); // อัปเดตตำแหน่งกระสุน
            // ลบกระสุนถ้าหากกระสุนออกจากหน้าจอ
            if (bullet.x < 0 || bullet.x > getWidth()) {
                bullets.remove(i);
            }
        }

        // ตรวจสอบว่าแพลตฟอร์มสามารถขยับได้หรือไม่
        if (platformMovable) {
            for (Platform platform : platforms) {
                if (platform.y < 650) { // ตรวจสอบว่าแพลตฟอร์มอยู่สูงกว่า 650 หรือไม่
                    platform.y += 5; // ขยับแพลตฟอร์มลง 5 หน่วย
                } else {
                    platformMovable = false; // หยุดการขยับแพลตฟอร์มเมื่อถึงขนาดที่กำหนด
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameMain game = new GameMain();
            game.setVisible(true);
        });
    }
}
