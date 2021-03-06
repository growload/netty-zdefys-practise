package com.zdefys.netty;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author: zdefys
 * @date: 2020/6/28 21:36
 * @version: v1.0
 * @description:
 */
public class Handler implements Runnable{

    private SelectionKey key;
    private State state;

    public Handler(SelectionKey key) {
        this.key = key;
        this.state = State.READ;
    }

    @Override
    public void run() {
        // 判断是读还是写
        switch (state) {
            case READ:
                read();
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
