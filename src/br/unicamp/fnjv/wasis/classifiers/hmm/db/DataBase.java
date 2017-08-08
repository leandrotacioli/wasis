/*
  Please feel free to use/modify this class. 
  If you give me credit by keeping this information or
  by sending me an email before using it or by reporting bugs , i will be happy.
  Email : gtiwari333@gmail.com,
  Blog : http://ganeshtiwaridotcomdotnp.blogspot.com/ 
 */
package br.unicamp.fnjv.wasis.classifiers.hmm.db;

public interface DataBase {
	public String[] readRegistered(String strFileName);
	
	public Model readModel(String strFilePath, String strFileName);
	
	public void setType(String strType);

	public void saveModel(Model objModel, String strFilePath, String strFileName);
}
