package de.elbe5.cms.application;

import de.elbe5.cms.page.PagePartFlexClass;

public class Statics {

    public static final String KEY_JSP = "$JSP";
    public static final String KEY_REQUESTDATA = "$REQUESTDATA";
    public static final String KEY_URL = "$URL";
    public static final String KEY_MESSAGE = "$MESSAGE";
    public static final String KEY_MESSAGETYPE = "$MESSAGETYPE";
    public static final String KEY_TARGETID = "$TARGETID";

    public static final String KEY_TITLE = "$TITLE";
    public static final String KEY_LOGIN = "$LOGIN";
    public static final String KEY_LOCALE = "$LOCALE";
    public static final String KEY_CAPTCHA = "$CAPTCHA";
    public static final String KEY_HOST = "$HOST";
    public static final String KEY_EDITMODE = "$EDITMODE";
    public static final String KEY_PAGE = "pageData";
    public static final String KEY_PART = "partData";

    public static final String MESSAGE_TYPE_NONE ="";
    public static final String MESSAGE_TYPE_INFO ="info";
    public static final String MESSAGE_TYPE_SUCCESS ="success";
    public static final String MESSAGE_TYPE_ERROR ="danger";

    public static String MODAL_DIALOG_ID = "modalDialog";
    public static String MODAL_DIALOG_JQID = "#"+MODAL_DIALOG_ID;



    public static String ENCODING = "UTF-8";
    public static String PAGE_CONTAINER_ID = "pageContainer";
    public static String PAGE_CONTAINER_JQID = "#"+PAGE_CONTAINER_ID;
    public static String PAGE_CONTENT_ID = "pageContent";
    public static String PAGE_CONTENT_JQID = "#"+PAGE_CONTENT_ID;
    public static PagePartFlexClass DEFAULT_CLASS= PagePartFlexClass.COL12;

    public static String getTypeKey(String msgType){
        switch (msgType){
            case MESSAGE_TYPE_INFO: return Strings._info.toString();
            case MESSAGE_TYPE_SUCCESS: return Strings._success.toString();
            case MESSAGE_TYPE_ERROR: return Strings._error.toString();
            default: return "";
        }
    }
}
