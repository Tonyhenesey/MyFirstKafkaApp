package pjatk.tpo;

import org.springframework.kafka.test.EmbeddedKafkaBroker;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        EmbeddedKafkaBroker embeddedKafkaBroker = new EmbeddedKafkaBroker(1).kafkaPorts(9092);
        embeddedKafkaBroker.afterPropertiesSet();

        SwingUtilities.invokeLater(() -> {
            new Chat("Chat","Temat Czatu");
//            new Chat("Jakub", "chat");
//            new Chat("Mikołaj", "chat");
//            new Chat("Adam", "chat");
//            new Chat("Marta", "chat");
        });
//        Chat chat = new Chat("Jakub", "chat");


    }
}
