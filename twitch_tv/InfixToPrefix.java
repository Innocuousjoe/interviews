import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class InfixToPrefix{

public static String[] operators = {"+","-","/","*","^","(",")"};
public static int adjustSlider = 0; //Used in evaluate to adjust each FOR loop's i to accommodate for nested recursion
 
	
public static void main (String[] args) throws IOException{
	String[] returning;
	boolean reduce = false;
	if (args.length == 2 && args[0].equals("-r")){
		returning = scanFix(args[1]).split("[ ]+");//split with a regex to get rid of spaces
		reduce = true;
	}else{
		returning = scanFix(args[0]).split("[ ]+");//split with a regex to get rid of spaces
		reduce = false;
	}
	Stack<String> operatorStack = new Stack<String>();
	Stack<String> operandStack = new Stack<String>();
	operatorStack.push("sentinelString");
	/*
	 * Infix to Prefix algorithm using a stack for operators and a stack for operands
	 */
	for (int i = 0 ; i < returning.length ; i++){
		String upNextString = returning[i];
		if (!Arrays.asList(operators).contains(upNextString)){
			operandStack.push(upNextString);
		}else if (upNextString.equals("(") || operatorStack.isEmpty() || (precedenceCalc(upNextString) > precedenceCalc(operatorStack.peek()))){
			operatorStack.push(upNextString);
		}else if (upNextString.equals(")")){
			while(!operatorStack.peek().equals("(")){
				String tempOperator = operatorStack.pop();
				String rightOperand = operandStack.pop();
				String leftOperand = operandStack.pop();
				String operand = "(" + tempOperator + " " + leftOperand + " " + rightOperand + ")";
				operandStack.push(operand);
			}
			operatorStack.pop();
		}else if (precedenceCalc(upNextString) <= precedenceCalc(operatorStack.peek())){
			while(!operatorStack.isEmpty() &&
					precedenceCalc(upNextString) <= precedenceCalc(operatorStack.peek())){
				String tempOperator = operatorStack.pop();
				String rightOperand = operandStack.pop();
				String leftOperand = operandStack.pop();
				String operand = "(" + tempOperator + " " + leftOperand + " " + rightOperand + ")";
				operandStack.push(operand);
			}
			operatorStack.push(upNextString);
		}
	}
	while(!operatorStack.peek().equals("sentinelString")){
		String tempOperator = operatorStack.pop();
		String rightOperand = operandStack.pop();
		String leftOperand = operandStack.pop();
		String operand = "(" + tempOperator + " " + leftOperand + " " + rightOperand + ")";
		operandStack.push(operand);
	}
	
	String result = operandStack.pop();
	System.out.println(result);
	ArrayList<String> returnResult = new ArrayList<String>();
	if (reduce){
		returnResult = evaluate(result);
	}
	String returnVal = returnResult.toString();
	System.out.println(returnVal);
	
}

/*
 *@params: String containing the file to be read from
 *@return: String read from the file containing the infix expression to be evaluated
 */
public static String scanFix(String filename) throws IOException {
	Pattern operators = Pattern.compile("[+/*-]");
    ArrayList<String> lines = new ArrayList<String>();
    try{
    FileReader fileReader = new FileReader(filename);
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    String toReturn = null;
    while ((toReturn = bufferedReader.readLine()) != null) {
    	boolean testVal = isDouble(toReturn);
    	if (!operators.matcher(toReturn).find() && !testVal){
    		continue;
    	}
        lines.add(toReturn);
    }
    bufferedReader.close();
    if (lines.size() != 1){
    	System.err.println("Wrong number of infix expressions!  Consider splitting into multiple files.");
    	System.exit(1);
    }
    }catch(FileNotFoundException e){
    	System.err.println("Oops!  File not found!  Check the path.");
    	System.exit(1);
    }
    return lines.get(0);
}
/*
 *@params: String
 *@return: boolean indicating whether or not the input is a Double or not, by abusing try/catch
 */
public static boolean isDouble(String s) {
    try { 
        Double.parseDouble(s); 
    } catch(NumberFormatException e) { 
        return false; 
    }
    // only got here if we didn't return false
    return true;
}

/*
 *@params: String containing an operator
 *@return: precedence of that operator
 */
public static int precedenceCalc (String s){
	if (s.equals("^")){
		return 3;
	}
	if (s.equals("*") || s.equals("/")){
		return 2;
	}
	if (s.equals("+") || s.equals("-")){
		return 1;
	}
	if (s.equals("sentinelString")){
	return 0;
	}
	return 0;
}
/*
 * @params: String containing the prefix expression based on the original input
 * @return: an ArrayList<String> containing the expression reduced as much as possible.
 */
public static ArrayList<String> evaluate(String prefix){
	String[] toEval = prefix.split(" ");
	ArrayList<String> returningList = new ArrayList<String>();
	for (int i = 0 ; i < toEval.length ; i ++){
		String temp = toEval[i];
		if (temp.equals("(") || temp.equals(")") || temp.equals(" ")){
			continue;
		}
		if ((temp.startsWith("("))){
			returningList.add(evalTwoArgs(toEval, i));
			i = adjustSlider;
		}	
		}
	return returningList;
}

/*
 * @params: String array containing a series of expressions and numbers that have yet to be evaluated
 * 			Integer designating where in the subString to start from, based on previous recursive calls
 * @return: String containing the reduced (as much as possible) subString
 */
public static String evalTwoArgs(String[] subString, int index){
	String operator = subString[index];
	if (operator.startsWith("(")){
		operator = operator.substring(1);
	}
	int startFrom = ++index;
	String arg1 = "";
	String arg2 = "";
	boolean arg1eval = false;
	boolean arg2eval = false;
	for (int i = startFrom ; i < subString.length ; i++){
		if (arg1eval && arg2eval){
			break;
		}
		String temp = subString[i];
		if (temp.endsWith(")")){
			temp = temp.substring(0, 1);
			adjustSlider = i;
		}
		if (temp.equals("(") || temp.equals(")") || temp.equals(" ")){
			continue;
		}
		if (temp.startsWith("(") && !arg1eval){
			arg1 = evalTwoArgs(subString, i);
			arg1eval = true;
			i = adjustSlider;
			continue;
		}
		if (!arg1eval){
			arg1 = temp;
			arg1eval = true;
		}else if (temp.startsWith("(") && !arg2eval){
			arg2 = evalTwoArgs(subString, i);
			arg2eval = true;
			i = adjustSlider;
			continue;
		}else if (!arg2eval){
			arg2 = temp;
			arg2eval = true;
		}
		
	}
	/*
	 * Probably generalizable, but at least it looks symmetric...
	 */
	if (operator.equals("+") && isDouble(arg1)	&& isDouble(arg2)){
		double middle = Double.parseDouble(arg1) + Double.parseDouble(arg2);
		return Double.toString(middle);
	} else if (operator.equals("+")){
		return "(" + operator + " " + arg1 + " " + arg2 + ")";
	}
	
	if (operator.equals("*") && isDouble(arg1)	&& isDouble(arg2)){
		double middle = Double.parseDouble(arg1) * Double.parseDouble(arg2);
		return Double.toString(middle);
	} else if (operator.equals("*")){
		return "(" + operator + " " + arg1 + " " + arg2 + ")";
	}
	
	if (operator.equals("/") && isDouble(arg1)	&& isDouble(arg2)){
		double middle = Double.parseDouble(arg1) / Double.parseDouble(arg2);
		return Double.toString(middle);
	} else if (operator.equals("/")){
		return "(" + operator + " " + arg1 + " " + arg2 + ")";
	}
	
	if (operator.equals("-") && isDouble(arg1)	&& isDouble(arg2)){
		double middle = Double.parseDouble(arg1) - Double.parseDouble(arg2);
		return Double.toString(middle);
	} else if (operator.equals("-")){
		return "(" + operator + " " + arg1 + " " + arg2 + ")";
	}
	
	if (operator.equals("^") && isDouble(arg1)	&& isDouble(arg2)){
		double middle = Math.pow(Double.parseDouble(arg1), Double.parseDouble(arg2));
		return Double.toString(middle);
	} else if (operator.equals("^")){
		return "(" + operator + " " + arg1 + " " + arg2 + ")";
	}
	return "";

}

}