package bookshop;

import bookshop.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler{
    @Autowired OrderRepository orderRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverShipped_UpdateStatus(@Payload Shipped shipped){

        if(shipped.isMe()){        
            Optional<Order> optionalOrder = orderRepository.findById(shipped.getOrderId());
            Order order = optionalOrder.get();
            order.setStatus("Shipped");
            orderRepository.save(order);
          }
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverBought_UpdateStatus(@Payload Bought bought){

        if(bought.isMe()){  
            Optional<Order> optionalOrder = orderRepository.findById(bought.getId());
            Order order = optionalOrder.get();
            order.setStatus("Bought");
            orderRepository.save(order);
          }
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
