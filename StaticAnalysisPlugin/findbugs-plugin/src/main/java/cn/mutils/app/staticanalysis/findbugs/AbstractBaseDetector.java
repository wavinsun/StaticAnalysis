package cn.mutils.app.staticanalysis.findbugs;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

public abstract class AbstractBaseDetector extends OpcodeStackDetector {

    private BugReporter mReporter;

    public AbstractBaseDetector(BugReporter reporter) {
        mReporter = reporter;
    }

    protected void reportBug(String type, int priority) {
        BugInstance bug = new BugInstance(this, type, priority);
        bug.addClass(this);
        bug.addMethod(this);
        bug.addSourceLine(this);
        mReporter.reportBug(bug);
    }

}
