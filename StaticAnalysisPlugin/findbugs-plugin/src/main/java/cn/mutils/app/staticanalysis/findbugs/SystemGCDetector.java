package cn.mutils.app.staticanalysis.findbugs;

import edu.umd.cs.findbugs.BugReporter;

/**
 * Created by wenhua.ywh on 2017/1/18.
 */
public class SystemGCDetector extends AbstractBaseDetector {

    public SystemGCDetector(BugReporter reporter) {
        super(reporter);
    }

    @Override
    public void sawOpcode(int seen) {
        if (seen == INVOKESTATIC) {
            if ("java/lang/System".equals(getClassConstantOperand())) {
                if ("gc".equals(getNameConstantOperand())) {
                    reportBug("SYSTEM_GC", HIGH_PRIORITY);
                }
            }
        }
    }
}
