import java.io.*;
import java.net.*;
import java.util.*;

//서버
public class MultiServer{
	private ArrayList<MultiServerThread> list;
	private Socket socket;//서버와 클라이언트가 통신 담당

	public MultiServer() throws IOException {
		//접속한 사용자 LIST
		list = new ArrayList<MultiServerThread>();
		//소켓 생성, 서버 포트 지정
		ServerSocket serverSocket = new ServerSocket(5000);
		MultiServerThread mst = null;
		boolean isStop = false;
		while (!isStop) {
			//서버는 대기 중의 상태
			System.out.println("Server ready...");
			//사용자 받음
			socket = serverSocket.accept();
			//새로운 사용자 쓰레드 클래스 생성
			mst = new MultiServerThread(this);
			list.add(mst);//사용자 LIST에 담아줌
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
	//쓰레드
	class MultiServerThread implements Runnable {
	private Socket socket;
	private MultiServer ms;
	//메시지 입출력을 위한 I/O
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	public MultiServerThread(MultiServer ms) {
		this.ms = ms;
	}

	public synchronized void run() {
		boolean isStop = false;
		try {
			//소켓 생성
			socket = ms.getSocket();
			//데이터 입출력을 위한 I/O
			ois = new ObjectInputStream(socket.getInputStream());//클라이언트로 받은 메시지
			oos = new ObjectOutputStream(socket.getOutputStream());//전송 메시지
			String message = null;
			//클라이언트가 isStop이 true가 될때까지 
			while (!isStop) {
				message = (String) ois.readObject();//메시지READ
				String[] str = message.split("#");
				
				if (str[1].equals("exit")) {//exit입력될 경우
					broadCasting(message);
					isStop = true;
				}else {//일반적인 메시지는 전송
					broadCasting(message);
				}
			}		
			//나간 클라이언트 arraylist에서 remove
			ms.getList().remove(this);
			System.out.println(socket.getInetAddress() + "정상적으로 종료하셨습니다");
			System.out.println("list size : " + ms.getList().size());
		}catch (Exception e) {
			ms.getList().remove(this);
			System.out.println(socket.getInetAddress() + "정상적으로 종료하셨습니다");
			System.out.println("list size : " + ms.getList().size());
		}
	}
	//서버에 연결된 모든 클라이언트들에게 메시지 전달을 위한 메서드
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