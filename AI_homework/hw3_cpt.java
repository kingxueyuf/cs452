import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author robin-xue
 * @project_name AI_homework
 * @file_name hw3_cpt.java
 * @tag 
 * @date 2013-11-2
 */

/*
 * Burglary
 * JohnCalls | Alarm = T
 * Burglary | JohnCalls = T, MaryCalls = T
 * "C:\\Users\\robin-xue\\Desktop\\assign03\\burglary.txt";
 * 
 * Recommendation | Honesty = T
 * Recommendation | Honesty = T , Quality = 1
 * Recommendation | Honesty = T , Quality = 1 , Kindness = 2 
 * Recommendation | Honesty = T , Quality = 1 , Kindness = 2
 * "C:\\Users\\robin-xue\\Desktop\\assign03\\books.txt"
 * 
 * WetGrass | Cloudy = T , Sprinkler = T , Rain = F
 * "C:\\Users\\robin-xue\\Desktop\\assign03\\sprinklers.txt"
 */
public class hw3_cpt {
	
	private LinkedHashMap<String,Node> nodePointer = new LinkedHashMap<String,Node>();
	private HashMap<String,ArrayList<String>> nodeParameter = new HashMap<String,ArrayList<String>>();
	private HashMap<String,String> premiseMap = new HashMap<String,String>();
	private LinkedHashSet<String> problemSet = new LinkedHashSet<String>();
	
	public static void main(String args[]) {
		String path = args[0];
		System.out.println("Loading file "+path);
		System.out.println();
		BufferedReader scan;
		try {
			scan = new BufferedReader(new InputStreamReader(
					new FileInputStream(path)));
			Scanner scan2 = new Scanner(System.in);
			hw3_cpt hw3 = new hw3_cpt();
			hw3.getInputDes(scan);
			
			while(hw3.getInputQuery(scan2)) {
				hw3.solve();
				hw3.cleanNodeValue();
				System.out.println();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void solve()
	{
		// calculate Denominator ·ÖÄ¸
		String[] arr = this.nodePointer.keySet().toArray(new String[0]);
		double down = calculateDenominator(arr, 0);
		
		cleanNodeValue();

		String[] arr2 = this.problemSet.toArray(new String[0]);

		for (int i = 0; i < arr2.length; i++) {
			ArrayList<String> var = this.nodeParameter.get(arr2[i]);
			for (int j = 0; j < var.size(); j++) {
				this.premiseMap.put(arr2[i], var.get(j));
				double up = calculateDenominator(arr, 0);
				System.out.print("P("+var.get(j)+")"+" = "+up/down+"");
				if(j != var.size())
				{
					System.out.print(", ");
				}
				cleanNodeValue();
			}
			System.out.println();
		}
	}
	
	public double calculateNumerator()
	{
		return 0;
	}
	
	public void cleanNodeValue()
	{
		for(String key : nodePointer.keySet())
		{
			this.nodePointer.get(key).value="";
		}
	}
	
	public double calculateDenominator(String[] array, int index) {
		if(index == array.length )
		{
			return 1;
		}
		String nodeName = array[index];
		if (this.premiseMap.containsKey(nodeName)) {
			String value = this.premiseMap.get(nodeName);
			Node node = this.nodePointer.get(nodeName);
			/* important */
			node.value = value;
			double p =1;
			if (node.hasParent) {
				String key = "";
				for (int i = 0; i < node.parentPointer.size(); i++) {
					key += node.parentPointer.get(i).value;
				}
				p = Double.valueOf(node.hasParentNodeTable.get(key).get(value));
			} else {
				String possibility = node.noParentNodeTable.get(value);
				p = Double.valueOf(possibility);
			}
			return p * calculateDenominator(array, index + 1);
		} else {
			double p = 1;
			Node node = this.nodePointer.get(nodeName);
			List<String> var = this.nodeParameter.get(nodeName);
			if (node.hasParent) {
				double total = 0;
				String key = "";
				for (int i = 0; i < node.parentPointer.size(); i++) {
					key += node.parentPointer.get(i).value;
				}

				for (int i = 0; i < var.size(); i++) {
					node.value = var.get(i);
					String possibility = node.hasParentNodeTable.get(key).get(
							var.get(i));
					p = Double.valueOf(possibility);
					total += p * calculateDenominator(array, index + 1);
				}
				return total;

			} else {
				double total = 0;
				for (int i = 0; i < var.size(); i++) {
					node.value = var.get(i);
					String possibility = node.noParentNodeTable.get(var.get(i));
					p = Double.valueOf(possibility);
					total += p * calculateDenominator(array, index + 1);
				}
				return total;
			}
		}
	}

	public boolean getInputQuery(Scanner scan)
	{
		String oneLine = scan.nextLine();
		if(oneLine.endsWith("quit"))
		{
			return false;
		}
		
		premiseMap = new HashMap<String,String>();
		problemSet = new LinkedHashSet<String>();
		
		if(oneLine.contains("|"))
		{
			//conditional probably problem.
			oneLine = oneLine.replaceAll(" ","");
//			System.out.println(oneLine);
			String[] arr = oneLine.split("\\|");
			String problemPart = arr[0];
//			System.out.println(problemPart);
			String conditionPart = arr[1];
//			System.out.println(conditionPart);
			
			//solve problem part
			if(problemPart.contains(","))
			{
				String[] arr2 = problemPart.split(",");
				for(int i = 0 ; i <arr2.length ; i ++)
				{
					this.problemSet.add(arr2[i]);
				}
			}else
			{
				problemSet.add(problemPart);
			}
			
			//solve condition part
			if(conditionPart.contains(","))
			{
				String[] arr3 = conditionPart.split(",");
				for(int i = 0 ; i < arr3.length ; i ++)
				{
					String[]arr4 = arr3[i].split("=");
					this.premiseMap.put(arr4[0],arr4[1]);
				}
				
			}else
			{
				String[] arr4 = conditionPart.split("=");
				this.premiseMap.put(arr4[0], arr4[1]);
			}
			
		}else
		{
			//not conditional probably problem.
			problemSet.add(oneLine.replace(" ", ""));
		}
		return true;
	}
	/**
	 * These function will get input from txt file and this txt file can be divided into three part.
	 *	
	 */
	public void getInputDes(BufferedReader scan) {
		
		/*
		 * 	1st part
		 *	Burglary T F
		 *	Earthquake T F
		 *	Alarm T F
		 *  JohnCalls T F
	     *  MaryCalls T F
		 */
		initNodeVariables(scan);
		
		/*
		 *	2nd part
		 *	# Parents
		 *	Alarm Burglary Earthquake
		 *	JohnCalls Alarm
		 * 	MaryCalls Alarm
		 */
		initNodeRelationship(scan);
		
		/*
		 *	3rd part
		 *	# Tables
		 *	Burglary
		 *	0.001
		 *	Earthquake
		 *	0.002
		 */
		initCPT(scan);
	}
	
	public void initNodeVariables(BufferedReader scan)
	{
		String oneLine="";
		try {
			oneLine = scan.readLine();
			while(!oneLine.equals("# Parents"))
			{
				String[] sArr = oneLine.split(" ");
				this.nodePointer.put(sArr[0], null);
				ArrayList list = new ArrayList<String>();
				for(int i = 1 ; i <sArr.length ; i++)
				{
					list.add(sArr[i]);
				}
				this.nodeParameter.put(sArr[0], list);
				oneLine = scan.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void initNodeRelationship(BufferedReader scan)
	{
		String oneLine;
		try {
			oneLine = scan.readLine();
			while(!oneLine.equals("# Tables"))
			{
				String[] arr = oneLine.split(" ");
				
				Node childNode = this.nodePointer.get(arr[0]);
				if( childNode == null )
				{
					childNode = new Node(arr[0]);
					this.nodePointer.put(arr[0], childNode);
				}
				childNode.hasParent = true;
				
				for(int i = 1 ; i<arr.length ; i++)
				{
					Node parentNode = this.nodePointer.get(arr[i]);
					if(parentNode == null)
					{
						parentNode = new Node(arr[i]);
						this.nodePointer.put(arr[i], parentNode);
					}
					childNode.parentPointer.add(parentNode);
					parentNode.childPointer.add(childNode);
				}
				oneLine = scan.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initCPT(BufferedReader scan) {
		String oneLine;
		try {
			oneLine = scan.readLine();
			while (oneLine != null) {
				List<String> currentNodeVar = this.nodeParameter.get(oneLine);
				Node currentNode = this.nodePointer.get(oneLine);

				// variables are boolean type
				if (currentNode.hasParent) {
					// child node
					List<Node> list4 = currentNode.parentPointer;
					int totalSize = 1;
					for (int i = 0; i < list4.size(); i++) {
						String name = list4.get(i).name;
						int size = this.nodeParameter.get(name).size();
						totalSize = totalSize * size;
					}
					for (int i = 0; i < totalSize; i++) {
						String line5 = scan.readLine();
						String[] arr3 = line5.split(" ");
						String parentKey = "";
						for (int j = 0; j < list4.size(); j++) {
							parentKey += arr3[j];
						}

						HashMap<String, String> map = new HashMap<String, String>();
						int index = 0;
						double total = 0;
						for (int j = list4.size(); j < arr3.length; j++) {
							total += Double.valueOf(arr3[j]);
							map.put(currentNodeVar.get(index), arr3[j]);
							index++;
						}
						map.put(currentNodeVar.get(index), (1 - total) + "");
						currentNode.hasParentNodeTable.put(parentKey, map);
					}
				} else {
					// no parent node
					String oneLine3 = scan.readLine();
					String[] arr3 = oneLine3.split(" ");
					double total = 0;
					for (int i = 0; i < arr3.length; i++) {
						total += Double.valueOf(arr3[i]);
						currentNode.noParentNodeTable.put(
								currentNodeVar.get(i), arr3[i]);
					}
					if (currentNodeVar.size() > currentNode.noParentNodeTable
							.size()) {
						currentNode.noParentNodeTable.put(
								currentNodeVar.get(arr3.length), (1 - total)
										+ "");
					}
				}
				oneLine = scan.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class Node
	{
		Node(String name)
		{
			this.name = name;
		}
		String name;//node name
		String value;//node value "T","F"
		List<Node> parentPointer = new ArrayList<Node>();
		List<Node> childPointer = new ArrayList<Node>();
		Boolean hasParent = false;//"hasParent" "noParent"
		HashMap<String,HashMap<String,String>> hasParentNodeTable = new HashMap<String,HashMap<String,String>>();
		HashMap<String,String>noParentNodeTable = new HashMap<String,String>();
	}
}
