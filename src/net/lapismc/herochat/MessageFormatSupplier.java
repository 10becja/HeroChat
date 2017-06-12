package net.lapismc.herochat;

public interface MessageFormatSupplier {
    String getStandardFormat();

    String getConversationFormat();

    String getAnnounceFormat();

    String getEmoteFormat();
}
