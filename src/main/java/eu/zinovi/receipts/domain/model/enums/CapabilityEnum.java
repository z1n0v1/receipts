package eu.zinovi.receipts.domain.model.enums;

public enum CapabilityEnum {
    CAP_ADMIN( "Admin privilege"),
    CAP_ADMIN_LIST_RECEIPTS( "Admin list receipts"),
    CAP_ADMIN_VIEW_RECEIPT( "Admin view receipt"),

    CAP_ADD_USER("Add user"),
    CAP_VIEW_USER_DETAILS("View user details"),
    CAP_LIST_USERS("List users"),
    CAP_EDIT_USER("Edit user"),
    CAP_EDIT_ALL_USERS("Edit all users"),
    CAP_DELETE_USER("Delete user"),

    CAP_ADD_ROLE("Add role"),
    CAP_LIST_ROLES("List roles"),
    CAP_EDIT_ROLE("Edit role"),
    CAP_DELETE_ROLE("Delete role"),

    CAP_LIST_CAPABILITIES("List capabilities"),
    CAP_EDIT_CAPABILITY("Edit capability"),

    CAP_LIST_CATEGORIES("List categories"),
    CAP_ADD_CATEGORY("Add category"),
    CAP_EDIT_CATEGORY("Edit category"),
    CAP_DELETE_CATEGORY("Delete category"),

    CAP_VIEW_HOME("View home"),

    CAP_ADD_RECEIPT("Add receipt"),
    CAP_VIEW_RECEIPT("View receipt"),
    CAP_EDIT_RECEIPT("Edit receipt"),
    CAP_LIST_RECEIPTS("List receipts"),
    CAP_LIST_ALL_RECEIPTS("List all receipts"),
    CAP_DELETE_RECEIPT("Delete receipt"),

    CAP_DELETE_ITEM("Delete item"),
    CAP_ADD_ITEM("Add item"),
    CAP_EDIT_ITEM("Edit item"),
    CAP_LIST_ITEMS("List items"),

    CAP_CHANGE_PASSWORD("Change password");


    private final String description;

    CapabilityEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}
