package bookshop;

import bookshop.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MypageViewHandler {


    @Autowired
    private MypageRepository mypageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenBought_then_CREATE_1 (@Payload Bought bought) {
        try {

            if (bought.isMe()){
            // view 객체 생성
            Mypage mypage = new Mypage();
            // view 객체에 이벤트의 Value 를 set 함
            mypage.setBookId(bought.getBookId());
            mypage.setOrderId(bought.getId());
            mypage.setTitle(bought.getTitle());
            mypage.setQty(bought.getQty());
            mypage.setAddress(bought.getAddress());
            mypage.setStatus(bought.getStatus());
            mypage.setReview("");
            // view 레파지 토리에 save
            mypageRepository.save(mypage);
            }           
        
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenShipped_then_UPDATE_1(@Payload Shipped shipped) {
        try {
            //if (!shipped.validate()) return;
            if (shipped.isMe()){
                // view 객체 조회
                List<Mypage> mypageList = mypageRepository.findByOrderId(shipped.getOrderId());
                for(Mypage mypage : mypageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    mypage.setStatus(shipped.getStatus());
                    // view 레파지 토리에 save
                    mypageRepository.save(mypage);
                }
             }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenWrote_then_UPDATE_2(@Payload Wrote wrote) {
        try {
            //if (!wrote.validate()) return;
            if (wrote.isMe()){
                // view 객체 조회
                List<Mypage> mypageList = mypageRepository.findByOrderId(wrote.getOrderId());
                for(Mypage mypage : mypageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    mypage.setReview(wrote.getContents());
                    // view 레파지 토리에 save
                    mypageRepository.save(mypage);
                }
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}