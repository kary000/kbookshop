![image](https://user-images.githubusercontent.com/84000898/122630609-f58f9e00-d0ff-11eb-91f2-43c0dcb23fb5.png)

# Book Shop

본 프로젝트는 MSA/DDD/Event Storming/EDA 를 포괄하는 분석/설계/구현/운영 전단계를 커버하도록 구성한 프로젝트입니다.
이는 클라우드 네이티브 애플리케이션의 개발에 요구되는 체크포인트들을 통과하기 위한 내용을 포함합니다.

# 서비스 시나리오

기능적 요구사항
1. 서점은 새로운 도서를 등록할 수 있다.
2. 고객이 도서제목으로 주문한다.
3. 서점에 없는 도서를 주문시 수량이 없다고 안내한다.
4. 주문이 되면 주문 내역이 배송 업체에게 전달된다.
5. 주문 내역이 배송 업체에 전달되는 동시에, 구매 가능 수량이 변경된다.
6. 배송업체에서 주문 내역을 확인하여 배송을 준비한다.
7. 도서배송이 완료되면, 배송완료 상태로 변경된다.
8. 고객이 주문을 취소할 수 있고, 취소하면 구매 가능한 수량이 증가된다.
9. 고객이 주문내역에 대해 리뷰를 작성한다.
10. 고객이 주문내역을 조회한다.
   ex) 도서제목, 수량, 주소, 배송상태, 리뷰

비기능적 요구사항
1. 트랜잭션
    1. 도서 주문건은 대여 가능 수량이 변경되어야 한다. Sync 호출
2. 장애격리
    1. 도서 배송관리 기능이 수행되지 않더라도 주문은 365일 24시간 받을 수 있어야 한다.  Async (event-driven), Eventual Consistency
    2. 도서주문이 과중되면 사용자를 잠시동안 받지 않고 주문을 잠시후에 하도록 유도한다.  Circuit breaker
3. 성능
    1. 고객이 주문내역을 별도의 고객페이지에서 확인할 수 있어야 한다. CQRS

# 체크포인트

1. Saga
2. CQRS
3. Correlation
4. Req/Resp
5. Gateway
6. Deploy/ Pipeline
7. Circuit Breaker
8. Autoscale (HPA)
9. Zero-downtime deploy
10. f Map / Persistence Volume
11. Polyglot
12. Self-healing (Liveness Probe)

# 분석/설계

## AS-IS 조직 (Horizontally-Aligned)
  ![image](https://user-images.githubusercontent.com/487999/79684144-2a893200-826a-11ea-9a01-79927d3a0107.png)

## TO-BE 조직 (Vertically-Aligned)
  ![image](https://user-images.githubusercontent.com/84000863/121344166-65fb2a00-c95e-11eb-97b3-8d1490beb909.png)


## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과:  http://www.msaez.io/#/storming/tdKjnnj8k4dt4Pik8DOnp0yYffp2/mine/c530fa85996378909151f526c2952e29


### 이벤트 도출
![image](https://user-images.githubusercontent.com/84000898/124341305-5fc33b00-dbf6-11eb-855f-9c2180729a57.png)


### 액터, 커맨드 부착하여 읽기 좋게
![image](https://user-images.githubusercontent.com/84000898/124341345-99944180-dbf6-11eb-9803-bb3c3771e3d0.png)

### 어그리게잇으로 묶기
![image](https://user-images.githubusercontent.com/84000898/124341354-a6189a00-dbf6-11eb-88c0-987605960d0c.png)

    - book, order, delivery, review를 그와 연결된 command 와 event 들에 의하여 트랜잭션이 유지되어야 하는 단위로 그들 끼리 묶어줌


### 완성 모형

![image](https://user-images.githubusercontent.com/84000898/124341419-322ac180-dbf7-11eb-895d-321c06ce821b.png)

### 완성본에 대한 기능적 요구사항을 커버하는지 검증
    - 담당자가 책을 등록한다 (ok)
    - 고객이 책을 주문한다 (ok)
    - 주문이 되면 배송업체에게 전달된다 (ok)
    - 주문 내역이 배송업체에 전달되는 동시에, 주문 가능 수량이 변경된다. (ok)
    - 배송업체에서 주문 내역을 확인하여 책을 준비한다. (ok)
    - 책을 준비 후, 배송되면 배송완료 상태로 변경된다. (ok)
    - 고객이 주문을 취소할 수 있다 (ok)
    - 고객이 주문정보를 중간중간 조회한다. (View-green sticker 의 추가로 ok) 
    - 고객이 리뷰를 작성하면 마이페이지에서 조회된다. (ok)

### 완성본에 대한 비기능적 요구사항을 커버하는지 검증
    - 주문된 책에 대해 주문 가능 수량이 변경되어야 한다. (Req/Res)
    - 배송 기능이 수행되지 않더라도 주문은 365일 24시간 받을 수 있어야 한다. (Pub/sub)
    - 책 주문이 과중되면 잠시동안 받지 않고 주문을 잠시후에 하도록 유도한다 (Circuit breaker)
    - 고객이 주문상태를 별도의 고객페이지에서 확인할 수 있어야 한다 (CQRS)


## 헥사고날 아키텍처 다이어그램 도출
    
![image](https://user-images.githubusercontent.com/84000863/122320373-15d32780-cf5d-11eb-95b7-23935cda9bb4.png)

    - Chris Richardson, MSA Patterns 참고하여 Inbound adaptor와 Outbound adaptor를 구분함
    - 호출관계에서 PubSub 과 Req/Resp 를 구분함
    - 서브 도메인과 바운디드 컨텍스트의 분리:  각 팀의 KPI 별로 아래와 같이 관심 구현 스토리를 나눠가짐


# 구현:

구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 808n 이다)

```
cd book
mvn spring-boot:run

cd order
mvn spring-boot:run 

cd delivery
mvn spring-boot:run  

cd mapage
mvn spring-boot:run

cd review
mvn spring-boot:run

cd gateway
mvn spring-boot:run

```

## DDD 의 적용

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다. 아래가 그 예시이다.

```
package bookshop;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Book_table")
public class Book {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long bookId;
    private String title;
    private String author;
    private String publisher;
    private Integer stock;
    
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
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

}

```
- Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다
```
package bookshop;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="books", path="books")
public interface BookRepository extends PagingAndSortingRepository<Book, Long>{
    Book findByBookId(Long bookId);

}
```
- 적용 후 REST API 의 테스트
```
# book 서비스의 책등록처리
http POST http://localhost:8081/books title=NoMadLand author=nana publisher=cnn stock=10 bookId=1
![image](https://user-images.githubusercontent.com/84000898/124405875-bc4f6300-dd7a-11eb-980b-e9d594c7b909.png)

# 책등록 확인
http GET http://localhost:8081/books
![image](https://user-images.githubusercontent.com/84000898/124405911-d721d780-dd7a-11eb-8704-8d9f59cc4248.png)

```


## 폴리글랏 퍼시스턴스

review 서비스는 Hsql DB로 구현하고, 그와 달리 book,order,delivery 서비스는 h2 DB로 구현하여, MSA간 서로 다른 종류의 DB간에도 문제 없이 동작하여 다형성을 만족하는지 확인하였다.

- book,order,delivery 서비스의 pom.xml 설정

![image](https://user-images.githubusercontent.com/84000863/122320251-ed4b2d80-cf5c-11eb-85a9-e3a43e3e56d2.png)

- review 서비스의 pom.xml 설정

![image](https://user-images.githubusercontent.com/84000863/122320209-ddcbe480-cf5c-11eb-920c-4d3f86cac072.png)


## CQRS

Viewer를 별도로 구현하여 아래와 같이 view가 출력된다.

- myPage 구현

![image](https://user-images.githubusercontent.com/84000898/124431382-3f88ad00-ddab-11eb-9abf-30041e929f42.png)

- 주문 후의 myPage

![image](https://user-images.githubusercontent.com/84000898/124432126-38ae6a00-ddac-11eb-849c-81398d03d536.png)

- 리뷰작성 후의 myPage (리뷰 contents 확인 가능)

![image](https://user-images.githubusercontent.com/84000898/124432161-449a2c00-ddac-11eb-8477-00fb41eb95d1.png)


## 동기식 호출(Req/Resp)

분석단계에서의 조건 중 하나로 주문(order)->책(book)수량 체크&변경 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다. 

- 책수량 체크&변경을 위해서 FeignClient를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현 

```
# (order) BookService.java

package bookshop.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@FeignClient(name="book", url="http://book:8080")
public interface BookService {

    @RequestMapping(method= RequestMethod.GET, path="/chkAndModifyStock")
    public boolean modifyStock(@RequestParam("bookId") Long bookId,
                            @RequestParam("qty") Integer qty);

}
```

- 주문된 직후(@PostPersist) 재고수량이 업데이트 되도록 처리 (modifyStock 호출)
```
# Order.java (Entity)

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
    
```

- 재고수량은 아래와 같은 로직으로 처리
```
public boolean modifyStock(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
                boolean status = false;
                Long bookId = Long.valueOf(request.getParameter("bookId"));
                int qty = Integer.parseInt(request.getParameter("qty"));

                Book book = bookRepository.findByBookId(bookId);

                if(book != null){
                        if (book.getStock() >= qty) {
                                book.setStock(book.getStock() - qty);
                                bookRepository.save(book);
                                status = true;
                        }
                }

                return status;
        }
```

- 동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, book 서비스가 장애가 나면 주문(order)도 못하는 것을 확인:



- book 서비스를 잠시 내려놓음

![image](https://user-images.githubusercontent.com/84000898/124407129-1e5d9780-dd7e-11eb-80dc-ba2e08d2ea9a.png)

-도서주문하기(order)
```
http POST http://localhost:8082/orders buyer=kary title=Parasite qty=1 address=Jeongjadong status=Bought bookId=2
```
< Fail >

![image](https://user-images.githubusercontent.com/84000898/124407239-5bc22500-dd7e-11eb-8647-6814b3dcb7ec.png)


- book 서비스 재기동
```
cd book
mvn spring-boot:run
```

- 도서주문하기(order)
```
http POST http://localhost:8082/orders buyer=kary title=Parasite qty=1 address=Jeongjadong status=Bought bookId=2
```
< Success >

![image](https://user-images.githubusercontent.com/84000898/124407282-798f8a00-dd7e-11eb-8140-e3c0c032e23e.png)



## Gateway 적용

- gateway > applitcation.yml 설정

```
server:
  port: 8088

---

spring:
  profiles: default
  cloud:
    gateway:
      routes:
        - id: book
          uri: http://localhost:8081
          predicates:
            - Path=/books/**, /chkAndModifyStock/**
        - id: order
          uri: http://localhost:8082
          predicates:
            - Path=/orders/** 
        - id: delivery
          uri: http://localhost:8083
          predicates:
            - Path=/deliveries/** 
        - id: mypage
          uri: http://localhost:8084
          predicates:
            - Path= /myPages/**
        - id: review
          uri: http://localhost:8085
          predicates:
            - Path=/reviews/** 
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true


---

spring:
  profiles: docker
  cloud:
    gateway:
      routes:
        - id: book
          uri: http://book:8080
          predicates:
            - Path=/books/**, /chkAndModifyStock/**
        - id: order
          uri: http://order:8080
          predicates:
            - Path=/orders/** 
        - id: delivery
          uri: http://delivery:8080
          predicates:
            - Path=/deliveries/** 
        - id: mypage
          uri: http://mypage:8080
          predicates:
            - Path= /myPages/**
        - id: review
          uri: http://review:8080
          predicates:
            - Path=/reviews/** 
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true

server:
  port: 8080
  
```

- gateway 테스트

```
http GET http://localhost:8081/books
```
![image](https://user-images.githubusercontent.com/84000898/124407779-b4de8880-dd7f-11eb-8867-8e503e8e12a6.png)


## 비동기식 호출(Pub/Sub)

주문(order)이 이루어진 후에 배송업체(delivery)에서 책을 배송하는 행위는 동기식이 아니라 비 동기식으로 처리하여 배송업체(delivery)의 배송처리를 위하여 주문이 블로킹 되지 않도록 처리한다.

- 이를 위하여 책구매완료 되었음을 도메인 이벤트를 카프카로 송출한다(Publish)
 
```
    @PostPersist
    public void onPostPersist() {

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
```

- 배송업체(delivery)에서는 책구매완료(Bought) 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다:

```
package bookshop;

...

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
    
...

```
주문(order) 서비스는 배송업체(delivery) 서비스와 완전히 분리되어있으며(sync transaction 없음) 이벤트 수신에 따라 처리되기 때문에, delivery 서비스가 유지보수로 인해 잠시 내려간 상태라도 예약을 진행해도 문제 없다.(시간적 디커플링) :
  
- 배송업체(delivery) 서비스를 잠시 내려놓고, 도서주문(order)
```
http POST http://localhost:8082/orders buyer=kary title=wonder qty=1 address=Jeongjadong status=Bought bookId=3
```

< Success >

![image](https://user-images.githubusercontent.com/84000898/124421984-84a5e280-dd9d-11eb-9a57-c6a7f06b7aa4.png)


## Deploy / Pipeline

- 소스 git 업로드
```
git add .
git commit -m "commit v7"
git remote add kbookshop https://github.com/kary000/kbookshop.git
git push -u kbookshop master
```
![image](https://user-images.githubusercontent.com/84000898/124507200-4abdf600-de08-11eb-9a10-9670cec5d2ad.png)

- 빌드하기
```
cd book
mvn package 

cd order
mvn package 

cd delivery
mvn package  

cd mapage
mvn package 

cd review
mvn package 

cd gateway
mvn package
```

![image](https://user-images.githubusercontent.com/84000898/124408200-ae044580-dd80-11eb-94c6-c9a58584b0ff.png)

- 도커라이징(Dockerizing) : Azure Container Registry(ACR)에 Docker Image Push하기
```
cd book
az acr build --registry user19skccacr --image user19skccacr.azurecr.io/book:v1

cd order
az acr build --registry user19skccacr --image user19skccacr.azurecr.io/order:v1

cd delivery
az acr build --registry user19skccacr --image user19skccacr.azurecr.io/delivery:v1 

cd mapage
az acr build --registry user19skccacr --image user19skccacr.azurecr.io/mapage:v1

cd review
az acr build --registry user19skccacr --image user19skccacr.azurecr.io/review:v1

cd gateway
az acr build --registry user19skccacr --image user19skccacr.azurecr.io/gateway:v1
```
![image](https://user-images.githubusercontent.com/84000898/124409871-2f110c00-dd84-11eb-8ba7-9a5c6af7bc6b.png)

- 컨테이너라이징(Containerizing) : Deployment 생성
```
cd book
kubectl apply -f kubernetes/deployment.yml

cd order
kubectl apply -f kubernetes/deployment.yml

cd delivery
kubectl apply -f kubernetes/deployment.yml

cd mapage
kubectl apply -f kubernetes/deployment.yml

cd review
kubectl apply -f kubernetes/deployment.yml

cd gateway
kubectl create deploy gateway --image=user19skccacr.azurecr.io/gateway:v1

kubectl get all
```

- 컨테이너라이징(Containerizing) : Service 생성 확인
```
cd book
kubectl apply -f kubernetes/service.yaml

cd order
kubectl apply -f kubernetes/service.yaml

cd delivery
kubectl apply -f kubernetes/service.yaml

cd mapage
kubectl apply -f kubernetes/service.yaml

cd review
kubectl apply -f kubernetes/service.yaml

cd gateway
kubectl expose deploy gateway --type=LoadBalancer --port=8080

kubectl get all
```

![image](https://user-images.githubusercontent.com/84000898/124409718-cc1f7500-dd83-11eb-86c6-a2cb66a97f02.png)


## 서킷 브레이킹(Circuit Breaking)

* 서킷 브레이킹 : Spring FeignClient + Hystrix 옵션을 사용하여 구현함

시나리오는 도서주문 요청이 과도할 경우 Circuit Breaking 을 통하여 장애격리.

: 500밀리세컨초 (0.5초) 내에 주문(order) 호출에 대한 응답이 없다면 시간 초과 에러가 발생하여,  Circuit Breaking 회로가 닫히도록 (요청을 빠르게 실패처리, 차단) 설정

```
# application.yml

feign:
  hystrix:
    enabled: true

hystrix:
  command:
    default:
      execution.isolation.thread.timeoutInMilliseconds: 500

```

* 부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:
- 동시사용자 100명
- 30초 동안 실시

```
$ siege -c100 -t30S -v --content-type "application/json" 'http://order:8080/orders POST {"buyer" : "kary", "title" : "NoMadLand", "qty" : "1", "address" : "Jeongjadong", "status" : "Bought", "boookId" : "1"}'
```

앞서 설정한 부하가 발생하여 Circuit Breaker가 발동, 초반에는 요청 실패처리되었으며
밀린 부하가 order에서 처리되면서 다시 요청을 받기 시작함

- Availability 가 높아진 것을 확인 (siege)

![image](https://user-images.githubusercontent.com/84000898/124447206-1b819780-ddbc-11eb-91f1-af8154cf6bc4.png)



### Autoscale (HPA)
주문(order) 서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 

- autocale out 설정 

![image](https://user-images.githubusercontent.com/84000898/124470515-a66f8b80-ddd6-11eb-80e5-9f8d0fcc53fe.png)

- 상품(product) 서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 1프로를 넘어서면 replica 를 10개까지 늘려준다:
```
kubectl autoscale deploy product --min=1 --max=10 --cpu-percent=1
kubectl get hpa
```
![image](https://user-images.githubusercontent.com/84000898/124458431-ec712300-ddc7-11eb-8132-2d3c8739c255.png)

- CB 에서 했던 방식대로 워크로드를 30초 동안 걸어준다.
```
siege -c100 -t30S -v --content-type "application/json" 'http://order:8080/orders POST {"buyer" : "kary", "title" : "NoMadLand", "qty" : "1", "address" : "Jeongjadong", "status" : "Bought", "bookId" : "1"}'
```

- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어둔다:
```
watch -n 1 kubectl get pod
```

- 어느정도 시간이 흐른 후 스케일 아웃이 벌어지는 것을 확인할 수 있다:

![image](https://user-images.githubusercontent.com/84000898/124523326-99cc5100-de31-11eb-9b03-48ac7594970b.png)


![image](https://user-images.githubusercontent.com/84000898/124458345-cf3c5480-ddc7-11eb-888b-dc8d386c970a.png)


## 무정지 재배포

* 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscaler 이나 서킷브레이커 설정을 제거함

- seige 로 배포작업 직전에 워크로드를 모니터링 함.
```
siege -c10 -t360S -v --content-type "application/json" 'http://book:8080/books POST {"title": "NoMadLand", "author":"nana", "publisher":"cnn", "stock":"10", "bookId":"1"}'
```
- Readiness가 설정되지 않은 yml 파일로 배포 중 버전6에서 버전4로 다운그레이드 시 서비스 요청 처리 실패
```
kubectl set image deploy book book=user19skccacr.azurecr.io/book:v4

```

![image](https://user-images.githubusercontent.com/84000898/124461237-5808bf80-ddcb-11eb-8403-5ffedd445f1e.png)

- deployment.yml에 readiness 옵션을 추가
![image](https://user-images.githubusercontent.com/84000898/124461316-6ce55300-ddcb-11eb-8a1c-98ff270bfba2.png)

- readiness 옵션을 배포 옵션을 설정 한 경우 Availability가 배포기간 동안 변화가 없기 때문에 무정지 재배포가 성공한 것으로 확인됨.

![image](https://user-images.githubusercontent.com/84000898/124462006-34924480-ddcc-11eb-8e34-58cd2f237f5f.png)


## ConfigMap

- order 서비스의 deployment.yml 파일에 아래 항목 추가
```
          env:
            - name: STATUS
              valueFrom:
                configMapKeyRef:
                  name: orderst
                  key: status
```

- Booking 서비스에 configMap 설정 데이터 가져오도록 아래 항목 추가

![image](https://user-images.githubusercontent.com/84000898/124462267-80dd8480-ddcc-11eb-90cb-2f61a6f814a4.png)

- ConfigMap 생성
```
kubectl create configmap orderst --from-literal=status=Bought
kubectl get configmap orderst -o yaml
```

![image](https://user-images.githubusercontent.com/84000898/124462617-dca80d80-ddcc-11eb-99d0-65f933b2c8f9.png)

- ConfigMap 설정 데이터 적용

![image](https://user-images.githubusercontent.com/84000898/124463020-3e687780-ddcd-11eb-898f-b255995e0392.png)



## Self-Healing (Liveness Probe)

- book 서비스의 deployment.yaml에 liveness probe 옵션 추가

![image](https://user-images.githubusercontent.com/84000898/124464834-a029e100-ddcf-11eb-99c1-4b7a12705431.png)
 
- book 서비스에 liveness 적용 확인

![image](https://user-images.githubusercontent.com/84000898/124464916-b9cb2880-ddcf-11eb-9e2e-08da160b4e51.png)

- book 서비스에 liveness가 발동되었고, 포트에 응답이 없기에 Restart가 발생함

![image](https://user-images.githubusercontent.com/84000898/124465102-f7c84c80-ddcf-11eb-8d52-a4c5768a9e23.png)
