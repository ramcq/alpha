package org.ucam.ned.teamalpha.shell;

import org.ucam.ned.teamalpha.algorithms.*;
import org.ucam.ned.teamalpha.animators.*;
import org.ucam.ned.teamalpha.queues.*;

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
		System.out.println("asdf");
	}
}
