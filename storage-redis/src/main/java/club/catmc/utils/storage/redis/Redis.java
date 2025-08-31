package club.catmc.utils.storage.redis;

import club.catmc.utils.storage.redis.annotations.RedisListener;
import club.catmc.utils.storage.redis.annotations.RedisSender;
import club.catmc.utils.storage.redis.packet.Packet;
import club.catmc.utils.storage.redis.packet.PacketContainer;
import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;

public class Redis {

    private String host;
    private int port;
    private String uri;
    private final Gson gson = new Gson();
    private Jedis jedis;

    public Redis(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Redis(String uri) {
        this.uri = uri;
    }

    public void init() {
        if (uri != null) {
            this.jedis = new Jedis(URI.create(uri));
        } else {
            this.jedis = new Jedis(host, port);
        }
    }

    public void start() {
        // The Jedis client is already connected in the init method.
    }

    public void reload() {
        end();
        init();
        start();
    }

    public void end() {
        if (this.jedis != null) {
            this.jedis.close();
        }
    }

    public Jedis getJedis() {
        return jedis;
    }

    public void publish(String channel, Packet packet) {
        try (Jedis publisher = new Jedis(host, port)) {
            String className = packet.getClass().getName();
            String data = gson.toJson(packet);
            PacketContainer container = new PacketContainer(className, data);
            publisher.publish(channel, gson.toJson(container));
        }
    }

    public void registerListener(Object listener) {
        for (Method method : listener.getClass().getMethods()) {
            if (method.isAnnotationPresent(RedisListener.class)) {
                if (method.getParameterCount() != 1 || !Packet.class.isAssignableFrom(method.getParameterTypes()[0])) {
                    continue;
                }
                RedisListener annotation = method.getAnnotation(RedisListener.class);
                String channel = annotation.channel();
                Class<?> packetType = method.getParameterTypes()[0];

                Thread thread = new Thread(() -> {
                    try (Jedis subscriber = new Jedis(host, port)) {
                        subscriber.subscribe(new JedisPubSub() {
                            @Override
                            public void onMessage(String ch, String message) {
                                try {
                                    PacketContainer container = gson.fromJson(message, PacketContainer.class);
                                    Class<?> receivedPacketClass = Class.forName(container.getClassName());

                                    if (packetType.isAssignableFrom(receivedPacketClass)) {
                                        Packet packet = (Packet) gson.fromJson(container.getData(), receivedPacketClass);
                                        method.invoke(listener, packet);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, channel);
                    }
                });
                thread.setDaemon(true);
                thread.start();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T createSender(Class<T> senderInterface) {
        if (!senderInterface.isInterface()) {
            throw new IllegalArgumentException("senderInterface must be an interface.");
        }
        return (T) Proxy.newProxyInstance(senderInterface.getClassLoader(), new Class[]{senderInterface}, (proxy, method, args) -> {
            if (method.getDeclaringClass().equals(Object.class)) {
                return method.invoke(this, args);
            }
            if (method.isAnnotationPresent(RedisSender.class)) {
                if (args == null || args.length != 1 || !(args[0] instanceof Packet)) {
                    throw new IllegalArgumentException("Method annotated with @RedisSender must have a single Packet parameter.");
                }
                RedisSender annotation = method.getAnnotation(RedisSender.class);
                String channel = annotation.channel();
                Packet packet = (Packet) args[0];
                publish(channel, packet);
                return null;
            }
            throw new UnsupportedOperationException("Only methods annotated with @RedisSender are supported on this interface.");
        });
    }
}