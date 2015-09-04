import java.util.HashSet;
public class StringTransformer{
	public static String transform(String word, int method, int arg){
		if (word==null)
			return null;
		String str = "";
		try{
			switch (method){
				case(0):
					str = append(word, (char)arg);
					break;
				case(1):
					str = prepend(word, (char)arg);
					break;
				case(2):
					str = delete_first(word);
					break;
				case(3):
					str = delete_last(word);
					break;
				case(4):
					str = reverse(word);
					break;
				case(5):
					str = duplicate(word);
					break;
				case(6):
					str = uppercase(word);
					break;
				case(7):
					str = lowercase(word);
					break;
				case(8):
					str = capitalize(word);
					break;
				case(9):
					str = ncapitalize(word);
					break;
				case(10):
					str = toggle_case(word);
					break;
				case(11):
					str = reflect(word);
					break;
				case(12):
					str = reflect2(word);
					break;
			}
		} catch (StringIndexOutOfBoundsException e){
			return null;
		}
		return str;
	}
	/*
		prepend a character to the string, e.g., @string;
		append a character to the string, e.g., string9;
		delete the first character from the string, e.g., tring;
		delete the last character from the string, e.g., strin;
		reverse the string, e.g., gnirts;
		duplicate the string, e.g., stringstring;
		reflect the string, e.g., stringgnirts or gnirtsstring;
		uppercase the string, e.g., STRING;
		lowercase the string, e.g., string;
		capitalize the string, e.g., String;
		ncapitalize the string, e.g., sTRING;
		toggle case of the string, e.g., StRiNg or sTrInG; 
	*/
	private static String prepend(String s, char c){
		return ""+c+s;
	}
	private static String append(String s, char c){
		return ""+s+c;
	}
	private static String delete_first(String s){
		return s.substring(1,s.length());
	}
	private static String delete_last(String s){
		return s.substring(0,s.length()-1);
	}
	private static String reverse(String s){
		String temp = "";
		for (int i = s.length()-1; i>=0;i--)
			temp += s.charAt(i);
		return temp;
	}
	private static String reflect(String s){
		return s+reverse(s);
	}
	private static String reflect2(String s){
		return reverse(s)+s;
	}
	private static String duplicate(String s){
		return s+s;
	}
	private static String uppercase(String s){
		return s.toUpperCase();
	}
	private static String lowercase(String s){
		return s.toLowerCase();
	}
	private static String toggle_case(String s){
		return null;
	}
	private static String capitalize(String s){
		String first = s.substring(0,1);
		s = s.substring(1,s.length());
		first = first.toUpperCase();
		s = s.toLowerCase();
		return first + s;
	}
	private static String ncapitalize(String s){
		String first = s.substring(0,1);
		first = first.toLowerCase();
		s = s.substring(1,s.length());
		s = s.toUpperCase();
		return first + s;
	}
}