package com.common.tools.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by udbwcso on 2016/3/15.
 */
public class FTPClientUtil {

    private static final String FTP_PROPERTIES = "/ftp.properties";

    private FTPClientUtil(){
    }

    public static FTPClientUtil getInstance(){
        return SingletonHolder.instance;
    }

    /**
     * Returns the pathname of the current working directory.
     * @return
     */
    public String getWorkingDirectory() throws Exception {
        FTPClient client = getClientPool().borrowObject();
        String workingDir = client.printWorkingDirectory();
        getClientPool().returnObject(client);
        return workingDir;
    }

    private ObjectPool<FTPClient> getClientPool(){
        return SingletonHolder.POOL;
    }

    private static class SingletonHolder {

        private static final ObjectPool<FTPClient> POOL;

        static {
            InputStream resourceAsStream = FTPClientUtil.class.getResourceAsStream(FTP_PROPERTIES);
            Properties p = null;
            GenericObjectPoolConfig config = new GenericObjectPoolConfig();
            if (resourceAsStream != null) {
                p = new Properties();
                try {
                    p.load(resourceAsStream);
                    config.setTimeBetweenEvictionRunsMillis(Long.valueOf(p.getProperty("timeBetweenEvictionRunsMillis")));
                } catch (IOException e) {
                } finally {
                    try {
                        resourceAsStream.close();
                    } catch (IOException e) {
                        // Ignored
                    }
                }
            }
            POOL = new GenericObjectPool<FTPClient>(new FTPClientFactory(p), config);
        }

        private static FTPClientUtil instance = new FTPClientUtil();

    }

}
