import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ChatServerGUI extends UnicastRemoteObject implements ChatServerInterface {
    private List<ChatClientInterface> clients = new ArrayList<>();
    private JTextArea logTextArea;

    // Konstruktor untuk membuat objek ChatServerGUI.
    public ChatServerGUI() throws RemoteException {
        super();

        JFrame frame = new JFrame("Chat Server Log");
        logTextArea = new JTextArea(10, 40);
        logTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logTextArea);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        log("Server started");

        try {
            // Membuat dan mendaftarkan server di registry RMI
            java.rmi.registry.Registry registry = java.rmi.registry.LocateRegistry.createRegistry(1099);
            registry.rebind("ChatServer", this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    Metode untuk mencatat pesan ke dalam log.
    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logTextArea.append(message + "\n");
        });
    }

    @Override
    public void registerClient(ChatClientInterface client) throws RemoteException {
        // Menambahkan client baru ke daftar dan memberi tahu semua client bahwa ada pengguna baru
        clients.add(client);
        broadcastMessage("New user joined the chat.");
        log("Client registered: " + client.toString());
    }

    @Override
    public void broadcastMessage(String message) throws RemoteException {
        // Menyiarkan pesan ke semua client yang terdaftar
        log("Broadcasting message: " + message);

        SwingUtilities.invokeLater(() -> {
            for (ChatClientInterface client : clients) {
                try {
                    client.receiveMessage(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    Metode utama untuk menjalankan server chat GUI.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ChatServerGUI();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }
}
