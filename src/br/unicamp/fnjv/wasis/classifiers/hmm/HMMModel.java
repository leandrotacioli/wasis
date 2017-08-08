package br.unicamp.fnjv.wasis.classifiers.hmm;

import java.io.Serializable;

import br.unicamp.fnjv.wasis.classifiers.hmm.db.Model;

public class HMMModel implements Serializable, Model {
	private static final long serialVersionUID = 4152898515994806868L;
	
	protected int intNumStates;
	protected int intNumObservations;
	protected int intNumObservationSequences;
	
	protected double initialProbabities[];
	protected double transitionStates[][];
	protected double outputStates[][];
	
	public HMMModel() {
		
	}

	public int getNumStates() {
		return intNumStates;
	}

	public void setNumStates(int intNumStates) {
		this.intNumStates = intNumStates;
	}

	public int getNumObservations() {
		return intNumObservations;
	}

	public void setNumObservations(int intNumObservations) {
		this.intNumObservations = intNumObservations;
	}

	public int getNumObservationSequences() {
		return intNumObservationSequences;
	}

	public void setNumObservationSequences(int intNumObservationSequences) {
		this.intNumObservationSequences = intNumObservationSequences;
	}
	
	public double[] getInitialProbabities() {
		return initialProbabities;
	}

	public void setInitialProbabities(double[] initialProbabities) {
		this.initialProbabities = initialProbabities;
	}

	public double[][] getTransitionStates() {
		return transitionStates;
	}

	public void setTransitionStates(double[][] transitionStates) {
		this.transitionStates = transitionStates;
	}

	public double[][] getOutputStates() {
		return outputStates;
	}
	
	public void setOutputStates(double[][] outputStates) {
		this.outputStates = outputStates;
	}
}