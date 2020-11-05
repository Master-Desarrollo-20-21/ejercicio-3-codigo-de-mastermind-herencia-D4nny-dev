import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Mastermind {
	private Game game;
	
	public Mastermind(){
		this.game = new Game();
	}

	public void play() {
		do {
			this.game.play();
		} while (this.isResumed());	
	}
	
	private boolean isResumed() {
		String answer;
		Console console = new Console();
		do{
			answer = console.readString("Do you want continue? (y/n):");
 		}while(!answer.equals("y") && !answer.equals("n"));
		return answer.equals("y");
	}	
	
	public static void main(String[] args) {
		new Mastermind().play();
	}
}


class Game {
	
	private final int MAX_ATTEMPTS = 10;
	private Console console = new Console();
	private SecretCombination secret;
	
	public Game(){
		secret = new SecretCombination();
	}

	public SecretCombination getSecretCombination(){
		return this.secret;
	}

	public void play() {
		console.out("Avaliable Colors: " +Color.getColorsAvailables() +"\n");
		for (int i = 0; i < MAX_ATTEMPTS; i++) {				
			String propose = console.readCombination();
			Combination combination = new Combination(propose);
			
			if(this.isWinner(combination)){
				console.out("\nYOU WIN!! :D\n");
				System.exit(0);
			}else{
				console.showWhiteAndBlacks(combination,getSecretCombination());
			}
		}
		console.out("You lose :(\n");
	}
	

	private boolean isWinner(Combination combination){
		int blacks = getSecretCombination().count("b", combination);
		if(blacks == 4){
			return true;
		}else{
			return false;
		}
	}
}

class Combination{
	
	private List<Color> colors;
	public final static int MAX_LENGTH = 4;

	public Combination(String colors){
		this.colors = parse(colors);
	}
	
	public Combination(List<Color> colors){
		this.colors = colors;
	}	
	public Combination(){}
	
	public List<Color> getColors(){
		return this.colors;
	}

	public void setColors(List<Color> colors) {
		this.colors = colors;
	}
	
	public List<Color> random(){
		List<Color> random = new ArrayList<>();
		for (int i = 0; i < MAX_LENGTH; i++) {
			random.add(Color.values()[(int)(Math.random()*4)]);
		}
		return random;
	}
	
	private List<Color> parse (String labelCombination){
		List<Color> combination = new ArrayList<>();
		for (String label : format(labelCombination)){
			combination.add(Color.valueOfLabel(label));
		}
		return combination;
	}
	
	public boolean isValid(String labelCombination){
		if(labelCombination.length() > 0 && labelCombination.length() == MAX_LENGTH){
			for (String label : format(labelCombination)) {
				if(!Color.containsLabel(label)){
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	private String[] format(String labelCombination){
		return labelCombination.trim().toLowerCase().split("");
	}

	public boolean equalTo(Combination combination){
		List<String> labes = Color.subtractLabels(combination.getColors());
		if(Color.subtractLabels(this.getColors()).equals(labes)){
			return true;
		}
		return false;
	}

}

class SecretCombination extends Combination {
	
	public SecretCombination(){
		this.setColors(super.random());
	}

	public int count(String label,Combination combination){
		int count = 0;
		for (String l : this.getResult(combination)) {
			if(l.contains(label)){
				count++;
			}
		}
		return count;
	}
	

	public List<String> getResult(Combination combination){
		if(this.equalTo(combination)){
			return Arrays.asList("b","b","b","b");
		}else{
			return this.getFeedBack(combination);
		}
	}

	private List<String> getFeedBack(Combination combination){
		List<String> result = new ArrayList<>();	
		for (int i = 0; i < Combination.MAX_LENGTH; i++) {
			if(this.getColors().get(i) == combination.getColors().get(i)){
				result.add("b");
			}else{
				if(this.getColors().contains(combination.getColors().get(i))){
					result.add("w");
				}
			}
		}
		return result;
	}
}


enum Color {
	ORANGE("o"),
	YELLOW("y"),
	PURPLE("p"),
	RED("r");

	private final String label;
	private static final Map<String,Color> LABELS = new HashMap<>();

	private Color(String label){
		this.label = label;
	}
	
	static {
		for (Color c : values()){
			LABELS.put(c.label, c);
		}
	}

	public static String  getColorsAvailables(){
		List<String> labelsAvailable = new ArrayList<>();
		for (String label : LABELS.keySet()) {
			labelsAvailable.add(label);
		}
		return labelsAvailable.toString();
	}
	public String getLabel(){
		return label;
	}

	public static Color valueOfLabel (String label){
		return LABELS.get(label);
	}

	public static boolean containsLabel(String label){
		if(LABELS.containsKey(label)){
			return true;
		}
		return false;
	}

	public static List<String> subtractLabels(List<Color> colors){
		List<String> labels = new ArrayList<>();
		for (Color c : colors) {
			labels.add(c.getLabel());
		}
		return labels;
	}
	
}


class Console {

	private static BufferedReader b = new BufferedReader(new InputStreamReader(System.in));

	public String inString() {
		String input = null;
		try {
			input = b.readLine();
		} catch (Exception e) {
			this.exit();
		}
		return input;
	}

	public String readString(String output) {
		this.out(output);
		String input = this.inString();
		return input;
	}
	
	public String readCombination(){
		Combination combination = new Combination();
		String propose = new String();
		do{
			propose = this.readString("Introduce combination: ");
			if(!combination.isValid(propose)){
				System.out.println("Combination Error! ");
			}
		}while(!combination.isValid(propose));

		return propose;
	}
	
	public void showWhiteAndBlacks(Combination combination, SecretCombination secret){
		 int whites = secret.count("w",combination);
         int blacks = secret.count("b",combination);
		 this.out("W: [" +whites + "] | B [: " +blacks + "]\n");
	}

	public void out(String out) {
		System.out.print(out);
	}

	private void exit() {
		System.out.println("ERROR I/O");
		System.exit(0);
	}
}
