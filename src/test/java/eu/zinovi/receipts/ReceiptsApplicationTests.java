package eu.zinovi.receipts;

import eu.zinovi.receipts.util.CloudStorage;
import eu.zinovi.receipts.util.ReceiptProcessApi;
import eu.zinovi.receipts.util.RegisterBGApi;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class ReceiptsApplicationTests {

    @MockBean
    ReceiptProcessApi receiptProcessApi;

    @MockBean
    CloudStorage cloudStorage;

    @MockBean
    RegisterBGApi registerBGApi;

    @Test
    void contextLoads() {
    }

}
