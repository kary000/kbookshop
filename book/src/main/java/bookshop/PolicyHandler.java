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
    @Autowired BookRepository bookRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCanceled_IncreseStock(@Payload Canceled canceled){

        if(canceled.isMe()){        
            Book book = bookRepository.findByBookId(Long.valueOf(canceled.getBookId()));
            book.setStock(book.getStock() + canceled.getQty());
            bookRepository.save(book);  
          }
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
