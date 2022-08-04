package eu.zinovi.receipts.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import eu.zinovi.receipts.domain.model.entity.*;
import eu.zinovi.receipts.domain.model.service.ReceiptPolyJsonServiceModel;
import eu.zinovi.receipts.domain.exception.ReceiptProcessException;
import eu.zinovi.receipts.repository.ReceiptImageRepository;
import eu.zinovi.receipts.repository.ReceiptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ReceiptProcessService {
    private final UserService userService;
    private final MessagingService messagingService;
    private final CompanyService companyService;
    private final StoreService storeService;
    private final CategoryService categoryService;
    private final ItemService itemService;
    private final ReceiptImageRepository receiptImageRepository;
    private final ReceiptRepository receiptRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final Gson gson;

    public ReceiptProcessService(UserService userService, MessagingService messagingService, CompanyService companyService, StoreService storeService, CategoryService categoryService, ItemService itemService, ReceiptImageRepository receiptImageRepository, ReceiptRepository receiptRepository, Gson gson) {
        this.userService = userService;
        this.messagingService = messagingService;
        this.companyService = companyService;
        this.storeService = storeService;
        this.categoryService = categoryService;
        this.itemService = itemService;
        this.receiptImageRepository = receiptImageRepository;
        this.receiptRepository = receiptRepository;
        this.gson = gson;
    }

    @Transactional
    public UUID parseReceipt(String processedMLJson, ReceiptImage receiptImage,
                             String fileName, String qrCode) {

        Receipt receipt = new Receipt();
        receipt.setReceiptImage(receiptImage);
        receipt.setAnalyzed(false);
        receipt.setUser(userService.getCurrentUser());

        // Read the QR code
        boolean hasQRCode = qrCode != null;
        boolean totalFound = hasQRCode;

        if (hasQRCode) {
            LocalDateTime qrReceiptDate = LocalDateTime.parse(
                    qrCode.split("\\*")[2] + " " + qrCode.split("\\*")[3],
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            BigDecimal qrTotal = new BigDecimal(qrCode.split("\\*")[4]);
            receipt.setDateOfPurchase(qrReceiptDate);
            receipt.setTotal(qrTotal);
        }

        // Get receipt lines from mlPolyJson
        List<String> receiptLines = mlPolyJsonToReceiptLines(processedMLJson);
        StringBuilder receiptText = new StringBuilder();
        for (String line : receiptLines) {
            receiptText.append(line).append("\n");
        }
        receipt.setReceiptLines(receiptText.toString());
//        receipt.setDateOfPurchase(LocalDateTime.now());
        receiptRepository.save(receipt);
        receiptImage.setReceipt(receipt);
        receiptImageRepository.save(receiptImage);

        // Find EIK, store name and store address :

        String eik = null;
        int itemListStartIndex = 0;
        String storeName = "";
        String storeAddress = "";
        Pattern eikPattern = Pattern.compile("\\d{9}");
        for (int i = 0; i < Math.min(receiptLines.size(), 10); i++) {

     /*
            if (Pattern.compile("ЕИК|EUK").matcher(receiptLines.get(i)).find()) {
                String eikString = receiptLines.get(i)
                        .replace("ЕИК", "")
                        .replace("EUK", "")
                        .replace(":", "")
                        .replace(" ", "")
                        .trim();
             */
            Matcher matcher = eikPattern.matcher(receiptLines.get(i));
            if (matcher.find()) {
                eik = matcher.group();
                storeName = receiptLines.get(i + 1);
                storeAddress = receiptLines.get(i + 2);
                itemListStartIndex = i + 3;
                break;
            }

                    /*
                storeName = receiptLines.get(i + 1);
                storeAddress = receiptLines.get(i + 2);
                itemListStartIndex = i + 3;
                break;
            }
*/
        }
        if (eik == null) {
            throw new ReceiptProcessException("ЕИК не е разчетен!");
        }
        Company company = companyService.findByEik(eik);
        if (company == null) {
            throw new ReceiptProcessException("Фирмата не е разчетена!");
        }
        receipt.setCompany(company);

        receipt.setStore(storeService.findByNameAndAddressAndCompany(storeName, storeAddress, company));

        receiptRepository.save(receipt);

        // Find total and date
        BigDecimal total = null;
        int itemListEndIndex = receiptLines.size();

        LocalDate date = null;
        LocalTime time = null;

        Pattern datePattern = Pattern.compile("\\d\\d[-.]\\d\\d[-.]\\d{4,4}");
        Pattern timePattern = Pattern.compile("^\\d\\d:\\d\\d:\\d\\d");
        Pattern costPattern = Pattern.compile("-{0,1}\\d+[.|,]\\d\\d$");
        Pattern quantityPattern = Pattern.compile("\\d+[.|,]\\d\\d\\d");

        for (int i = receiptLines.size() - 1; i > itemListStartIndex; i--) {
            String currentLine = cyrillizeSymbols(
                    stripRecipeLine(receiptLines.get(i)));

            if (currentLine.toLowerCase().contains("обща") && currentLine.toLowerCase().contains("сума")) {
                itemListEndIndex = i;
                Matcher matcher = costPattern.matcher(currentLine);
                if (matcher.find()) {
                    try {
                        total = new BigDecimal(matcher.group().replace(",", "."));
                        totalFound = true;
                    } catch (NumberFormatException e) {
                        LOGGER.error("Error parsing total!");
                    }
                    if (!hasQRCode) {
                        receipt.setTotal(total);
                    }
                    break;
                }
            }
            if (!hasQRCode) {
                Matcher matcher = datePattern.matcher(currentLine);
                if (matcher.find()) {
                    try {
                        date = LocalDate.parse(matcher.group().replace(".", "-"),
                                DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    } catch (DateTimeParseException e) {
                        LOGGER.error("Error parsing date: " + matcher.group());
                    }
                }
                matcher = timePattern.matcher(currentLine);
                if (matcher.find()) {
                    try {
                        time = LocalTime.parse(matcher.group(), DateTimeFormatter.ofPattern("HH:mm:ss"));
                    } catch (DateTimeParseException e) {
                        LOGGER.error("Error parsing time: " + matcher.group());
                    }
                }
            }
            if (receipt.getDateOfPurchase() == null) {
                receipt.setDateOfPurchase(LocalDateTime.of(date != null ? date : LocalDate.now(),
                        time != null ? time : LocalTime.now()));
            }
        }

        if (!totalFound) {
            throw new ReceiptProcessException("Сумата не е разчетена!");
        }

        List<Item> items = new ArrayList<>();

        // TODO: Find items and their prices

        Matcher matcher;

        for (int i = itemListStartIndex; i < itemListEndIndex; i++) {
            String currentLine = stripRecipeLine(receiptLines.get(i));

            String nextLine;
            if (itemListEndIndex > i + 1) {
                nextLine = stripRecipeLine(receiptLines.get(i + 1));
            } else {
                nextLine = "";
            }
            String secondToNextLine;
            if (itemListEndIndex > i + 2) {
                secondToNextLine = stripRecipeLine(receiptLines.get(i + 2));
            } else {
                secondToNextLine = "";
            }


            // Quantity check
            matcher = quantityPattern.matcher(currentLine);
            if (matcher.find()) {
                BigDecimal quantity = new BigDecimal(matcher.group().replace(',', '.'))
                        .setScale(3, RoundingMode.HALF_UP);
                matcher = costPattern.matcher(currentLine.substring(matcher.end()).trim()
                        .replace(" ", "")
                        .replace("=", "")
                        .replace("x", ""));
                if (matcher.find()) {
                    BigDecimal pricePerUnit = new BigDecimal(matcher.group().replace(",", "."));
                    BigDecimal price = pricePerUnit.multiply(quantity)
                            .setScale(2, RoundingMode.HALF_EVEN);
                    if (!items.isEmpty()) {
                        if (items.get(items.size() - 1).getPrice().compareTo(price) == 0) {
                            items.get(items.size() - 1).setQuantity(quantity);
                            continue;
                        }
                    }
                    matcher = costPattern.matcher(nextLine);
                    if (matcher.find()) {
                        BigDecimal currentPrice = new BigDecimal(matcher.group().replace(",", "."));
                        Item item = new Item();
                        item.setQuantity(quantity);
                        item.setPrice(currentPrice);
                        items.add(item);
                        i++;

                    } else {
                        matcher = costPattern.matcher(secondToNextLine);
                        if (matcher.find()) {
                            BigDecimal currentPrice = new BigDecimal(matcher.group().replace(",", "."));
                            Item item = new Item();
                            item.setQuantity(quantity);
                            item.setPrice(currentPrice);
                            items.add(item);
                            i += 2;
                        }
                    }
                }
            } else {

                // item check
                matcher = costPattern.matcher(currentLine);
                if (matcher.find()) {
                    BigDecimal currentPrice = new BigDecimal(matcher.group().replace(",", "."));
                    Item item = new Item();
                    item.setPrice(currentPrice);
                    item.setQuantity(BigDecimal.ONE);
                    items.add(item);
                }
            }
        }

        BigDecimal itemsTotal = BigDecimal.ZERO;
        Category other = categoryService.findByName("Други").orElse(null);
        for (Item item : items) {
            itemsTotal = itemsTotal.add(item.getPrice());
//            item.setName("");
            item.setCategory(other);
        }

        // ItemsTotal cleanup
        // fix for a strange case where total is null
        while (receipt.getTotal() != null && itemsTotal.compareTo(receipt.getTotal()) > 0) {
            Item item = items.get(items.size() - 1);
            itemsTotal = itemsTotal.subtract(item.getPrice());
            items.remove(items.size() - 1);
        }


        receipt.setItemsTotal(itemsTotal);
//        receipt.setAnalyzed(true);
        receiptRepository.save(receipt);

        for (int i = 0; i < items.size(); i++) {
            items.get(i).setReceipt(receipt);
            items.get(i).setPosition(i + 1);
            itemService.save(items.get(i));
        }

        receiptImage.setIsProcessed(true);
        receiptImage.setReceipt(receipt);
        receiptImageRepository.save(receiptImage);
        messagingService.sendMessage(fileName + ": " +
                        receipt.getTotal() + "лв. - " +
                        company.getName() + " - " +
                        receipt.getDateOfPurchase()
                                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                "success");
        return receipt.getId();
    }

    public List<String> mlPolyJsonToReceiptLines(String processedMLJson) {
        List<ReceiptPolyJsonServiceModel> polygons = gson.fromJson(processedMLJson,
                new TypeToken<ArrayList<ReceiptPolyJsonServiceModel>>() {
                }.getType());

        polygons.sort((a, b) -> {
            double height = a.getY3() - a.getY1();
            double delta = Math.abs(a.getY3() - b.getY3());


            if (delta > height / 2) {
                return a.getY1() - b.getY1();

            }
            return a.getX1() - b.getX1();
        });

        List<String> receiptLines = new ArrayList<>();

        StringBuilder line = new StringBuilder();
//        if (polygons.size() > 0) {
//            line.append(polygons.get(0).getText());
//        }
        for (int i = 1; i < polygons.size() - 1; i++) {
            ReceiptPolyJsonServiceModel last = polygons.get(i - 1);
            ReceiptPolyJsonServiceModel current = polygons.get(i);

            double currentRelativeCenter = (current.getY3() - current.getY1()) / (double) 2;
            double currentCenter = current.getY1() + currentRelativeCenter;
            if (currentCenter > last.getY3()) {
                if (line.length() > 0) {
                    receiptLines.add(line.toString());
                    line = new StringBuilder();
                }
            }
            line.append(current.getText()).append(" ");
        }
        if (line.length() > 0) {
            receiptLines.add(line.toString());
        }
        return receiptLines;
    }

    private String stripRecipeLine(String line) {
        line = line.trim();
        if (line.startsWith("# ") ||
                line.startsWith("H ")) {
            line = line.substring(2);
        }
        if (line.endsWith(" #") ||
                line.endsWith(" Б") ||
                line.endsWith(" б") ||
                line.endsWith(" T") ||
                line.endsWith(" Т") ||
                line.endsWith(" Г") ||
                line.endsWith(" г") ||
                line.endsWith(" r") ||
                line.endsWith(" 5") ||
                line.endsWith(" Н") ||
                line.endsWith(" Е") ||
                line.endsWith(" E") ||
                line.endsWith(" СЕ") ||
                line.endsWith(" 6")) {
            line = line.substring(0, line.length() - 2);
        }
        return line.trim();
    }

    private String cyrillizeSymbols(String stripRecipeLine) {
        return stripRecipeLine.replace("A", "А")
                .replace("a", "а")
                .replace("O", "О")
                .replace("o", "о")
                .replace("y", "у")
                .replace("Y", "У");
    }
}
