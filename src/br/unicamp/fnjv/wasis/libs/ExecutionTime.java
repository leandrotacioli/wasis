package br.unicamp.fnjv.wasis.libs;

/**
 * Calcula o tempo de execução de um determinado método, etc.
 * 
 * @author Leandro Tacioli
 *
 */
public class ExecutionTime {
	private long lgnInitialTime;
	private long lgnFinalTime;

	/**
	 * Calcula o tempo de execução de um determinado método, etc.
	 */
	public ExecutionTime() {
		
	}

	public void startExecution() {
		lgnInitialTime = System.currentTimeMillis();
	}
	
	public void finishExecution() {
		lgnFinalTime = System.currentTimeMillis();
		
		//System.out.printf("%.3f ms%n", (lgnFinalTime - lgnInitialTime) / 1000d);
		System.out.printf("%.3f %n", (lgnFinalTime - lgnInitialTime) / 1000d);
	}
}
