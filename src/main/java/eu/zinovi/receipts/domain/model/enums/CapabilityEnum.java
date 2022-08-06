package eu.zinovi.receipts.domain.model.enums;

public enum CapabilityEnum {
    CAP_ADMIN( "Админ права"),
    CAP_ADMIN_LIST_RECEIPTS( "Админ списък бележки"),
    CAP_ADMIN_VIEW_RECEIPT( "Админ разглеждане бележка"),

    CAP_ADD_USER("Добавяне потребител"),
    CAP_VIEW_USER_DETAILS("Разглеждане потребителски детайли"),
    CAP_LIST_USERS("Списък потребители"),
    CAP_EDIT_USER("Редакция потребител"),
    CAP_EDIT_ALL_USERS("Редакция всички потребители"),
    CAP_DELETE_USER("Изтриване потребител"),

    CAP_ADD_ROLE("Добавяне роля"),
    CAP_LIST_ROLES("Списък роли"),
    CAP_EDIT_ROLE("Редакция роля"),
    CAP_DELETE_ROLE("Изтриване роля"),

    CAP_LIST_CAPABILITIES("Списък права"),
    CAP_EDIT_CAPABILITY("Редакция права"),

    CAP_LIST_CATEGORIES("Списък категории"),
    CAP_ADD_CATEGORY("Добавяне категория"),
    CAP_EDIT_CATEGORY("Редакция категория"),
    CAP_DELETE_CATEGORY("Изтриване категория"),

    CAP_VIEW_HOME("Разглеждане на главната страница"),

    CAP_ADD_RECEIPT("Добавяне бележка"),
    CAP_VIEW_RECEIPT("Разглеждане бележка"),
    CAP_EDIT_RECEIPT("Редакция бележка"),
    CAP_LIST_RECEIPTS("Списък бележки"),
    CAP_LIST_ALL_RECEIPTS("Списък всички бележки"),
    CAP_DELETE_RECEIPT("Изтрриване бележка"),

    CAP_DELETE_ITEM("Изтриване артикул"),
    CAP_ADD_ITEM("Добавяне артикул"),
    CAP_EDIT_ITEM("Редакция артикул"),
    CAP_LIST_ITEMS("Списък артикули"),

    CAP_CHANGE_PASSWORD("Промяна парола");


    private final String description;

    CapabilityEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}
