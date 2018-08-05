package sample;

import java.util.ArrayList;

public class Automato {
    private ArrayList<Integer> states ;
    private int initialState ;
    private ArrayList<Integer> finishState;
    private ArrayList<Integer>[][] transitions ;

    // Constructor

    public Automato(ArrayList<Integer> states, int initialState, ArrayList<Integer> finishState, ArrayList[][] transitions) {
        this.states = states;
        this.initialState = initialState;
        this.finishState = finishState;
        this.transitions = transitions;
    }

    public ArrayList<Integer> getStates() {
        return states;
    }

    public void setStates(ArrayList<Integer> states) {
        this.states = states;
    }

    public int getInitialState() {
        return initialState;
    }

    public void setInitialState(int initialState) {
        this.initialState = initialState;
    }

    public ArrayList[][] getTransitions() {
        return transitions;
    }

    public void setTransitions(ArrayList[][] transitions) {
        this.transitions = transitions;
    }

    public ArrayList<Integer> getFinishState() {
        return finishState;
    }

    public void setFinishState(ArrayList<Integer> finishState) {
        this.finishState = finishState;
    }

}
