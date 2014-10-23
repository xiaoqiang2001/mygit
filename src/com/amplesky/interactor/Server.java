package com.amplesky.interactor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.amplesky.protobuf.CmsgProto.CMsg;
import com.amplesky.protobuf.CmsgProto.CMsgHead;
import com.amplesky.protobuf.CmsgProto.CMsgReg;

public class Server {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		run();
	}

	private static void run() throws IOException {
		ServerSocket serverSocket = new ServerSocket(12345);
		while (true) {
			System.out.println("等待接收用户连接：");
			// 接受客户端请求
			Socket client = serverSocket.accept();

			DataOutputStream dataOutputStream;
			DataInputStream dataInputStream;

			try {
				InputStream inputstream = client.getInputStream();

				dataOutputStream = new DataOutputStream(
						client.getOutputStream());
				byte len[] = new byte[1024];
				int count = inputstream.read(len);

				byte[] temp = new byte[count];

				for (int i = 0; i < count; i++) {

					temp[i] = len[i];
				}

				// 协议正文
				CMsg msg = CMsg.parseFrom(temp);
				//
				//
				CMsgHead head = CMsgHead.parseFrom(msg.getMsghead().getBytes());
				System.out.println("==len===" + head.getMsglen());
				System.out.println("==res===" + head.getMsgres());
				System.out.println("==seq===" + head.getMsgseq());
				System.out.println("==type===" + head.getMsgtype());
				System.out.println("==Termid===" + head.getTermid());
				System.out.println("==Termversion===" + head.getTermversion());

				CMsgReg body = CMsgReg.parseFrom(msg.getMsgbody().getBytes());
				System.out.println("==area==" + body.getArea());
				System.out.println("==Region==" + body.getRegion());
				System.out.println("==shop==" + body.getShop());
				sendProtoBufBack(dataOutputStream);
				inputstream.close();
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
				ex.printStackTrace();
			} finally {
				client.close();
				System.out.println("close");
			}
		}

	}

	private static byte[] getProtoBufBack() {

		// head
		CMsgHead head = CMsgHead.newBuilder().setMsglen(5).setMsgtype(1)
				.setMsgseq(3).setTermversion(41).setMsgres(5)
				.setTermid("11111111").build();

		// body
		CMsgReg body = CMsgReg.newBuilder().setArea(22).setRegion(33)
				.setShop(44).build();

		// Msg
		CMsg msg = CMsg.newBuilder()
				.setMsghead(head.toByteString().toStringUtf8())
				.setMsgbody(body.toByteString().toStringUtf8()).build();

		return msg.toByteArray();
	}

	private static void sendProtoBufBack(DataOutputStream dataOutputStream) {

		byte[] backBytes = getProtoBufBack();
		// 协议头部
		try {
			dataOutputStream.write(backBytes, 0, backBytes.length);
			dataOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
