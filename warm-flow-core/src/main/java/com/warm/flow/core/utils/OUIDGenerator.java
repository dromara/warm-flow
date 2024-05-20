package com.warm.flow.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 时间顺序的通用唯一标识符
 * @author xiarg
 * @date 2023/12/28 14:31
 */
public class OUIDGenerator {
    protected static final Logger logger = LoggerFactory.getLogger(OUIDGenerator.class);

    public interface Ipv4Provider {
        byte[]  getIp();
    }

    /**
     * 开头时间戳编码表（为了保持有序性，该表即使替换字符集也需保持Ascii有序性）
     */
    final static char[] TIMESTAMP_DIGITS = {
            '0' , '1' , '2' , '3' , '4' , '5' ,
            '6' , '7' , '8' , '9' , 'a' , 'b' ,
            'c' , 'd' , 'e' , 'f' , 'g' , 'h' ,
            'j' , 'k' , 'm' , 'n' , 'p' , 'r' ,
            's' , 't' , 'u' , 'v' , 'w' , 'x' ,
            'y' , 'z'
    };

    /**
     * JVM标识-状态相关字符集
     * <p>该表根据需要随意替换相同数量字符集</p>
     */
    final static char[] JVM_STAT_DIGITS = {
            'a' , 'b' , 'c' , 'd' , 'e' , 'f' ,
            'g' , 'h' , 'j' , 'k' , 'm' , 'n' ,
            'p' , 'r' , 's' , 't' , 'u' , 'v' ,
            'w' , 'x' , 'y' , 'z' , '0' , '1' ,
            '2' , '3' , '4' , '5' , '6' , '7' ,
            '8' , '9'
    };

    /**
     * JVM标识-IP相关字符集
     * <p>如需IP隐秘性，可根据需要随意替换相同数量字符集</p>
     */
    final static char[] JVM_IP_DIGITS = {
            'a' , 'b' , 'c' , 'd' , 'e' , 'f' ,
            '0' , '1' , '2' , '3' , '4' , '5' ,
            '6' , '7' , '8' , '9' , 'g' , 'h' ,
            'j' , 'k' , 'm' , 'n' , 'p' , 'r' ,
            's' , 't' , 'u' , 'v' , 'w' , 'x' ,
            'y' , 'z'
    };

    /**
     * IP位替换
     * <p>如需IP隐秘性，可根据需要随意替换相同数量字符集</p>
     * <p>由于私网IP网段有限，容易通过确定的网段结合统计攻击猜测IP，可设置替换特定IP位增加猜测难度</p>
     */
    private final static Byte[] REWRITE_IP_SEGMENT = { (byte) 112, null, null, null};

    /**
     * 生成有顺序的uuid
     */
    public static String generate() {
        return getTime(TIMESTAMP_DIGITS) +
                JVM_ID +
                getSerial();
    }

    private static String getTime(char[] digits) {
        long currentTimeMillis = System.currentTimeMillis();
        // 10位32进制，足以表示以毫秒计的3万年时间（当前系统时间肯定大于1970年，因此直接省略符号位处理）
        return format32(digits, currentTimeMillis, 10);
    }

    private static String format32(char[] digit, long val, int len) {
        char[] chars = new char[len];
        for (int i = chars.length - 1; i >= 0; i--) {
            // 获取字节的低5位有效值
            int j = (int) (val & 0x1f);
            chars[i] = digit[j];
            val = val >> 5;
        }
        return new String(chars);
    }

    public static final String JVM_ID = initJvm();

    /**
     * spi机制获取IP
     */
    private static byte[] getIpBySpi() {
        byte[] address = null;
        ServiceLoader<Ipv4Provider> providerServiceLoader = ServiceLoader.load(Ipv4Provider.class);
        Iterator<Ipv4Provider> iterator = providerServiceLoader.iterator();
        if (iterator.hasNext()) {
            // SPI 机制获取IP
            Ipv4Provider provider = iterator.next();
            address = provider.getIp();
            if (address != null && address.length != 4) {
                IllegalArgumentException illegalArgumentException =  new IllegalArgumentException("ipv4 provider not provide ipv4 address");
                logger.error(illegalArgumentException.getMessage(),illegalArgumentException);
                return null;
            }
        }
        return address;
    }

    /**
     * 从环境变量获取重写的IP位
     */
    private static Byte[] getRewriteIpSegmentsByEnv() {
        Byte[] rewriteIpBytes = null;
        String ouidRewriteIp = System.getenv("OUID_REWRITE_IP");
        if (ouidRewriteIp != null) {
            String[] split = ouidRewriteIp.split("\\.");
            rewriteIpBytes = new Byte[4];
            try {
                for (int i = 0; i < 4; i++) {
                    if ("%".equals(split[i])) {
                        continue;
                    }
                    int num = Integer.parseInt(split[i]);
                    if (num > 255 || num < 0) {
                        throw new IllegalArgumentException("invalid rewrite ipv4 address " + ouidRewriteIp);
                    }
                    rewriteIpBytes[i] = (byte) (num > 127 ? num - 256 : num);
                }
            } catch (Exception e) {
                logger.error( e.getMessage(), e);
                rewriteIpBytes = null;
            }
        }
        return rewriteIpBytes;
    }

    @SuppressWarnings({"ConstantConditions", "RedundantSuppression"})
    private static String initJvm() {
        long ipAddr;
        try {

            // spi机制获取IP
            byte[] address = getIpBySpi();
            if (address == null) {
                address = InetAddress.getLocalHost().getAddress();
            }

            // 从环境变量获取替换IP
            Byte[] rewriteIpSegments = getRewriteIpSegmentsByEnv();
            if (rewriteIpSegments == null) {
                rewriteIpSegments = REWRITE_IP_SEGMENT;
            }

            // 替换特定位IP
            for (int i = 0; i < address.length; i++) {
                if (rewriteIpSegments.length > i && rewriteIpSegments[i] != null) {
                    address[i] = rewriteIpSegments[i];
                }
            }

            ipAddr = toLong(address);
        } catch (Exception e) {
            logger.error( e.getMessage(), e);
            ipAddr = 0;
        }

        return format32(JVM_IP_DIGITS, ipAddr, 7) + getTime(JVM_STAT_DIGITS);
    }

    private static final AtomicInteger SEQ = new AtomicInteger((int) (Math.random() * Integer.MAX_VALUE / 1000));

    private static String getSerial() {
        long serial = SEQ.incrementAndGet() & 0x1ffffff;
        // 单实例每毫秒最大允许产生 33554431 个ID，
        // MAC i7 4核下3线程测试每毫秒产生ID数在1.9万左右，故该容量导致ID重复的概率几乎为0
        return format32(TIMESTAMP_DIGITS, serial, 5);
    }

    private static long toLong(byte[] bytes) {
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) + (0xff & bytes[i]);
        }
        return result;
    }

}