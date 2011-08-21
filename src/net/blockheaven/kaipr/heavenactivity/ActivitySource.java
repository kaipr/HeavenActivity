package net.blockheaven.kaipr.heavenactivity;

public enum ActivitySource {
    MOVE,
    COMMAND,
    COMMAND_CHAR,
    CHAT,
    CHAT_CHAR,
    BLOCK_PLACE,
    BLOCK_BREAK;
    
    public static ActivitySource parseActivitySource(String string) {
        try {
            return valueOf(string.toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
