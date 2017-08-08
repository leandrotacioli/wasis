/*
  Please feel free to use/modify this class. 
  If you give me credit by keeping this information or
  by sending me an email before using it or by reporting bugs , i will be happy.
  Email : gtiwari333@gmail.com,
  Blog : http://ganeshtiwaridotcomdotnp.blogspot.com/ 
 */
package br.unicamp.fnjv.wasis.classifiers.hmm.vq;

import br.unicamp.fnjv.wasis.classifiers.hmm.db.Model;

import java.io.Serializable;

/**
 * 
 * @author Ganesh Tiwari
 * 
 */
public class CodebookDictionary implements Serializable, Model {
	private static final long serialVersionUID = -1129974725687634649L;
	
	protected int intDimension;
	protected Centroid[] centroid;

	public CodebookDictionary() {
		
	}

	public int getDimension() {
		return intDimension;
	}

	public void setDimension(int intDimension) {
		this.intDimension = intDimension;
	}

	public Centroid[] getCentroid() {
		return centroid;
	}

	public void setCentroid(Centroid[] centroid) {
		this.centroid = centroid;
	}
}
