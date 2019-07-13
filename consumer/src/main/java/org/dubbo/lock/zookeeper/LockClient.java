package org.dubbo.lock.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.util.StringUtils;

/**
 * @author : cuixiuyin
 * @date : 2019/7/13
 */
public class LockClient {

    /**
     * 命名空间
     */
    private String namespace;
    /**
     * 连接信息
     */
    private String zookeeper;


    public LockClient(String namespace, String zookeeper) {
        this.namespace = namespace;
        this.zookeeper = StringUtils.replace(zookeeper, "zookeeper://", "");
    }


    /**
     * 分布式可重入排它锁
     *
     * @return
     */
    public InterProcessMutex interProcessMutex(String lockPath) {
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeper, new ExponentialBackoffRetry(1000, 3));
        // 启用命名空间，做微服务间隔离
        client.usingNamespace(namespace);
        client.start();
        return new InterProcessMutex(client, lockPath);
    }

    /**
     * 分布式排它锁（不可重入）
     *
     * @param lockPath
     * @return
     * @throws Exception
     */
    public InterProcessSemaphoreMutex interProcessSemaphoreMutex(String lockPath) {
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeper, new ExponentialBackoffRetry(1000, 3));
        // 启用命名空间，做微服务间隔离
        client.usingNamespace(namespace);
        client.start();
        return new InterProcessSemaphoreMutex(client, lockPath);
    }

    /**
     * 分布式读写锁 。 通过 lock.readLock() 得到写锁。lock.writeLock() 得到读锁
     *
     * @param lockPath
     * @return
     * @throws Exception
     */
    public InterProcessReadWriteLock interProcessReadWriteLock(String lockPath) {
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeper, new ExponentialBackoffRetry(1000, 3));
        // 启用命名空间，做微服务间隔离
        client.usingNamespace(namespace);
        client.start();
        return new InterProcessReadWriteLock(client, lockPath);
    }

    public static void main(String[] args) throws Exception {
        // 每隔 1 秒重试一次，最多重试三次
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("连接信息")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace("namespace")
                .build();
        curatorFramework.start();
        // 创建一个临时有序节点
        curatorFramework.create()
                .creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("路径", "内容".getBytes());

        // 删除一个节点
        curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().withVersion(10086).forPath("路径");
        // 读取一个节点的数据
        byte[] bytes = curatorFramework.getData().forPath("路径");
        // 更新一个节点的数据
        curatorFramework.setData().withVersion(10086).forPath("路径");
        // 检查节点是否存在
        curatorFramework.checkExists().forPath("路径");
    }
}
