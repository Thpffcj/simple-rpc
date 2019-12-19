package cn.edu.nju.common.util;

import cn.edu.nju.common.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created by thpffcj on 2019/12/16.
 */
public class IpUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(IpUtil.class);

    public static String getHostAddress() {
        String host = null;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
            return host;
        } catch (UnknownHostException e) {
            LOGGER.error("Cannot get server host.", e);
        }
        return host;
    }

    /**
     * 获得真实IP地址
     *
     * @return
     */
    public static String getRealIp() {
        String localIp = null;
        String netIp = null;

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            boolean finded = false;
            InetAddress ip = null;
            while (networkInterfaces.hasMoreElements() && !finded) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = addresses.nextElement();
                    if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() &&
                            ip.getHostAddress().indexOf(":") == -1) {
                        netIp = ip.getHostAddress();
                        finded = true;
                        break;
                    } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() &&
                            ip.getHostAddress().indexOf(":") == -1) {

                        localIp = ip.getHostAddress();
                    }
                }
            }

            if (netIp != null && !"".equals(netIp)) {
                return netIp;
            } else {
                return localIp;
            }
        } catch (SocketException e) {
            throw new RpcException(e);
        }
    }

//    public static void main(String[] args) {
//        System.out.println(IpUtil.getHostAddress());
//        System.out.println(IpUtil.getRealIp());
//    }
}
