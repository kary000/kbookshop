package bookshop;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Order_table")
public class Order {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long bookId;
    private String buyer;
    private String title;
    private Integer qty;
    private String address;
    private String status;

    @PostPersist
    public void onPostPersist(){

            boolean rslt = OrderApplication.applicationContext.getBean(bookshop.external.BookService.class)
            .modifyStock(this.getBookId(), this.getQty());

            if (rslt) {
                //this.setStatus("Bought");
                this.setStatus(System.getenv("STATUS"));

                Bought bought = new Bought();
                BeanUtils.copyProperties(this, bought);
                bought.publishAfterCommit();              
            } else {throw new OrderException("No Available stock!");}            


    }

    @PreRemove
    public void onPreRemove(){
        bookshop.external.Cancellation cancellation = new bookshop.external.Cancellation();
        // mappings goes here
        cancellation.setOrderId(this.getId());

        this.setStatus("Cancelled");
        Canceled canceled = new Canceled();
        BeanUtils.copyProperties(this, canceled);
        canceled.publishAfterCommit();

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }




}
