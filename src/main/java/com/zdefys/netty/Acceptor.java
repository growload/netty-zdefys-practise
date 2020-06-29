package com.zdefys.netty;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author: zdefys
 * @date: 2020/6/28 21:31
 * @version: v1.0
 * @description:
 */
public class Acceptor implements Runnable{

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    public Acceptor(Selector selector, ServerSocketChannel serverSocketChannel) {
        this.selector = selector;
        this.serverSocketChannel = serverSocketChannel;
    }

    @Override
    public void run() {
        try {
            // 接收到对应的客户端通道
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            // 注册通道的读事件
            SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);
            // 创建handler 处理后续的读写事件
//            Handler handler = new Handler(key);
            MultiHandler handler = new MultiHandler(key);
            key.attach(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
