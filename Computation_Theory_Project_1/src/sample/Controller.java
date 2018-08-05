package sample;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

public class Controller {

    public TextArea textarea;
    public TextField textfield;
    public int[][] positions ;
    public Automato automato;
    public Button reload;
    private boolean Loaded  = false ;
    @FXML
    private Button submitBtn;
    private Button chooserBtn;

    @FXML
    private Group root ;



    @FXML
    private void FileChooser() throws IOException {
        automato = new Automato(new ArrayList<Integer>() , 0, new ArrayList<Integer>() ,null);
        FileChooser chooser = new FileChooser();
        Charset encoding = Charset.defaultCharset();
        chooser.setTitle("Open File");
        File file = chooser.showOpenDialog(new Stage());
        if (file != null){
            Loaded = true ;
            root.getChildren().retainAll();
            automato =  handleFile(file ,encoding , automato);
            presentYourSelf(automato);
            positions = drawNDA(automato);

        }
    }

    private Automato handleFile(File file, Charset encoding , Automato automato)
            throws IOException {
        try (InputStream in = new FileInputStream(file);
             Reader reader = new InputStreamReader(in, encoding);
             // buffer for efficiency
             Reader buffer = new BufferedReader(reader)) {
            automato =  handleCharacters(buffer , automato);
        }
        return automato;
    }

    private Automato handleCharacters(Reader reader , Automato automato)
            throws IOException {
        int r;
        ArrayList<Character> arg = new ArrayList<>() ;
        boolean waitInitialState = false ;
        boolean waitFinishStates= false ;
        boolean waitNumberofStates = false ;
        boolean waitTransitions = false ;
        while ((r = reader.read()) != -1) {
            char ch = (char) r;
            // Every Automato starts with #
            if (ch == '#'){ waitNumberofStates = true;}
            else if (waitNumberofStates){ automato = this.createStates(ch , automato); waitNumberofStates = false ; }
            else  if (ch == '('){ waitFinishStates = true ;}
            else  if (waitFinishStates) {
                while (ch != ')') {
                    automato = FinishState(ch, automato);
                    ch =  (char) reader.read();
                }
                waitFinishStates = false ;
            }
            else  if(ch == '$') {waitInitialState = true ;}
            else if (waitInitialState) { automato = InitialState(ch , automato); waitInitialState = false ; }
            else if (ch == '[') {waitTransitions = true ; }
            else if (waitTransitions) {
                while (ch != ']') {

                    if ( ch != ',') {
                        arg.add(ch);
                    }else{
                        automato = setNDAtransation(arg, automato);
                        arg.clear();
                    }
                    ch = (char) reader.read();
                }
                waitTransitions = false ;
            }

        }


        return automato;
    }

    private Automato setNDAtransation(ArrayList<Character> arg, Automato automato) {
        try{
            int innerState  = Integer.valueOf(String.valueOf(arg.get(0)));
            int arow = Integer.valueOf(String.valueOf(arg.get(1)));
            int outerState  = Integer.valueOf(String.valueOf(arg.get(2)));
            automato.getTransitions()[innerState-1][outerState-1].add(arow)  ;

        }catch (Exception e){
            System.out.print("We have error with convert character to integer");
        }
        return automato;
    }
    private Automato createStates(char ch, Automato automato) {

        try{
            int size = Integer.valueOf(String.valueOf(ch));
            for (int i =  0; i < size ; i++){
                automato.getStates().add(i+1);
            }
            ArrayList<Integer>[][] transitions = new ArrayList[size][size];
            for (int i  = 0 ; i < size ; i++){
                for (int j = 0; j < size; j++){
                    transitions[i][j] = new ArrayList<Integer>();

                }
            }

            automato.setTransitions(transitions);
        }catch (Exception e){
            System.out.print("Problem with CreateStates");
        }
        return automato;
    }
    private Automato FinishState(char ch, Automato automato) {
        try{
            int finishState = Integer.valueOf(String.valueOf(ch));
            automato.getFinishState().add(finishState-1);

        }catch (Exception e){
            System.out.print("Problem with FinishState");
        }
        return automato;
    }
    private Automato InitialState(char ch  , Automato automato) {
        try{
            int initialState = Integer.valueOf(String.valueOf(ch));
            automato.setInitialState(initialState);

        }catch (Exception e){
            System.out.print("We have error with convert character to integer");
        }
        return automato;
    }
    private int[][] drawNDA(Automato automato){
        if (automato.getTransitions()==null) return null;
        int width = 30 ;
        int height = 120 ;
        int positions[][] = new int [automato.getStates().size()][2];
        for (int i = 0; i < automato.getStates().size(); i++) {


            positions[i][0] = width;
            positions[i][1] = height;
            Circle node = new Circle(width,height,25);
            if (automato.getInitialState() == i+1) {
                node.setStroke(Color.LIGHTBLUE);
                node.setStrokeWidth(15);
            }
           else  if (automato.getFinishState().contains(i)) {
                node.setStroke(Color.RED);
                node.setStrokeWidth(15);
            }
            else
                node.setFill(Color.rgb(155 ,155,200));
            root.getChildren().add(node);
            width = 150 + width;
        }
        for (int i = 0; i < automato.getTransitions().length; i++){
            for (int j = 0; j < automato.getTransitions().length; j++){
                for (int k = 0; k< automato.getTransitions()[i][j].size(); k++) {
                    if (1 == (int ) automato.getTransitions()[i][j].get(k)) {
                        if (positions[i][0] == positions[j][0] && positions[i][1] == positions[j][1]) {
                            Circle node = new Circle(positions[i][0], positions[i][1], 25);
                            node.setStroke(Color.rgb(0,0,50));
                            node.setStrokeWidth(8);
                            node.setFill(Color.rgb(155, 155, 200));
                            root.getChildren().add(node);
                            continue;
                        }
                        QuadCurve curve1 = new QuadCurve(positions[i][0], positions[i][1], positions[i][0] + 65, positions[i][1] + 100, positions[j][0], positions[j][1]);
                        curve1.setStroke(Color.rgb(0,0,50));
                        curve1.setStrokeWidth(3);
                        curve1.setFill(null);
                        root.getChildren().add(curve1);
                    } else if ( (int) automato.getTransitions()[i][j].get(k) == 0) {

                        if (positions[i][0] == positions[j][0] && positions[i][1] == positions[j][1]) {
                            Circle node = new Circle(positions[i][0], positions[i][1], 25);
                            node.setStroke(Color.BLUE);
                            node.setStrokeWidth(3);
                            node.setFill(Color.rgb(155, 155, 200));
                            root.getChildren().add(node);
                            continue;
                        }

                        QuadCurve curve1 = new QuadCurve(positions[i][0], positions[i][1], positions[i][0] + 65, positions[i][1] - 100, positions[j][0], positions[j][1]);
                        curve1.setStroke(Color.BLUE);
                        curve1.setStrokeWidth(3);
                        curve1.setFill(null);
                        root.getChildren().add(curve1);

                    } else continue;
                }
        }
    }
        root.getChildren().addAll();
        return positions;
    }
    public void presentYourSelf(Automato automato){

        textarea.appendText("States of Automato ");
        textarea.appendText("\n");
        textarea.appendText("\n");
        for (int i = 0; i < automato.getStates().size(); i++) textarea.appendText(" " + automato.getStates().get(i));
        textarea.appendText("\n");
        textarea.appendText("InitialState is  : " + automato.getInitialState());
        textarea.appendText("\n");
        textarea.appendText("\n");
        for (int i = 0; i < automato.getFinishState().size(); i++) textarea.appendText(" FinalStates is : "+ (automato.getFinishState().get(i)+1 )+"\n");
        textarea.appendText("\n");
        textarea.appendText("Transition ");
        textarea.appendText("\n");
        textarea.appendText("\n");
        textarea.appendText("LIGΗΤBLUE CIRCLE : represents  the initial state ");
        textarea.appendText("\n");
        textarea.appendText("\n");
        textarea.appendText("ORANGE CIRCLE : represents  the latest state of the automatic");
        textarea.appendText("\n");
        textarea.appendText("\n");
        textarea.appendText("BLUE CIRCLE : represents  a reflective transition with letter 0 ");
        textarea.appendText("\n");
        textarea.appendText("\n");
        textarea.appendText("DARK BLUE CIRCLE : represents  a reflective transition with letter 1 ");
        textarea.appendText("\n");
        textarea.appendText("\n");
        textarea.appendText("RED CIRCLE  :represents  final states");
        textarea.appendText("\n");
        textarea.appendText("\n");
        textarea.appendText("BLACK CIRCLE : represents states that the automatic was ");
        textarea.appendText("\n");
        textarea.appendText("\n");
        textarea.appendText("BLACK LINE : represents  a transition with letter 1 ");
        textarea.appendText("\n");
        textarea.appendText("\n");
        textarea.appendText("BLUE LINE : represents  a transition with letter 0 ");
        textarea.appendText("\n");
        textarea.appendText("\n");
        for (int i = 0; i < automato.getTransitions().length; i++){
            for (int j = 0; j < automato.getTransitions().length; j++){
                if(automato.getTransitions()[i][j].isEmpty()) continue;
                for (int k = 0; k< automato.getTransitions()[i][j].size(); k++)
                if( (int) automato.getTransitions()[i][j].get(k) == 0 || (int) automato.getTransitions()[i][j].get(k)== 1) {
                    textarea.appendText(" Transition from state  :   " + (i+1) + " to  state : " + (j+1) +  " with letter : " + automato.getTransitions()[i][j] );
                    textarea.appendText("\n");
                }

            }
        }
    }
    public ArrayList<Integer> diagnose(){

        String word = textfield.getText();
        String[] characters = word.split("(?!^)");
        ArrayList<Integer>  possible_transitions = new ArrayList<>();
        ArrayList<Integer>  plan = new ArrayList<>();
        int current = automato.getInitialState()-1;
        int[] intCharacters = getIntCharacters(characters);
        if (automato.getTransitions()==null) return null;
        for(int i = 0; i < automato.getTransitions().length; i++){
            for (int j = 0; j < automato.getTransitions().length; j++){
                System.out.print(automato.getTransitions()[i][j]);
            }
            System.out.print("\n");
        }
        for (int i = 0 ; i < intCharacters.length; i++){
             for (int states = 0; states < automato.getTransitions().length; states++) {
                 for (int k = 0; k < automato.getTransitions()[current][states].size(); k++) {
                     if (intCharacters[i] == (int) automato.getTransitions()[current][states].get(k)) {
                         possible_transitions.add(states);
                     }
                 }
             }
             if(!possible_transitions.isEmpty()) {
                 System.out.print("Choices when you are in " + current + "" + possible_transitions);
                 System.out.println("\n");
                 int index = new Random().nextInt(possible_transitions.size());
                 plan.add(possible_transitions.get(index));
                 current = possible_transitions.get(index);
                 possible_transitions.clear();
             }else return null;
        }
        System.out.print("The plan is : "  + plan);
        return plan;
    }
    private int[] getIntCharacters(String[] characters) {
        int[] a  = new int[characters.length];
        try{
            for (int i = 0; i < characters.length; i++){
                a[i] = Integer.valueOf(characters[i]);
            }
        }catch (Exception e){System.out.println("Problem diangose Method");}
        return a;
    }
    public void Simulation(ActionEvent actionEvent ) {
        if(!Loaded){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Automatic not found");
            alert.setContentText("You must first load a automatic and then submit a word");
            alert.showAndWait();
            return ;
        }
        ArrayList<Integer> plan = diagnose();
        if (plan == null){
            textarea.appendText("  \n");
            textarea.appendText("This word isn't diagnosed from this Automato ");
        }else {

            for (int i = 0; i<plan.size(); i++){
                try {
                    if(i !=0){
                        Circle node =  new Circle(positions[plan.get(i-1)][0] , positions[plan.get(i-1)][1] ,25);
                        node.setFill(Color.BLACK);
                        root.getChildren().add(node);
                    }
                    Circle node =  new Circle(positions[plan.get(i)][0] , positions[plan.get(i)][1] ,25);
                    node.setFill(Color.ORANGE);
                    Thread.sleep(100);
                    root.getChildren().add(node);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
            if (automato.getFinishState().contains(plan.get(plan.size()-1))){
                textarea.appendText("\n");
                textarea.appendText("This Automato diagnosed the word  : " + textfield.getText());
            }else{
                textarea.appendText("\n");
                textarea.appendText("This Automato  doesn't diagnosed the word  : " + textfield.getText());
            }
        }
    }
    public void Reload(ActionEvent actionEvent) {
        root.getChildren().retainAll();
        this.drawNDA(automato);
    }
}
