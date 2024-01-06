import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.Random;

public class ChatClientGUI extends UnicastRemoteObject implements ChatClientInterface {
    private JTextArea chatTextArea;
    private JTextField messageField;
    private ChatServerInterface server;
    
    // Mendeklarasikan objek Random untuk menghasilkan identitas unik klien
    Random random = new Random();
    int randomNumber = random.nextInt(9000) + 1000;

//    Konstruktor untuk membuat objek ChatClientGUI.
    public ChatClientGUI() throws RemoteException {
        super();

        JFrame frame = new JFrame("Chat Client");
        chatTextArea = new JTextArea(10, 40);
        chatTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatTextArea);

        messageField = new JTextField(30);
        JButton sendButton = new JButton("Send");

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(messageField);
        bottomPanel.add(sendButton);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Menambahkan ActionListener untuk tombol "Send" agar dapat mengirim pesan ke server
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                if (!message.isEmpty()) {
                    try {
                        // Mengirim pesan ke server dengan menyertakan identitas unik klien
                        server.broadcastMessage("User[" + randomNumber + "] : " + message);
                        messageField.setText("");
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        try {
            // Mendapatkan referensi server dari registry RMI
            java.rmi.registry.Registry registry = java.rmi.registry.LocateRegistry.getRegistry("localhost", 1099);
            server = (ChatServerInterface) registry.lookup("ChatServer");
            
            // Mendaftarkan klien ke server
            server.registerClient(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveMessage(String message) throws RemoteException {
        // Menampilkan pesan yang diterima dari server pada antarmuka pengguna klien
        SwingUtilities.invokeLater(() -> {
            chatTextArea.append(message + "\n");
        });
    }

//    Metode utama untuk menjalankan klien chat GUI.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ChatClientGUI();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }
}
