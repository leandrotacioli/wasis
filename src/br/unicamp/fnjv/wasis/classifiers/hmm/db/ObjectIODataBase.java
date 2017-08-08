/*
  Please feel free to use/modify this class. 
  If you give me credit by keeping this information or
  by sending me an email before using it or by reporting bugs , i will be happy.
  Email : gtiwari333@gmail.com,
  Blog : http://ganeshtiwaridotcomdotnp.blogspot.com/ 
 */
package br.unicamp.fnjv.wasis.classifiers.hmm.db;

import java.io.File;

import br.unicamp.fnjv.wasis.classifiers.hmm.HMMModel;
import br.unicamp.fnjv.wasis.classifiers.hmm.vq.CodebookDictionary;


/**
 * 
 * @author Ganesh Tiwari
 * 
 */
public class ObjectIODataBase implements DataBase {
    /** Type of current model,gmm,hmm,cbk, which is extension of the saved file */
    String strType;
    
    String[] modelFiles;
    String[] userNames;
    String CURRENTFOLDER;
    
    String currentModelType;
    
    /**
     * MAKE SURE THAT Files are/will be in this folder structure
     * the folder structure for training :
     * (Selected)DBROOTFOLDER\
     * \speechTrainWav\\apple\\apple01.wav
     * \speechTrainWav\\apple\\apple02.wav
     * \speechTestWav\\cat\\cat01.wav
     * \speechTestWav\\cat\\cat01.wav
     * \speechTestWav\\cat\\cat01.wav
     * \codeBook\\codeBook.cbk
     * \models\\HMM\\apple.hmm
     * \models\\HMM\\cat.hmm
     * \models\\GMM\\ram.gmm
     * \models\\GMM\\shyam.gmm
     */
    public ObjectIODataBase() {
    	
    }

    /**
     *
     * @param type type of the model, valid entry are either gmm, hmm, or cbk
     */
    public void setType(String strType) {
        this.strType = strType;
        
        if (strType.equalsIgnoreCase("hmm")) {
            CURRENTFOLDER = "Models\\HMM";
        } else if (strType.equalsIgnoreCase("cbk")) {
            CURRENTFOLDER = "Models\\Codebook";
        }
    }

    /**
     *
     */
    @Override
    public Model readModel(String strFilePath, String strFileName) {
        Model objModel = null;
        
        // HMM
        if (strType.equalsIgnoreCase("hmm")) {
            ObjectIO<HMMModel> oio = new ObjectIO<HMMModel>();
            
            objModel = new HMMModel();
            objModel = oio.readModel(CURRENTFOLDER + "\\" + strFilePath + "\\" + strFileName + "." + strType);
            
        // Codebook
        } else if (strType.equalsIgnoreCase("cbk")) {
            ObjectIO<CodebookDictionary> oio = new ObjectIO<CodebookDictionary>();
            
            objModel = new CodebookDictionary();
            objModel = oio.readModel(CURRENTFOLDER + "\\" + strFileName + "." + strType);
        }
        
        return objModel;
    }

    /**
     *
     */
    @Override
    public String[] readRegistered(String strFileName) {
        modelFiles = readRegisteredWithExtension(strFileName);

        return removeExtension(modelFiles);
    }

    /**
     *
     */
    @Override
    public void saveModel(Model objModel, String strFilePath, String strFileName) {
    	// HMM
        if (strType.equalsIgnoreCase("hmm")) {
        	File fileDirectory = new File(CURRENTFOLDER + "\\" + strFilePath);
    		
            if (!fileDirectory.exists()) {
                fileDirectory.mkdir();
            }
        	
            ObjectIO<HMMModel> oio = new ObjectIO<HMMModel>();
            oio.setModel((HMMModel) objModel);
            oio.saveModel(CURRENTFOLDER + "\\" + strFilePath + "\\" + strFileName + "." + strType);
            
        // Codebook
        } else if (strType.equalsIgnoreCase("cbk")) {
            ObjectIO<CodebookDictionary> oio = new ObjectIO<CodebookDictionary>();
            oio.setModel((CodebookDictionary) objModel);
            oio.saveModel(CURRENTFOLDER + "\\" + strFileName + "." + strType);
        }
    }

    private String[] readRegisteredWithExtension(String strFileName) {
        File modelPath = new File(CURRENTFOLDER + "\\" + strFileName);
        
        modelFiles = new String[modelPath.list().length];
        modelFiles = modelPath.list();    // Must return only folders
        
        return modelFiles;
    }

    /**
     * 
     * @param modelFiles
     * 
     * @return
     */
    private String[] removeExtension(String[] modelFiles) {
        String[] noExtension = new String[modelFiles.length];
        
        for (int i = 0; i < modelFiles.length; i++) {
            noExtension[i] = modelFiles[i].substring(0, modelFiles[i].length() - 4);  // TODO:check the lengths
        }
        
        return noExtension;
    }
}
