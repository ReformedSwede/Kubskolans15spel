import java.io.Serializable;


public class NumberPair<T1 extends Number, T2 extends Number> implements Serializable{
	
	private static final long serialVersionUID = -8168131040377764109L;
	
	public T1 number1;
	public T2 number2;
	
	public NumberPair(T1 obj1, T2 obj2){
		number1 = obj1;
		number2 = obj2;
	}
}
