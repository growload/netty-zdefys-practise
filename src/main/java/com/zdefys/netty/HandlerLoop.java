package com.zdefys.netty;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

/**
 * @author: zdefys
 * @date: 2020/6/28 22:05
 * @version: v1.0
 * @description: 用于处理从Selector的监听轮询
 */
public class HandlerLoop implements Runnable{

    private Selector selector;

    public HandlerLoop(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            // 不断循环遍历 是否有事件发生
            while (true) {
                // 返回当前发生的事件个数  num>0  要处理事件
                int num = selector.select();
                if (num == 0) {
                    continue;
                }
                // 接收事件的集合  然后遍历SelectionKey代表一种事件
                Set<SelectionKey> set = selector.selectedKeys();
                Iterator<SelectionKey> iterator = set.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    // 避免重复处理
                    iterator.remove();
                    // 根据事件的类型  分发给Acceptor或者Handler进行处理
                    // 通过attachment方法取出存储的对象
                    // 接收的是读写事件  获取的是Handler对象
                    Runnable runnable = (Runnable) selectionKey.attachment();
                    runnable.run();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
