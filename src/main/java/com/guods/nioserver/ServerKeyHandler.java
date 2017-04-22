package com.guods.nioserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ServerKeyHandler {

	/**
	 * 处理SelectionKey
	 * @throws IOException
	 */
	public void handleKey(Selector selector, SelectionKey key) throws IOException {
		try {
			if (key.isAcceptable()) {
				accept(selector, key);
			}
			if (key.isReadable()) {
				read(key);
			};
			if (key.isWritable()) {
				write(key);
			}
		} catch (CancelledKeyException e) {
		}
	}
	
	/**
	 * 服务端：接收客户端连接
	 * @throws IOException
	 */
	private void accept(Selector selector, SelectionKey key) throws IOException {
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel channel = null;
		// 获得和客户端连接的通道
		channel = server.accept();
		// 设置成非阻塞
		channel.configureBlocking(false);
		// 在和客户端连接成功之后，为了可以接收到客户端的信息，需要给通道设置读的权限。
		channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
	}
	
	/**
	 * 客户端、服务端：读取收到的信息
	 * @throws IOException
	 */
	private void read(SelectionKey key) {
		// 服务器可读取消息:得到事件发生的Socket通道
		SocketChannel channel = (SocketChannel) key.channel();
		// 创建读取的缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(100);
		try {
			channel.read(buffer);
			byte[] data = buffer.array();
			String msg = new String(data).trim();
			System.out.println("服务端收到信息：" + msg);
		} catch (IOException e) {
			try {
				channel.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println("close one channel");
		}
	}

	/**
	 * 客户端、服务端：向对端发送信息
	 * @throws IOException
	 */
	private void write(SelectionKey key) {
		// 服务器可读取消息:得到事件发生的Socket通道
		SocketChannel channel = (SocketChannel) key.channel();
		String msg = "server message";
		System.out.println("服务端写信息：" + msg);
		ByteBuffer outBuffer = ByteBuffer.wrap(msg.getBytes());
		try {
			// 将消息回送给客户端
			channel.write(outBuffer);
		} catch (IOException e) {
			try {
				channel.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println("close one channel");
		}
	}
}
