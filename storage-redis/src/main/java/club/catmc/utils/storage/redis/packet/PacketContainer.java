package club.catmc.utils.storage.redis.packet;

public class PacketContainer {

    private final String className;
    private final String data;

    public PacketContainer(String className, String data) {
        this.className = className;
        this.data = data;
    }

    public String getClassName() {
        return className;
    }

    public String getData() {
        return data;
    }
}