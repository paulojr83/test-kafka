package br.com.ecommerce.consumer;

import br.com.ecommerce.Message;
import br.com.ecommerce.MessageAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Deserializer;

public class GsonDeserializer implements Deserializer<Message> {

    //public static final String CLAZZ_CONFIG = "br.com.ecommerce.clazz_config";
    final private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Message.class, new MessageAdapter())
            .create();

    /*private Class<T> clazz;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        String clazzName = String.valueOf(configs.get(CLAZZ_CONFIG));
        try {
            this.clazz = (Class<T>) Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
           throw new RuntimeException("Clazz for deserialization does not exist in the classpath", e);
        }
    }*/

    @Override
    public Message deserialize(String s, byte[] bytes) {
        return gson.fromJson(new String(bytes), Message.class);
    }
}
