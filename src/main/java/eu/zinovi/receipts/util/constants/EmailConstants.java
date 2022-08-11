package eu.zinovi.receipts.util.constants;

public class EmailConstants {
    public static final String EMAIL_SUBJECT = "Потвърдете вашата електронна поща";
    public static final String EMAIL_FROM = "Бележки.бг";
    public static final String EMAIL_BODY = """
            <p> Моля потвърдете вашата електронна поща </p>
            <p> <a href="%s"> %s </a> </p>
            """;
}
