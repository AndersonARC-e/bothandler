package data;

public enum BanReason {
    USER_DELETED(18),BLACKLISTED(900),GROUP_NOT_ALLOWED(901),MESSAGES_NOT_ALLOWED(902),GROUP_MESSAGES_OFF(915),GROUP_MESSAGES_BAN(916),CHAT_BAN(917),EMAIL_BAN(918),GROUP_BAN(203);

    private int type;

    BanReason (int type) {

    }

    public int asInt(){
        return type;
    }
}
