
package bookshop.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

//@FeignClient(name="book", url="http://localhost:8081")
@FeignClient(name="book", url="http://book:8080")
public interface BookService {

    @RequestMapping(method= RequestMethod.GET, path="/chkAndModifyStock")
    public boolean modifyStock(@RequestParam("bookId") Long bookId,
                            @RequestParam("qty") Integer qty);

}