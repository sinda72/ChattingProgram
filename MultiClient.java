import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.JProgressBar;

public class MultiClient implements ActionListener {
   private Socket socket;
   private ObjectInputStream ois;
   private ObjectOutputStream oos;
   private JFrame jframe, login1; //â 
   private JTextField jtf, pass;//���۸޽���,���â 
   private JTextArea jta;
   private JLabel jlb1, jPW;//���̸�, ������̸� ��
   private JPanel jp1, jp2, jp3;//������Ʈ ��ü ���� �г�
   private String id;//������̸�
   private JButton jbtn, jbtn1 , jexit;//����, �α���, ���� ��ư
   Color color = new Color(0xD4F4FA);
   private boolean login = false;

   public MultiClient() {
      jframe = new JFrame("Multi Chatting");
      login1 = new JFrame("Login");
      JProgressBar progressBar = new JProgressBar();
      progressBar.setStringPainted(true);
      progressBar.setIndeterminate(true);
      progressBar.setBounds(32, 303, 195, 14);
      
      jtf = new JTextField(20);
      pass = new JTextField(20);
      jta = new JTextArea(43, 43) {
         {
            setOpaque(false);
         }
      };
      jlb1 = new JLabel("ROOM 1") {
         {
            setOpaque(false);
         }
      };
      
     
      jPW = new JLabel("name"); //������̸�
      jbtn = new JButton("Enter"); //���� ��ư
      jbtn1 = new JButton("Login"); //�α��� ��ư
      jexit = new JButton("exit"); //���� ��ư
      jp1 = new JPanel();
      jp2 = new JPanel();
      jp3 = new JPanel(); //�α��� ȭ�鿡 �߰���

	  jbtn.setFont(new Font("�������", Font.PLAIN, (int) 20));
      jlb1.setFont(new Font("�������", Font.PLAIN, (int) 15));
      jlb1.setBackground(color);
      
      jPW.setFont(new Font("�������", Font.PLAIN, (int) 30));
      jPW.setHorizontalAlignment(jPW.CENTER);
      
      pass.setFont(new Font("�������", Font.PLAIN, (int) 30));
      pass.setBackground(Color.WHITE);
      jbtn1.setBackground(color);
      jbtn1.setFont(new Font("�������", Font.PLAIN, (int) 30));
      jexit.setBackground(color);
      jexit.setFont(new Font("�������", Font.PLAIN, (int) 30));
      jbtn.setBackground(color);
      
      jp1.setLayout(new BorderLayout());
      jp2.setLayout(new BorderLayout());
      jp3.setLayout(new GridLayout(2,2,10,10));

      jp1.add(jbtn, BorderLayout.EAST); 
      jp1.add(jtf, BorderLayout.CENTER);//�޽���
      jp2.add(jlb1, BorderLayout.CENTER);
      
      jp1.setBackground(Color.WHITE);
      jp2.setBackground(color);
      jp3.setBackground(Color.WHITE);
   
      jp3.add(jPW);
      jp3.add(pass);
      jp3.add(jbtn1);
      jp3.add(jexit);
      jframe.add(jp1, BorderLayout.SOUTH);
      jframe.add(jp2, BorderLayout.NORTH);
      
      JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      jframe.add(jsp, BorderLayout.CENTER);
      login1.add(jp3, BorderLayout.CENTER);

      jtf.addActionListener(this); 
      jbtn.addActionListener(this);
      jexit.addActionListener(this);
      
      jframe.addWindowListener(new WindowAdapter() {
		 
         public void windowClosing(WindowEvent e) {
            try {
               oos.writeObject(id + "#exit");//������̸��� �Բ� ���� �޽��� 
            } catch (IOException ee) {
               ee.printStackTrace();
            }
            System.exit(0); //����
         }

         public void windowOpened(WindowEvent e) {
            jtf.requestFocus();
         }
      });

      jbtn1.addActionListener(this); //�α��ι�ư

      jta.setEditable(false);
      Toolkit tk = Toolkit.getDefaultToolkit();
      Dimension d = tk.getScreenSize();
      int screenHeight = d.height;
      int screenWidth = d.width;
      
      jframe.pack();
	  jframe.setSize(500, 600);

      jframe.setLocation((screenWidth - jframe.getWidth()) / 2, (screenHeight - jframe.getHeight()) / 2);
      jframe.setResizable(false);
      jframe.setVisible(false);

      login1.pack();
      login1.setSize(500, 200);
      login1.setLocation((screenWidth - jframe.getWidth()) / 2, (screenHeight - jframe.getHeight()) / 2);
      login1.setResizable(false);
      login1.setVisible(true);
   }

   public void actionPerformed(ActionEvent e) {
      Object obj = e.getSource();
      String msg = jtf.getText();

      String str = e.getActionCommand();

      if (str.equals("Login")) {
         jframe.setVisible(true);
         login1.setVisible(false);

         id = pass.getText();//����ڿ��� �Է¹��� NAME
      }
      
      if (str.equals("exit")){
         System.exit(0);
      }
      
      if (obj == jtf) {
         if (msg == null || msg.length() == 0) {
            JOptionPane.showMessageDialog(jframe, "����������", "���", JOptionPane.WARNING_MESSAGE);
         } else {
            try {
			   // �޽��� write
               oos.writeObject(id + "#" + msg);
            } catch (IOException ee) {
               ee.printStackTrace();
            }
            jtf.setText("");
         }
      } else if (obj == jbtn) {
         try {
			oos.writeObject(id + "#" + msg);
         } catch (IOException ee) {
            ee.printStackTrace();
         }
		 jtf.setText("");
      }
   }

   public void exit() {
      System.exit(0);
   }
   
   public void init() throws IOException {
	  //���ϻ���, ���� ip�ּҿ� ��Ʈ��ȣ
      socket = new Socket("192.168.35.215", 5000);
      System.out.println("connected...");//���������� ����Ǹ� ��µǴ� �޽���

	  //������� ���� I/O
      oos = new ObjectOutputStream(socket.getOutputStream());
      ois = new ObjectInputStream(socket.getInputStream());
	  //
      MultiClientThread ct = new MultiClientThread(this);
      Thread t = new Thread(ct);
      t.start();
   }
	// main�Լ�
   public static void main(String args[]) throws IOException {
      JFrame.setDefaultLookAndFeelDecorated(true);
      MultiClient cc = new MultiClient();
      cc.init(); //�Լ�ȣ��
   }
   //
   public ObjectInputStream getOis() {
      return ois;
   }

   public JTextArea getJta() {
      return jta;
   }

   public String getId() {
      return id;
   }

   public void SetName(String a) {
      id = a;
   }

   public void Clear() {
	   jta.setText(""); //�Էºκ� �ʱ�ȭ
	   jtf.requestFocus(); //��Ŀ���ֱ�
	}
   //���� ������ Ŭ����
   class MultiClientThread extends Thread{
    private MultiClient mc;
    
    public MultiClientThread(MultiClient mc){
        this.mc = mc;
    }
    public void run(){
        String message = null;
        String[] receivedMsg = null;
        
        boolean isStop = false;
        while(!isStop){
            try{
                message = (String)mc.getOis().readObject();
                receivedMsg = message.split("#");

            }catch(Exception e){
                e.printStackTrace();
                isStop = true;
            }
            System.out.println(receivedMsg[0]+","+receivedMsg[1]);
            if(receivedMsg[1].equals("exit")){ //exit �ԷµǸ� ä�ù� ����
                if(receivedMsg[0].equals(mc.getId())){ //������ ����ڴ� �����
                    mc.exit();
                }else{ //���� ä�ù� ���� �ִ� ����ڿ��� ��µ�msg
                    mc.getJta().append(
                    receivedMsg[0] +"���� ���� �ϼ̽��ϴ�."+
                    System.getProperty("line.separator"));
                    mc.getJta().setCaretPosition(
						mc.getJta().getDocument().getLength());
                }
            }else{  //exit�� �ƴ� �Ϲ� �޽����� ��� ä�� ���� ������             
                mc.getJta().append(
                receivedMsg[0] +" : "+receivedMsg[1]+
                System.getProperty("line.separator"));
                mc.getJta().setCaretPosition(
                    mc.getJta().getDocument().getLength());     
            }
        }
    }
}
}