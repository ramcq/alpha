package org.ucam.ned.teamalpha.shell;

public class Shell {
	static Shell myself = null;
	
	public static void main(String[] args) {
		Shell me;
		me = Shell.getInstance();
		me.go();
	}

	public static Shell getInstance() {
		if (!(myself instanceof Shell))
			myself = new Shell();

		return myself;
	}

	public void go() {
		try {
			Class algorithm = Class.forName("org.ucam.ned.teamalpha.algorithms.Algorithm");
			Class[] algorithms = algorithm.getDeclaredClasses();
			for (int i = 0; i < algorithms.length; i++) {
				System.out.println(algorithms[i]);
			}
		} catch (ClassNotFoundException e) {
			System.err.println(e);
		}
	}
}
