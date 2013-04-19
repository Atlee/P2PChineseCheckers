package game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class CircularList<T> {
	
	private final ListNode<T> start;
	private ListNode<T> current;
	
	public static void main(String[] argv) throws Exception {
		Player p = new Player("unknown", 1);
		Move m = new Move(p, new Point(0,0));
		
		String s = m.serialize();
		System.out.println(m.serialize());
		
		Move x = Move.deSerialize(s);
		
		System.out.println(x.getJumps());
		
		/*
		ArrayList<Integer> l = new ArrayList<Integer>();
		l.add(0);
		l.add(1);
		l.add(2);
		l.add(3);
		l.add(4);
		CircularList<Integer> cl = new CircularList<Integer>(l);
		for (int cnt = 0; cnt < 20; cnt++) {
			System.out.println(cl.next());
		}*/
	}
	
	CircularList(List<T> l) {
		ArrayList<ListNode<T>> temp = new ArrayList<ListNode<T>>();
		for (T t : l) {
			temp.add(new ListNode(t));
		}
		
		for (int i = 0; i < temp.size(); i++) {
			if (i == temp.size() - 1) {
				temp.get(i).next = temp.get(0);
			} else {
				temp.get(i).next = temp.get(i+1);
			}
		}
		this.start = temp.get(0);
		this.current = this.start;
	}
	
	public T next() {
		T output = current.data;
		current = current.next;
		return output;
	}
	
	public T getCurrent() {
		return current.data;
	}

}

class ListNode<T> {
	public ListNode<T> next;
	public T data;
	
	public ListNode(T data) {
		this.data = data;
	}
}
