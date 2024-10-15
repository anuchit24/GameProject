package src;

// คลาสสำหรับกระสุน
class Bullet {
    int x, y; // ตำแหน่ง X และ Y ของกระสุน
    int width = 10; // ความกว้างของกระสุน
    int height = 5; // ความสูงของกระสุน
    int speed = 10; // ความเร็วในการเคลื่อนที่ของกระสุน
    boolean movingRight; // ทิศทางการเคลื่อนที่ของกระสุน

    public Bullet(int startX, int startY, boolean facingRight) {
        this.x = startX;
        this.y = startY;
        this.movingRight = facingRight;
    }

    public void update() {
        if (movingRight) {
            x += speed; // เคลื่อนที่ไปทางขวา
        } else {
            x -= speed; // เคลื่อนที่ไปทางซ้าย
        }
    }
}
