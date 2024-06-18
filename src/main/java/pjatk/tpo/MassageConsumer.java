package pjatk.tpo;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

public class MassageConsumer {
    private final KafkaConsumer<String, String> kafkaConsumer;
    private final Consumer<String> messageCallback;

    public MassageConsumer(String topic, String id, Consumer<String> messageCallback) {
        this.messageCallback = messageCallback;

        kafkaConsumer = new KafkaConsumer<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
                        ConsumerConfig.GROUP_ID_CONFIG, id,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true"
                )
        );
        kafkaConsumer.subscribe(Collections.singletonList(topic));
    }

    public void pollMessages() {
        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(1));
        for (ConsumerRecord<String, String> record : records) {
            messageCallback.accept("\n"+record.key() + " : " + record.value());
        }
    }

    public void close() {
        kafkaConsumer.wakeup();
        kafkaConsumer.close();
    }
}
