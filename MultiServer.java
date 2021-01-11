import java.io.*;
import java.net.*;
import java.util.*;

//����
public class MultiServer{
	private ArrayList<MultiServerThread> list;
	private Socket socket;//������ Ŭ���̾�Ʈ�� ��� ���

	public MultiServer() throws IOException {
		//������ ����� LIST
		list = new ArrayList<MultiServerThread>();
		//���� ����, ���� ��Ʈ ����
		ServerSocket serverSocket = new ServerSocket(5000);
		MultiServerThread mst = null;
		boolean isStop = false;
		while (!isStop) {
			//������ ��� ���� ����
			System.out.println("Server ready...");
			//����� ����
			socket = serverSocket.accept();
			//���ο� ����� ������ Ŭ���� ����
			mst = new MultiServerThread(this);
			list.add(mst);//����� LIST�� �����
			Thread t = new Thread(mst);
			t.start();
		}
	}

	public ArrayList<MultiServerThread> getList(){
		return list;
	}

	public Socket getSocket() {
		return socket;
	}

	public static void main(String arg[]) throws IOException {
		new MultiServer();
	}
	//������
	class MultiServerThread implements Runnable {
	private Socket socket;
	private MultiServer ms;
	//�޽��� ������� ���� I/O
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	public MultiServerThread(MultiServer ms) {
		this.ms = ms;
	}

	public synchronized void run() {
		boolean isStop = false;
		try {
			//���� ����
			socket = ms.getSocket();
			//������ ������� ���� I/O
			ois = new ObjectInputStream(socket.getInputStream());//Ŭ���̾�Ʈ�� ���� �޽���
			oos = new ObjectOutputStream(socket.getOutputStream());//���� �޽���
			String message = null;
			//Ŭ���̾�Ʈ�� isStop�� true�� �ɶ����� 
			while (!isStop) {
				message = (String) ois.readObject();//�޽���READ
				String[] str = message.split("#");
				
				if (str[1].equals("exit")) {//exit�Էµ� ���
					broadCasting(message);
					isStop = true;
				}else {//�Ϲ����� �޽����� ����
					broadCasting(message);
				}
			}		
			//���� Ŭ���̾�Ʈ arraylist���� remove
			ms.getList().remove(this);
			System.out.println(socket.getInetAddress() + "���������� �����ϼ̽��ϴ�");
			System.out.println("list size : " + ms.getList().size());
		}catch (Exception e) {
			ms.getList().remove(this);
			System.out.println(socket.getInetAddress() + "���������� �����ϼ̽��ϴ�");
			System.out.println("list size : " + ms.getList().size());
		}
	}
	//������ ����� ��� Ŭ���̾�Ʈ�鿡�� �޽��� ������ ���� �޼���
	public void broadCasting(String message) throws IOException {
		for (MultiServerThread ct : ms.getList()) {
			ct.send(message);
		}
	}

	public void send(String message) throws IOException {
		oos.writeObject(message);
	}
}
}