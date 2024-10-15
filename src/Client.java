package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final int SERVER_PORT = 4096;
    private String serverAddress;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;
    private JButton sendButton;

    public Client(String serverAddress) {
        this.serverAddress = serverAddress;
        createGUI();
    }

    // สร้าง GUI สำหรับฝั่ง Client
    private void createGUI() {
        frame = new JFrame("Game Client");
        textArea = new JTextArea(10, 30);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        textField = new JTextField(20);
        sendButton = new JButton("Send");

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(textField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        // ตั้งค่าฟอนต์ที่รองรับภาษาไทย
        textArea.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 12));
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // ตั้งค่าการกระทำเมื่อกดปุ่มส่งข้อความ
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
    }

    // Client
    public void startClient() {
        try {
            socket = new Socket(serverAddress, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // เริ่ม thread สำหรับการรับข้อความจากเซิร์ฟเวอร์
            Thread listenerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String serverMessage;
                        while ((serverMessage = in.readLine()) != null) {
                            textArea.append(serverMessage + "\n");
                        }
                    } catch (IOException e) {
                        textArea.append("เกิดข้อผิดพลาดในการรับข้อมูลจากเซิร์ฟเวอร์\n");
                    }
                }
            });
            listenerThread.start();

            // สร้างหน้าเกมที่นี่
            SwingUtilities.invokeLater(() -> {
                GameStartScreen game = new GameStartScreen();
                game.setVisible(true);
            });

        } catch (IOException e) {
            textArea.append("ไม่สามารถเชื่อมต่อกับเซิร์ฟเวอร์: " + e.getMessage() + "\n");
        }
    }

    // ส่งข้อความไปยังเซิร์ฟเวอร์
    private void sendMessage() {
        String message = textField.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            textField.setText("");
        }
    }

    public static void main(String[] args) {
        String serverIP = JOptionPane.showInputDialog("Enter server IP address:");
        if (serverIP != null && !serverIP.trim().isEmpty()) {
            Client client = new Client(serverIP);
            client.startClient();
        } else {
            System.out.println("กรุณาใส่ IP Address ของเซิร์ฟเวอร์");
        }
    }
}
