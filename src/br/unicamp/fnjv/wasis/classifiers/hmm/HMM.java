package br.unicamp.fnjv.wasis.classifiers.hmm;

import java.text.DecimalFormat;

import br.unicamp.fnjv.wasis.classifiers.hmm.db.DataBase;
import br.unicamp.fnjv.wasis.classifiers.hmm.db.ObjectIODataBase;

/**
* This class represents a left-to-right Hidden Markov Model
* and its essential methods for speech recognition. The collection of methods
* include Forward-Backward Algorithm, Baum-Welch Algorithm and Viterbi.
*
* @author Danny Su
* 
* @modified-by Leandro Tacioli - 23/Jun/2016
*/
public class HMM {
	/** Number of states.<br>
	 * <br>
	 * <b>Example:</b> Number of urns. */ 
	private int intNumStates;
	
	/** Number of observation symbols per state.<br>
	 * <br>
	 * <b>Example:</b> How many different colour balls there are. */ 
	private int intNumObservations;
	
	/** Number of observation sequences */
	private int intNumObservationSequences;
	
	/** Length of a observation sequence */
	private int intLengthObservationSequence;
	
	/** Current observation sequence (processing) */
	private int[] currentObservationSequence;
	
	/** Discrete set of observation */
	private int[][] observationSequence;
	
	/** The initial probabilities for each state: initialProbabities[state] */ 
	private double[] initialProbabities;
	
	/** The state change probability to switch from state A to * state B: transitionStates[stateA][stateB] */ 
	private double[][] transitionStates;
	
	/** The probability to emit symbol S in state A: outputStates[stateA][symbolS] */ 
	private double[][] outputStates;
	
	/** Forward Alpha */
	private double[][] forwardAlpha;
	
	/** Backward Beta */
	private double[][] backwardBeta;
	
	/** Scale Coefficient */
	private double[] scaleFactor;
	
	/** Variable for Viterbi algorithm */
	private int psi[][];
	
	/** Number of states the model is allowed to jump */
	private final int JUMP_STATES = 2;
	
	/** Minimum probability */
	private final double MIN_PROBABILITY = 0.0001;
	
	/**
	 * HMM - Creates a left-to-right model with multiple
	 * observation sequences for training
	 * 
	 * @param intNumStates - Number of states
	 * @param intNumObservations - Number of observations
	 */
	public HMM(int intNumStates, int intNumObservations) {
		this.intNumStates = intNumStates;
		this.intNumObservations = intNumObservations;
		
		initialProbabities = new double[intNumStates];
		transitionStates = new double[intNumStates][intNumStates];
		outputStates = new double[intNumStates][intNumObservations];
		
		setProbabilities();
	}
	
	/**
	 * Create a model from a saved file.
	 * 
	 * @param strWord - Path of the file to load
	 */
	public HMM(String strFilePath, String strFileName) {
		DataBase objDb = new ObjectIODataBase();
		objDb.setType("hmm");
		
		HMMModel objModel = new HMMModel();
		objModel = (HMMModel) objDb.readModel(strFilePath, strFileName);
		
		intNumObservationSequences = objModel.getNumObservationSequences();
		
		initialProbabities = objModel.getInitialProbabities();
		transitionStates = objModel.getTransitionStates();
		outputStates = objModel.getOutputStates();
		
		intNumStates = outputStates.length;
		intNumObservations = outputStates[0].length;
	}
	
	/**
	 * Generates random probabilities for transition and output states.
	 */
	private void setProbabilities() {
		// Set initial probability
		// In a left-to-right HMM model, the first state is always the initial state. (e.g. probability = 1)
		initialProbabities[0] = 1; 
		
		for (int i = 1; i < intNumStates; i++) { 
			initialProbabities[i] = 0; 
		}
		
		// Generates random probabilities
		for (int i = 0; i < intNumStates; i++) {
			// Transition states
			for (int j = 0; j < intNumStates; j++) {
				if (j < i || j > i + JUMP_STATES) {
					transitionStates[i][j] = 0;     // R-L prob=0 for L-R HMM, and with Delta
				} else {
					transitionStates[i][j] = Math.random();
				}
			}
			
			// Output states
			for (int j = 0; j < intNumObservations; j++) {
				outputStates[i][j] = Math.random();
			}
		}
		
		/*
		// Set transition states in the left-to-right version 
		// NOTE: i now that this is dirty and very static. :) 
		for (int i = 0; i < intNumStates; i++) { 
			for(int j = 0; j < intNumStates; j++) { 
				if (i == intNumStates - 1 && j == intNumStates - 1) {          // last row 
					transitionStates[i][j] = 1.0;
				} else if (i == intNumStates - 2 && j == intNumStates - 2) {   // next to last row 
					transitionStates[i][j] = 0.5;
				} else if (i == intNumStates - 2 && j == intNumStates - 1) {   // next to last row 
					transitionStates[i][j] = 0.5;
				} else if (i <= j && i > j - JUMP_STATES - 1) { 
					transitionStates[i][j] = 1.0 / (JUMP_STATES + 1); 
				} else {
					transitionStates[i][j] = 0;
				}
			}
		}
		
		// Output probability 
		for (int i = 0; i < intNumStates; i++) { 
			for (int j = 0; j < intNumObservations; j++) { 
				outputStates[i][j] = 1.0 / (double) intNumObservations; 
			}
		}
		*/
	}
	
	/**
	 * Train the HMM model.
	 * 
	 * @param trainSequences
	 * @param intSteps
	 */
	public void train(int[][] trainSequences, int intSteps) {
		intNumObservationSequences = trainSequences.length;
		observationSequence = new int[intNumObservationSequences][];
		
		for (int indexObservationSequence = 0; indexObservationSequence < intNumObservationSequences; indexObservationSequence++) {
			observationSequence[indexObservationSequence] = trainSequences[indexObservationSequence];
		}
		
		// Execute training according to the number of steps
		for (int indexStep = 1; indexStep <= intSteps; indexStep++) {
			executeTraining();
		}
	}
	
	/**
	 * Baum-Welch Algorithm.<br>
	 * Re-estimate (iterative update and improvement) of HMM parameters.
	 */
	private void executeTraining() {
		double transitionStatesTrain[][] = new double[intNumStates][intNumStates];
		double outputStatesTrain[][] = new double[intNumStates][intNumObservations];
		
		double numerator[] = new double[intNumObservationSequences];
		double denominator[] = new double[intNumObservationSequences];
		
		double dblProbability = 0;
		
		// Calculate new transition probability matrix
		for (int i = 0; i < intNumStates; i++) {
			for (int j = 0; j < intNumStates; j++) {
				
				if (j < i || j > i + JUMP_STATES) {
					transitionStatesTrain[i][j] = 0;
		
				} else {
					for (int k = 0; k < intNumObservationSequences; k++) {
						numerator[k] = 0;
						denominator[k] = 0;
						
						setObservationSequence(observationSequence[k]);
						
						dblProbability += computeForwardAlpha();
						computeBackwardBeta();
						
						for (int t = 0; t < intLengthObservationSequence - 1; t++) {
							numerator[k] += forwardAlpha[t][i] * transitionStates[i][j] * outputStates[j][currentObservationSequence[t + 1]] * backwardBeta[t + 1][j];
							denominator[k] += forwardAlpha[t][i] * backwardBeta[t][i];
						}
					}
					
					double dblDenominator = 0;
					
					for (int k = 0; k < intNumObservationSequences; k++) {
						transitionStatesTrain[i][j] += (1 / dblProbability) * numerator[k];
						dblDenominator += (1 / dblProbability) * denominator[k];
					}
					
					transitionStatesTrain[i][j] /= dblDenominator;
					transitionStatesTrain[i][j] += MIN_PROBABILITY;
				}
			}
		}
		
		// Calculate new output probability matrix
		dblProbability = 0;
		
		for (int i = 0; i < intNumStates; i++) {
			for (int j = 0; j < intNumObservations; j++) {
				for (int k = 0; k < intNumObservationSequences; k++) {
					numerator[k] = 0;
					denominator[k] = 0;
					
					setObservationSequence(observationSequence[k]);
		
					dblProbability += computeForwardAlpha();
					computeBackwardBeta();
		
					for (int t = 0; t < intLengthObservationSequence - 1; t++) {
						if (currentObservationSequence[t] == j) {
							numerator[k] += forwardAlpha[t][i] * backwardBeta[t][i];
						}
		
						denominator[k] += forwardAlpha[t][i] * backwardBeta[t][i];
					}
				}
				
				double dblDenominator = 0;
				
				for (int k = 0; k < intNumObservationSequences; k++) {
					outputStatesTrain[i][j] += (1 / dblProbability) * numerator[k];
					dblDenominator += (1 / dblProbability) * denominator[k];
				}
				
				outputStatesTrain[i][j] /= dblDenominator;
				outputStatesTrain[i][j] += MIN_PROBABILITY;
			}
		}
		
		transitionStates = transitionStatesTrain;
		outputStates = outputStatesTrain;
	}
	
	/**
	 * Calculate Forward Alpha.
	 *
	 * @return dblProbability - Probability of the observation sequence
	 */
	private double computeForwardAlpha() {
		double dblProbability = 0;
		
		// *********************************************************************
		// Reset ScaleFactor
		for (int indexObservationSequence = 0; indexObservationSequence < intLengthObservationSequence; indexObservationSequence++) {
			scaleFactor[indexObservationSequence] = 0;
		}
		
		// *********************************************************************
		// Initialization: Calculating all forward variables at time = 0
		for (int indexState = 0; indexState < intNumStates; indexState++) {
			forwardAlpha[0][indexState] = initialProbabities[indexState] * outputStates[indexState][currentObservationSequence[0]];
		}
		
		rescaleForwardAlpha(0);
		
		// *********************************************************************
		// Induction
		for (int t = 0; t < intLengthObservationSequence - 1; t++) {
			for (int j = 0; j < intNumStates; j++) {
				double dblSum = 0;
		
				for (int i = 0; i < intNumStates; i++) {
					dblSum += forwardAlpha[t][i] * transitionStates[i][j];
				}
				
				forwardAlpha[t + 1][j] = dblSum * outputStates[j][currentObservationSequence[t + 1]];
			}
			
			rescaleForwardAlpha(t + 1);
		}
	
		// *********************************************************************
		// Calculate Probability
		for (int indexState = 0; indexState < intNumStates; indexState++) {
			dblProbability += forwardAlpha[intLengthObservationSequence - 1][indexState];
		}
		
		dblProbability = 0;
		
		for (int indexObservationSequence = 0; indexObservationSequence < intLengthObservationSequence; indexObservationSequence++) {
			dblProbability += Math.log(scaleFactor[indexObservationSequence]);
		}
	
		return -dblProbability;
	}
	
	/**
	 * Rescales Forward Alpha to prevent underflow.
	 * 
	 * @param indexFwd - Index number of forward variable alpha
	 */
	private void rescaleForwardAlpha(int indexForward) {
		// Calculate scale coefficients
		for (int indexState = 0; indexState < intNumStates; indexState++) {
			scaleFactor[indexForward] += forwardAlpha[indexForward][indexState];
		}
		
		scaleFactor[indexForward] = 1 / scaleFactor[indexForward];
	
		// Apply scale coefficients
		for (int indexState = 0; indexState < intNumStates; indexState++) {
			forwardAlpha[indexForward][indexState] *= scaleFactor[indexForward];
		}
	}
	
	/**
	 * Calculate Backward Beta for later use with Re-Estimation method.
	 */
	private void computeBackwardBeta() {
		// *********************************************************************
		// Initialization: Set all backward variables to 1 at time = intLengthObservationSequence - 1
		for (int indexState = 0; indexState < intNumStates; indexState++) {
			backwardBeta[intLengthObservationSequence - 1][indexState] = 1;
		}
		
		rescaleBackwardBeta(intLengthObservationSequence - 1);
		
		// *********************************************************************
		// Induction
		for (int t = intLengthObservationSequence - 2; t >= 0; t--) {
			for (int i = 0; i < intNumStates; i++) {
				for (int j = 0; j < intNumStates; j++) {
					backwardBeta[t][i] += transitionStates[i][j] * outputStates[j][currentObservationSequence[t + 1]] * backwardBeta[t + 1][j];
				}
			}
			
			rescaleBackwardBeta(t);
		}
	}
	
	/**
	 * Rescales Backward Beta to prevent underflow.
	 * 
	 * @param indexBackward - Index number of Backward Beta
	 */
	private void rescaleBackwardBeta(int indexBackward) {
		for (int indexState = 0; indexState < intNumStates; indexState++) {
			backwardBeta[indexBackward][indexState] *= scaleFactor[indexBackward];
		}
	}
	
	/**
	 * Returns the probability calculated from the testing sequence.
	 *
	 * @param testingSequence
	 * 
	 * @return Probability of observation sequence given the model
	 */
	public double getProbability(int testingSequence[]) {
		setObservationSequence(testingSequence);
		
		return computeForwardAlpha();
	}
	
	/**
	 * Set observation sequence.
	 *
	 * @param observationSequence
	 */
	private void setObservationSequence(int observationSequence[]) {
		currentObservationSequence = observationSequence;
		intLengthObservationSequence = observationSequence.length;
		
		forwardAlpha = new double[intLengthObservationSequence][intNumStates];
		backwardBeta = new double[intLengthObservationSequence][intNumStates];
		scaleFactor = new double[intLengthObservationSequence];
	}
	
	/**
	 * Viterbi Algorithm used to get best state sequence and probability.
	 *
	 * @param testSequence
	 * 
	 * @return probability
	 */
	public double viterbi(int testSequence[]) {
		setObservationSequence(testSequence);
		
		double phi[][] = new double[intLengthObservationSequence][intNumStates];
		
		psi = new int[intLengthObservationSequence][intNumStates];
		
		int q[] = new int[intLengthObservationSequence];
		
		for (int i = 0; i < intNumStates; i++) {
			double dblTemp = initialProbabities[i];
		
			if (dblTemp == 0) {
				dblTemp = MIN_PROBABILITY;
			}
		
			phi[0][i] = Math.log(dblTemp) + Math.log(outputStates[i][currentObservationSequence[0]]);
			psi[0][i] = 0;
		}
		
		for (int t = 1; t < intLengthObservationSequence; t++) {
			for (int j = 0; j < intNumStates; j++) {
				double dblMax = phi[t - 1][0] + Math.log(transitionStates[0][j]);
				double dblTemp = 0;
				int intIndex = 0;
				
				for (int i = 1; i < intNumStates; i++) {
					dblTemp = phi[t - 1][i] + Math.log(transitionStates[i][j]);
		
					if (dblTemp > dblMax) {
						dblMax = dblTemp;
						intIndex = i;
					}
				}
		
				phi[t][j] = dblMax + Math.log(outputStates[j][currentObservationSequence[t]]);
				psi[t][j] = intIndex;
			}
		}
		
		double dblMax = phi[intLengthObservationSequence - 1][0];
		double dblTemp = 0;
		int intIndex = 0;
		
		for (int i = 1; i < intNumStates; i++) {
			dblTemp = phi[intLengthObservationSequence - 1][i];
		
			if (dblTemp > dblMax) {
				dblMax = dblTemp;
				intIndex = i;
			}
		}
		
		q[intLengthObservationSequence - 1] = intIndex;
		
		for (int t = intLengthObservationSequence - 2; t >= 0; t--) {
			q[t] = psi[t + 1][q[t + 1]];
		}
		
		if (Double.isNaN(dblMax)) {
			dblMax = -10000000;
		}
		
		return dblMax;
	}
	
	/**
	 * Save HMM model to file.
	 * 
	 * @param strFilePath
	 * @param strFileName
	 */
	public void save(String strFilePath, String strFileName) {
		HMMModel objModel = new HMMModel();
		objModel.setInitialProbabities(initialProbabities);
		objModel.setTransitionStates(transitionStates);
		objModel.setOutputStates(outputStates);
		
		DataBase objDB = new ObjectIODataBase();
		objDB.setType("hmm");
		objDB.saveModel(objModel, strFilePath, strFileName);
	}
	
	/**
	 * Print all values of the HMM model.
	 */
	public void printValues() {
		DecimalFormat fmt = new DecimalFormat(); 
		fmt.setMinimumFractionDigits(5); 
		fmt.setMaximumFractionDigits(5); 
		
		for (int i = 0; i < intNumStates; i++) {
			System.out.println("initialProbabities(" + i + ") = " + fmt.format(initialProbabities[i]));
		}
		
		System.out.println(""); 
		
		for (int i = 0; i < intNumStates; i++) {
			for (int j = 0; j < intNumStates; j++) {
				System.out.print(fmt.format(transitionStates[i][j]) + "	");
			}
		
			System.out.println(""); 
		} 
		
		System.out.println(""); 
		
		for (int i = 0; i < intNumStates; i++) { 
			for (int k = 0; k < intNumObservations; k++) {
				System.out.print(fmt.format(outputStates[i][k]) + "	"); 
			}
		
			System.out.println("");
		} 
	}
}