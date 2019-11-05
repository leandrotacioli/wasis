/*
OC Volume - Java Speech Recognition Engine
Copyright (c) 2002-2004, OrangeCow organization
All rights reserved.

Redistribution and use in source and binary forms,
with or without modification, are permitted provided
that the following conditions are met:

 * Redistributions of source code must retain the
  above copyright notice, this list of conditions
  and the following disclaimer.
 * Redistributions in binary form must reproduce the
  above copyright notice, this list of conditions
  and the following disclaimer in the documentation
  and/or other materials provided with the
  distribution.
 * Neither the name of the OrangeCow organization
  nor the names of its contributors may be used to
  endorse or promote products derived from this
  software without specific prior written
  permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS
AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

Contact information:
Please visit http://ocvolume.sourceforge.net.
 */

package br.unicamp.fnjv.wasis.classifiers.hmm.vq;

import java.io.Serializable;

import br.unicamp.fnjv.wasis.classifiers.hmm.db.DataBase;
import br.unicamp.fnjv.wasis.classifiers.hmm.db.ObjectIODataBase;

/**
 * last updated on June 15, 2002<br>
 * <b>description:</b> Codebook for Vector Quantization component<br>
 * <b>calls:</b> Centroid, Points<br>
 * <b>called by:</b> volume, train<br>
 * <b>input:</b> speech signal<br>
 * <b>output:</b> set of centroids, set of indices
 * 
 * @author Danny Su
 * @author Andrei Leonov
 * 
 * @modified-by Ganesh Tiwari : DB Operations last updated on Dec-27,2010
 */
public class Codebook implements Serializable, Cloneable {
	private static final long serialVersionUID = 1243587036568272064L;
	
	/** split factor (should be in the range of 0.01 <= SPLIT <= 0.05) */
	protected final double SPLIT = 0.01;
	
	/** minimum distortion */
	protected final double MIN_DISTORTION = 0.1;
	
	/** Codebook size - number of codewords (codevectors) (default is: 256) */
	protected int intCodebookSize = 256;
	
	/** centroids array */
	protected Centroid centroids[];
	
	/** training points */
	protected Points points[];
	
	/** dimension = number of features */
	protected int dimension;
	
	protected boolean blnHasEnoughTrainingPoints;
	
	public boolean getHasEnoughTrainingPoints() {
		return blnHasEnoughTrainingPoints;
	}

	/**
	 * constructor to train a Codebook with given training points and Codebook
	 * size<br>
	 * calls: none<br>
	 * called by: trainCodebook
	 * 
	 * @param points - training vectors
	 * @param intCodebookSize - Codebook size
	 */
	public Codebook(Points[] points, int intCodebookSize) {
		this.points = points;
		this.intCodebookSize = intCodebookSize;

		// make sure there are enough training points to train the Codebook
		if (points.length >= intCodebookSize) {
			blnHasEnoughTrainingPoints = true;
			dimension = points[0].getDimension();
			initialize();
		} else {
			blnHasEnoughTrainingPoints = false;
			//System.out.println("err: not enough training points");
		}
	}

	/**
	 * Constructor to train a Codebook with given training points and
	 * default Codebook size (256).<br>
	 * calls: none<br>
	 * called by: trainCodebook
	 * 
	 * @param points - Training vectors
	 */
	public Codebook(Points[] points) {
		this.points = points;
		
		// make sure there are enough training points to train the Codebook
		if (points.length >= intCodebookSize) {
			blnHasEnoughTrainingPoints = true;
			dimension = points[0].getDimension();
			initialize();
		} else {
			blnHasEnoughTrainingPoints = false;
			//System.out.println("err: not enough training points");
		}
	}
	
	/**
	 * Constructor to load a saved Codebook from external file.<br>
	 * calls: Centroid<br>
	 * called by: volume
	 */
	public Codebook(String strFileName) {
		DataBase objDb = new ObjectIODataBase();
		objDb.setType("cbk");
		
		CodebookDictionary objCodebookDictionary = new CodebookDictionary();
		objCodebookDictionary = (CodebookDictionary) objDb.readModel("", strFileName);
		
		dimension = objCodebookDictionary.getDimension();
		centroids = objCodebookDictionary.getCentroid();
	}

	/**
	 * Constructor to load a saved Codebook from external file.<br>
	 * calls: Centroid<br>
	 * called by: volume
	 */
	public Codebook() {
		//DataBase objDb = new ObjectIODataBase();
		//objDb.setType("cbk");
		
		//CodebookDictionary objCodebookDictionary = new CodebookDictionary();
		//objCodebookDictionary = (CodebookDictionary) objDb.readModel(null, null);
		
		//dimension = objCodebookDictionary.getDimension();
		//centroids = objCodebookDictionary.getCentroid();
	}
	
	/**
	 * Creates a Codebook using LBG algorithm which includes K-means.<br>
	 * calls: Centroid<br>
	 * called by: Codebook
	 */
	protected void initialize() {
		double dblDistortionBeforeUpdate = 0; // distortion measure before updating centroids
		double dblDistortionAfterUpdate = 0;  // distortion measure after update centroids

		// design a 1-vector Codebook
		centroids = new Centroid[1];
		
		// then initialize it with (0, 0) coordinates
		double[] origin = new double[dimension];
		centroids[0] = new Centroid(origin);
		
		// initially, all training points will belong to 1 single cell
		for (int i = 0; i < points.length; i++) {
			centroids[0].add(points[i], 0);
		}

		// calls update to set the initial codevector as the average of all points
		centroids[0].update();

		// Iteration 1: repeat splitting step and K-means until required number of codewords is reached
		while (centroids.length < intCodebookSize) {
			// split codevectors by a binary splitting method
			split();
			
			// group training points to centroids closest to them
			groupPointsToCells();

			// Iteration 2: perform K-means algorithm
			do {
				for (int i = 0; i < centroids.length; i++) {
					dblDistortionBeforeUpdate += centroids[i].getDistortion();
					centroids[i].update();
				}
				
				// regroup
				groupPointsToCells();
				
				for (int i = 0; i < centroids.length; i++) {
					dblDistortionAfterUpdate += centroids[i].getDistortion();
				}

			} while (Math.abs(dblDistortionAfterUpdate - dblDistortionBeforeUpdate) < MIN_DISTORTION);
		}
	}

	/**
	 * Save Codebook to cbk object file.<br>
	 * calls: none<br>
	 * called by: train
	 */
	@SuppressWarnings("unused")
	private void saveToFile(String strFilePath, String strFileName) {
		DataBase objDB = new ObjectIODataBase();
		objDB.setType("cbk");
		
		CodebookDictionary objCodebookDictionary = new CodebookDictionary();
		
		// No need to save all the points,
		// Must be removed in objectIO, to reduce the size of file
		for (int i = 0; i < centroids.length; i++) {
			centroids[i].pts.removeAllElements();
		}
		
		objCodebookDictionary.setDimension(dimension);
		objCodebookDictionary.setCentroid(centroids);
		
		objDB.saveModel(objCodebookDictionary, strFilePath, strFileName);
	}
	
	public CodebookDictionary getCodebookDictionary() {
		CodebookDictionary objCodebookDictionary = new CodebookDictionary();
		
		// No need to save all the points,
		// Must be removed in objectIO, to reduce the size of file
		for (int i = 0; i < centroids.length; i++) {
			centroids[i].pts.removeAllElements();
		}
		
		objCodebookDictionary.setDimension(dimension);
		objCodebookDictionary.setCentroid(centroids);
		
		return objCodebookDictionary;
	}
	
	public void loadCodebookDictionary(CodebookDictionary objCodebookDictionary) {
		dimension = objCodebookDictionary.getDimension();
		centroids = objCodebookDictionary.getCentroid();
	}

	/**
	 * Splitting algorithm to increase number of centroids by multiple of 2.<br>
	 * calls: Centroid<br>
	 * called by: Codebook
	 */
	protected void split() {
		//System.out.println("Centroids length now becomes " + centroids.length + 2);
		
		Centroid temp[] = new Centroid[centroids.length * 2];
		double tCo[][];
		
		for (int i = 0; i < temp.length; i += 2) {
			tCo = new double[2][dimension];
			
			for (int j = 0; j < dimension; j++) {
				tCo[0][j] = centroids[i / 2].getCoordinate(j) * (1 + SPLIT);
			}
			
			temp[i] = new Centroid(tCo[0]);
			
			for (int j = 0; j < dimension; j++) {
				tCo[1][j] = centroids[i / 2].getCoordinate(j) * (1 - SPLIT);
			}
			
			temp[i + 1] = new Centroid(tCo[1]);
		}

		// replace old centroids array with new one
		centroids = new Centroid[temp.length];
		centroids = temp;
	}

	/**
	 * Quantize the input array of points in k-dimensional space.<br>
	 * calls: none<br>
	 * called by: volume
	 * 
	 * @param points
	 * @return quantized index array
	 */
	public int[] quantize(Points points[]) {
		int output[] = new int[points.length];
		
		for (int i = 0; i < points.length; i++) {
			output[i] = closestCentroidToPoint(points[i]);
		}
		
		return output;
	}

	/**
	 * Calculates the distortion.<br>
	 * calls: none<br>
	 * called by: volume
	 * 
	 * @param points
	 * 
	 * @return distortion measure
	 */
	public double getDistortion(Points points[]) {
		double dblDistortion = 0;
		
		for (int i = 0; i < points.length; i++) {
			int index = closestCentroidToPoint(points[i]);
			double d = getDistance(points[i], centroids[index]);
			
			dblDistortion += d;
		}
		
		return dblDistortion;
	}

	/**
	 * Finds the closest Centroid to a specific Points.<br>
	 * calls: none<br>
	 * called by: Codebook
	 * 
	 * @param points
	 * 
	 * @return index number of the closest Centroid
	 */
	private int closestCentroidToPoint(Points points) {
		int intLowestIndex = 0;
		
		double dblTempDist = 0;
		double dblLowestDist = 0;
		
		for (int i = 0; i < centroids.length; i++) {
			dblTempDist = getDistance(points, centroids[i]);
			
			if (dblTempDist < dblLowestDist || i == 0) {
				dblLowestDist = dblTempDist;
				intLowestIndex = i;
			}
		}
		
		return intLowestIndex;
	}

	/**
	 * Finds the closest Centroid to a specific Centroid.<br>
	 * calls: none<br>
	 * called by: Codebook
	 * 
	 * @param centroid
	 * 
	 * @return index number of the closest Centroid
	 */
	private int closestCentroidToCentroid(Centroid centroid) {
		int intLowestIndex = 0;
		
		double dblTempDist = 0;
		double dblLowestDist = Double.MAX_VALUE;
		
		for (int i = 0; i < centroids.length; i++) {
			dblTempDist = getDistance(centroid, centroids[i]);
			
			if (dblTempDist < dblLowestDist && centroids[i].getNumPts() > 1) {
				dblLowestDist = dblTempDist;
				intLowestIndex = i;
			}
		}
		
		return intLowestIndex;
	}

	/**
	 * Finds the closest Points in c2's cell to c1.<br>
	 * calls: none<br>
	 * called by: Codebook
	 * 
	 * @param c1 - First Centroid
	 * @param c2 - Second Centroid
	 * 
	 * @return index of Points
	 */
	private int closestPoint(Centroid c1, Centroid c2) {
		int intLowestIndex = 0;
		
		double dblTempDist = 0;
		double dblLowestDist = getDistance(c2.getPoint(0), c1);

		for (int i = 1; i < c2.getNumPts(); i++) {
			dblTempDist = getDistance(c2.getPoint(i), c1);
			
			if (dblTempDist < dblLowestDist) {
				dblLowestDist = dblTempDist;
				intLowestIndex = i;
			}
		}
		
		return intLowestIndex;
	}

	/**
	 * Grouping points to cells.<br>
	 * calls: none<br>
	 * called by: Codebook
	 */
	private void groupPointsToCells() {
		// find closest Centroid and assign Points to it
		for (int i = 0; i < points.length; i++) {
			int index = closestCentroidToPoint(points[i]);
			centroids[index].add(points[i], getDistance(points[i], centroids[index]));
		}
		
		// make sure that all centroids have at least one Points assigned to it
		// no cell should be empty or else NaN error will occur due to division
		// of 0 by 0
		for (int i = 0; i < centroids.length; i++) {
			if (centroids[i].getNumPts() == 0) {
				// find the closest Centroid with more than one points assigned to it
				int index = closestCentroidToCentroid(centroids[i]);
				
				// find the closest Points in the closest Centroid's cell
				int closestIndex = closestPoint(centroids[i], centroids[index]);
				Points closestPt = centroids[index].getPoint(closestIndex);
				
				centroids[index].remove(closestPt, getDistance(closestPt, centroids[index]));
				centroids[i].add(closestPt, getDistance(closestPt, centroids[i]));
			}
		}
	}

	/**
	 * calculates the distance of a Points to a Centroid.<br>
	 * calls: none<br>
	 * called by: Codebook
	 * 
	 * @param points
	 * @param centroid
	 */
	private double getDistance(Points points, Centroid centroid) {
		double dblDistance = 0;
		double dblTemp = 0;
		
		for (int i = 0; i < dimension; i++) {
			dblTemp = points.getCoordinate(i) - centroid.getCoordinate(i);
			dblDistance += dblTemp * dblTemp;
		}
		
		dblDistance = Math.sqrt(dblDistance);
		
		return dblDistance;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}