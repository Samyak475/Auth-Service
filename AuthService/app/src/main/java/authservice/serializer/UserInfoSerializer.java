package authservice.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;
import authservice.modal.UserInfoDto;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserInfoSerializer implements Serializer<UserInfoDto> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String topic, UserInfoDto data) {
        byte[] serialzer = null;
        ObjectMapper objectMapper= new ObjectMapper();
        try{
          serialzer=  objectMapper.writeValueAsString(data).getBytes();

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return serialzer;
    }

    @Override
    public byte[] serialize(String topic, Headers headers, UserInfoDto data) {
        return Serializer.super.serialize(topic, headers, data);
    }

    @Override
    public void close() {
        Serializer.super.close();
    }
}
