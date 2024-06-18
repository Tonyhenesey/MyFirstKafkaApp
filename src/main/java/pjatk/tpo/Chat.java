package pjatk.tpo;


import org.apache.kafka.clients.producer.ProducerRecord;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class Chat extends JFrame {
    private JTextArea chatView;
    private JTextField textField1;
    private JButton sendButton;
    private JButton logOutButton;
    private JButton loginButton;
    private JPanel panel1;
    DefaultListModel<String> listModel = new DefaultListModel<>();
    JList<String> loginList = new JList<>(listModel);
    private JScrollPane scrollPane2;
    private JButton newChatButton;
    private JTextArea currentUser;
    ChatHistory chatHistory;
    private MassageConsumer massageConsumer;
    private MassageProducer massageProducer;
    private String topic;
    private String id;
    private static Chat originalChatInstance;
    public Chat(String id, String topic) throws HeadlessException {

        this.id = id;
        this.topic = topic;
        this.chatHistory = new ChatHistory();
        this.massageProducer = new MassageProducer();
        initUI();
        listModel.addElement("User: " + id);
        massageConsumer = new MassageConsumer(topic, id, this::updateChatView);

        startConsumer();

        sendButton.addActionListener(e -> sendMessage());
//        sendButton.addActionListener(e -> System.out.println("\n"));
        loginButton.addActionListener(e -> logIn());
        logOutButton.addActionListener(e -> {
            logOut();
            this.dispose();


        });
        newChatButton.addActionListener(e -> addTopic());

        loadChatHistory(chatHistory.getHistory(id));
        if (originalChatInstance == null) {
            originalChatInstance = this;
        }
    }
    public Chat()throws HeadlessException{
        initUI();
        loginButton.addActionListener(e -> logIn());

    }

    private void initUI() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.add(panel1);
        this.setVisible(true);
        this.setTitle(topic);
        currentUser.setText("Current User: "+id);
        this.pack();

        scrollPane2.add(loginList);
        scrollPane2.setViewportView(loginList);
        scrollPane2.setVisible(true);
        KeyListener keyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        };

    }


    private void startConsumer() {
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                if (massageConsumer != null) {
                    massageConsumer.pollMessages();
                }
            }
        });
    }


    private void sendMessage() {
        String message = textField1.getText()+"\n";
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
//        if (message.equals("\" : logged in\"")){
//            MassageProducer.send(new ProducerRecord<>(topic, message));
//        }
        massageProducer.send(new ProducerRecord<>(topic, id," [" + timestamp + "] : "+ message));
//        updateChatView(id + " [" + timestamp + "] : "+ message + "\n");
//        chatView.append(" [" + timestamp + "] : "+ message);
        chatHistory.addMessage(id," [" + timestamp + "] : "+ message);
        textField1.setText("");
    }

    private void logIn() {
        String newId = JOptionPane.showInputDialog(this, "Enter your ID:");

        if (newId != null && !newId.trim().isEmpty()) {
//            if (massageConsumer != null) {
//                massageConsumer.close();
//            }
//            this.id = newId.trim();
            this.chatView.append(newId + " : logged in");
//            updateChatView(newId + " : logged in");
            Chat chat = new Chat(newId, topic);
            chat.setTitle(newId);
            chat.updateChatView(newId + " : logged in");
//            for (int i=0;i<listModel.size();i++) {
//                listModel.addElement(listModel.get(i));
//            }
            listModel.addElement("User: "+newId);

//            massageConsumer = new MassageConsumer(topic, newId,this::updateChatView);
//            startConsumer();

        }
    }

    private void logOut() {

//        updateChatView(id + " : logged out");
        if (massageConsumer != null) {
            massageConsumer.close();
            massageConsumer = null;
        }
        JOptionPane.showMessageDialog(this, "You have logged out.");
//        if (originalChatInstance != null) {
//            originalChatInstance.updateChatView(id + " : logged out");
//        }

    }


    private void updateChatView(String message) {
        ChatHistory.addMessage(id, message);
        SwingUtilities.invokeLater(() -> chatView.append(message + "\n"));
    }

    private void loadChatHistory(List<String> history) {
        for (String message : history) {
            chatView.append(message + "\n");
        }

    }
    private void addTopic() {
        String newTopic = JOptionPane.showInputDialog(this, "Enter topic to add to chat:");
        if (newTopic != null && !newTopic.trim().isEmpty()) {
            Chat chat=new Chat(id, newTopic);
            new MassageConsumer(newTopic, id, this::updateChatView);}
        startConsumer();


    }

}