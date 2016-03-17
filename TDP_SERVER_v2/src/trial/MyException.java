package trial;

class SocketReadWriteException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5641953951033556915L;

	public SocketReadWriteException(String mString){
		super(mString);
	}
}

public class MyException {
	
	public void recv(int num) throws SocketReadWriteException{
		System.out.println("Recv pakcet: " + num);
		send(num+1);;
		if(num == 3){
			throw new SocketReadWriteException("End of receiving the packet!");
		}
	}
	
	public void send(int num){
		System.out.println("Send pakcet: " + num);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyException myException = new MyException();
		myException.send(1);
		int i = 1;
		while(true){
			try {
				myException.recv(i);
				i++;
			} catch (SocketReadWriteException e) {
				// TODO: handle exception
				break;
			}
		}
	}

}
