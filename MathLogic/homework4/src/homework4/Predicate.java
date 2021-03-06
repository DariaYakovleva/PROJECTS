package homework4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Predicate implements Expression {
	String val;
	List<Expression> e1;
	public Predicate(String val, List<Expression> e1) {
		this.val = val;
		this.e1 = e1;
	}

	public String printExp() {
		if (val.equals("=")) {
			return "(" + e1.get(0).printExp() + val + e1.get(1).printExp() + ")";
		}
		String res =  val;
		if (!e1.isEmpty()) {
			res += "(";
		}
		for (int i = 0; i < e1.size(); i++) {
			res += e1.get(i).printExp();
			if (i + 1 < e1.size()) {
				res += ", ";
			} else {
				res += ")";
			}
		}
		return res;
	}

	public boolean equalTree(Expression b) {
		if (!(b instanceof Predicate))
			return false;
		Predicate c = (Predicate)b;
		if (e1.size() != c.e1.size()) return false;
		boolean eq = true;
		for (int i = 0; i < e1.size(); i++) {
			eq &= e1.get(i).equalTree(c.e1.get(i));
		}
		return val.equals(c.val) && eq;
	} 

	public boolean almostEqualTree(Expression b, Map<String, Expression> list) {
		//?????????������� ���-�� � ���������
		if (e1.isEmpty() && !val.equals("=")) {
			if (!list.containsKey(val)) {
				list.put(val, b);
				return true;
			}
			return b.equalTree(list.get(val));
		}
		if (!(b instanceof Predicate))
			return false;
		Predicate c = (Predicate)b;
		if (e1.size() != c.e1.size()) return false;
		boolean eq = true;
		for (int i = 0; i < e1.size(); i++) {
			eq &= e1.get(i).almostEqualTree(c.e1.get(i), list);
		}
		return val.equals(c.val) && eq;
	} 
	
	public boolean freeEntry(Variable xx) {
		boolean good = false;
		for (int i = 0; i < e1.size(); i++) {
			good |= e1.get(i).freeEntry(xx); 
		}
		return good;
	}
	
	public Expression replaceVar(Variable from, Expression to) {
		List<Expression> e2 = new ArrayList<>();
		for (int i = 0; i < e1.size(); i++) {
			e2.add(e1.get(i).replaceVar(from, to)); 
		}		
		return new Predicate(val, e2);
	}
	
	public Map<String, Expression> getVariables(Expression b) {
		Map<String, Expression> res = new HashMap<>();
		if (!(b instanceof Predicate))
			return res;
		Predicate c = (Predicate)b;
		if (e1.size() != c.e1.size()) return res;
		for (int i = 0; i < e1.size(); i++) {
			res.putAll(e1.get(i).getVariables(c.e1.get(i)));
		}	
		return res;
	}
	
	public boolean haveBoundEntry(Variable xx, Set<String> vars, List<String> quants) {
		boolean good = false;
		for (int i = 0; i < e1.size(); i++) {
			good |= e1.get(i).haveBoundEntry(xx, vars, quants); 
		}
		return good;
	}
}
