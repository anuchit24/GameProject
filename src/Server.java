package src;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Server {
    private static final int PORT = 4096; // เปลี่ยนพอร์ตตามที่ต้องการ
    private static Set<ClientHandler> clientHandlers = new HashSet<>();
    private static Map<String, ClientHandler> playerMap = new HashMap<>();
    private static final int MAX_PLAYERS = 3; // จำนวนผู้เล่นสูงสุด

    private JFrame frame;
    private JTextArea textArea;
    private JLabel ipLabel;

    public Server() {
        // สร้าง GUI สำหรับเซิร์ฟเวอร์
        frame = new JFrame("Game Server");
        textArea = new JTextArea(10, 30);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        // ตั้งค่าฟอนต์ที่รองรับภาษาไทย
        textArea.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 12));

        // ตรวจจับ IP Address อัตโนมัติ
        String ipAddress = getLocalIPAddress();
        ipLabel = new JLabel("Server IP Address: " + ipAddress);

        // เพิ่ม ScrollPane และ Label เข้าใน Frame
        frame.getContentPane().add(ipLabel, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // แสดงข้อความใน GUI
    private void showMessage(String message) {
        textArea.append(message + "\n");
    }

    // เริ่มการทำงานของเซิร์ฟเวอร์
    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            showMessage("เซิร์ฟเวอร์กำลังทำงาน...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                String playerName = assignPlayerName();
                showMessage(playerName + " เข้าร่วมเกมแล้ว!");

                // สร้าง ClientHandler เพื่อจัดการ client แต่ละคน
                ClientHandler clientHandler = new ClientHandler(clientSocket, playerName);
                clientHandlers.add(clientHandler);
                playerMap.put(playerName, clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            showMessage("เกิดข้อผิดพลาดในเซิร์ฟเวอร์: " + e.getMessage());
        }
    }

    // ส่งข้อความให้กับผู้เล่นทุกคน
    public static void broadcastMessage(String message) {
        for (ClientHandler handler : clientHandlers) {
            handler.sendMessage(message);
        }
    }

    // กำหนดชื่อผู้เล่นใหม่
    private String assignPlayerName() {
        for (int i = 1; i <= MAX_PLAYERS; i++) {
            String playerName = "Player" + i;
            if (!playerMap.containsKey(playerName)) {
                return playerName;
            }
        }
        return "Player" + (playerMap.size() + 1); // ถ้าผู้เล่นเต็ม
    }

    // คลาสสำหรับจัดการ client แต่ละคน
    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String playerName;

        public ClientHandler(Socket socket, String playerName) {
            this.clientSocket = socket;
            this.playerName = playerName;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // ส่งข้อความต้อนรับ
                out.println("ยินดีต้อนรับสู่เกม, " + playerName + "!");
                String inputLine;

                // รับข้อมูลจาก client และส่งไปยังผู้เล่นทุกคน
                while ((inputLine = in.readLine()) != null) {
                    broadcastMessage(playerName + ": " + inputLine);
                }
            } catch (IOException e) {
                showMessage("เกิดข้อผิดพลาดกับ " + playerName + ": " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    showMessage("ไม่สามารถปิดการเชื่อมต่อของ " + playerName);
                }
                showMessage(playerName + " ออกจากเกมแล้ว");
                playerMap.remove(playerName); // ลบผู้เล่นออกจากแผนที่
            }
        }

        // ส่งข้อความให้กับ client
        public void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }
    }

    // ฟังก์ชันตรวจจับ IP Address ของเซิร์ฟเวอร์
    private String getLocalIPAddress() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress(); // ดึง IP Address
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "Unknown IP"; // ในกรณีที่ไม่สามารถตรวจจับ IP ได้
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }
}
