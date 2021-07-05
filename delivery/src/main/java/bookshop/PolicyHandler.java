package bookshop;

import bookshop.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired DeliveryRepository deliveryRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverBought_PrepareShip(@Payload Bought bought){

        if(bought.isMe()){            
            Delivery delivery = new Delivery();
            delivery.setOrderId(bought.getId());      
            delivery.setStatus("ShipStarted");
            delivery.setAddress(bought.getAddress());
            deliveryRepository.save(delivery);
        }
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCanceled_DeleteCancel(@Payload Canceled canceled){

        if(canceled.isMe()){            
            Delivery delivery2 = new Delivery();
            delivery2.setOrderId(canceled.getId());
            deliveryRepository.delete(delivery2);
        } 
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
