package eu.zinovi.receipts.service;

import eu.zinovi.receipts.WithMockEmailUser;
import eu.zinovi.receipts.domain.model.entity.Receipt;
import eu.zinovi.receipts.domain.model.entity.ReceiptImage;
import eu.zinovi.receipts.domain.model.service.CompanyRegisterBGApiServiceModel;
import eu.zinovi.receipts.repository.ReceiptImageRepository;
import eu.zinovi.receipts.repository.ReceiptRepository;
import eu.zinovi.receipts.service.impl.UserServiceImpl;
import eu.zinovi.receipts.util.CloudStorage;
import eu.zinovi.receipts.util.ReceiptProcessApi;
import eu.zinovi.receipts.util.RegisterBGApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.transaction.Transactional;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Transactional
@SpringBootTest
//@ActiveProfiles("test")
public class ReceiptProcessServiceImplIT {

    @Autowired
    private ReceiptProcessService receiptProcessService;

    @Autowired
    private ReceiptImageRepository receiptImageRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private UserServiceImpl userServiceImpl;

    private ReceiptImage receiptImage;
    private String processedMLJson;

    @MockBean
    ReceiptProcessApi receiptProcessApi;

    @MockBean
    CloudStorage cloudStorage;

    @MockBean
    RegisterBGApi registerBGApi;

    @BeforeEach
    public void setUp() throws Exception {
        receiptImage = new ReceiptImage();
        receiptImage.setIsProcessed(false);
        receiptImage.setImageUrl("http://localhost:8080/images/receipts/1.jpg");
        receiptImage.setAddedOn(LocalDateTime.of(2022, 11, 11, 0, 0, 0));
        receiptImage.setUser(userServiceImpl.getCurrentUser());
        receiptImageRepository.save(receiptImage);

        InputStream is = this.getClass().getResourceAsStream(
                "/receipts/poly/7f625f00-6c09-4e9c-a425-0acb9c495816.json");
        processedMLJson = new String(is.readAllBytes());
        is.close();

        Mockito.when(registerBGApi.getCompanyInfo(Mockito.anyString()))
                .thenReturn(new CompanyRegisterBGApiServiceModel(
                        "Фирма", "Дейност", "Адрес"));
    }

    @Test
    @WithMockEmailUser
    public void testParseReceiptTotal() {
        UUID receiptId = receiptProcessService.parseReceipt
                (processedMLJson, receiptImage, "file.jpg", null);

        Receipt receipt = receiptRepository.findById(receiptId).orElseThrow();

        Assertions.assertEquals(receipt.getTotal(), new BigDecimal("17.26"));
    }

    @Test
    @WithMockEmailUser
    public void testParseItemsTotal() {
        UUID receiptId = receiptProcessService.parseReceipt
                (processedMLJson, receiptImage, "file.jpg", null);

        Receipt receipt = receiptRepository.findById(receiptId).orElseThrow();

        Assertions.assertEquals(receipt.getItemsTotal(), new BigDecimal("17.26"));
    }

    @Test
    @WithMockEmailUser
    public void testParseItemsCount() {
        UUID receiptId = receiptProcessService.parseReceipt
                (processedMLJson, receiptImage, "file.jpg", null);

        Receipt receipt = receiptRepository.findById(receiptId).orElseThrow();

        Assertions.assertEquals(5, receipt.getItems().size());
    }

    @Test
    @WithMockEmailUser
    public void testParseReceiptDate() {
        UUID receiptId = receiptProcessService.parseReceipt
                (processedMLJson, receiptImage, "file.jpg", null);

        Receipt receipt = receiptRepository.findById(receiptId).orElseThrow();

        Assertions.assertEquals(receipt.getDateOfPurchase(),
                LocalDateTime.of(2022, 7, 4, 8, 15, 56));
    }

    @Test
    @WithMockEmailUser
    public void testProcessReceiptLines() {

        UUID receiptId = receiptProcessService.parseReceipt
                (processedMLJson, receiptImage, "file.jpg", null);

        Receipt receipt = receiptRepository.findById(receiptId).orElseThrow();

        //Should work on the polygon arrangement algorithm....
        Assertions.assertEquals(receipt.getReceiptLines(),
                """
                        коне\s
                        БН\s
                        ЕВРОПА - ВН ООД\s
                        ГР.СОФИЯ , УЛ.МОМИНА\s
                        ЕИК : СЪЛЗА 14 А\s
                        831524037\s
                        СУПЕРМАРКЕТ ФАНТАСТИКО\s
                        ГР.СОФИЯ , БУЛ.ЧЕРНИ ВРЪХ 20\s
                        ЗДДС N : BG831524037 ОНБТБРепто\s
                        00001 МАЯ РАДОЕВА ИВАНОВА # 01 -\s
                        нКаса : 1 Бон : 23 Нул : 1480\s
                        нид . No : 2336414 II\s
                        0,886 x 1,79\s
                        ЯБЪЛКИ ЧЕРВЕНИ П - Д ПОЛША 1,59 6\s
                        * ЕКЛЕРИ МИНИ 500 ГР МАЯ - 5 6,79 Б\s
                        * ЕКЛЕРИ МИНИ 500 ГР МАЯ - 5 6,79 Б\s
                        ТОРБИЧКИ БИО РАЗГРАДИМИ 0,30 Б\s
                        АКТИВИЯ ЗАКУСКА 198 ГР 1,79 Б\s
                        МЕЖДИННА СУМА 17,26\s
                        ОБЩА СУМА 17,26\s
                        В БРОЙ ИВ 20,00\s
                        РЕСТО ИВ 2,74\s
                        I Благодарим Ви за покупката ! It\s
                        I Заповядайте отново ! IL\s
                        :\s
                        БЛАГОДАРИМ ВИ\s
                        FANTASTICO FF\s
                        5 АРТИКУЛА\s
                        0349275 0823 04-07-2022 08:15:56\s
                        ОКО\s
                        DOWN\s
                        BG ФИСКАЛЕН БОН\s
                        DT278901 02278901\s
                        DDB4C75AC 1BABD5C392076B742DBCАЗЕ\s                                             
                        """);
    }

}
