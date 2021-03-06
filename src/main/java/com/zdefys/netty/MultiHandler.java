package com.zdefys.netty;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

/**
 * @author: zdefys
 * @date: 2020/6/28 21:56
 * @version: v1.0
 * @description:
 */
public class MultiHandler implements Runnable{

    private SelectionKey key;
    private State state;

    public MultiHandler(SelectionKey key) {
        this.key = key;
        this.state = State.READ;
    }

    private ExecutorService pool;

    @Override
    public void run() {
        // 判断是读还是写
        switch (state) {
            case READ:
                // 将最耗时的读操作 放在线程池中执行
                pool.execute(() -> read());
                break;
            case WRITE:
                write();
                break;
        }

    }

    private void read() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 通过key获取通道
        SocketChannel channel = (SocketChannel) key.channel();
        try {
            // 将传送的数据 写入到buffer中
            int num = channel.read(buffer);
            // 转化为String
            String msg = new String(buffer.array());
            // 增加业务处理
            //
            // 继续注册写事件
            key.interestOps(SelectionKey.OP_WRITE);
            this.state = State.WRITE;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write() {
        ByteBuffer buffer = ByteBuffer.wrap("hello".getBytes());
        // 通过key获取通道
        SocketChannel channel = (SocketChannel) key.channel();
        try {
            channel.write(buffer);
            // 继续注册读事件
            key.interestOps(SelectionKey.OP_READ);
            this.state = State.READ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private enum State{
        READ,WRITE
    }
}
