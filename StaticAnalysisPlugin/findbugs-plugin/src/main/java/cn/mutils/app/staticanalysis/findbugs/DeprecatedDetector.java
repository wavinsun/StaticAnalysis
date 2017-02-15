package cn.mutils.app.staticanalysis.findbugs;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.ba.XClass;
import edu.umd.cs.findbugs.ba.XField;
import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;

/**
 * Created by wenhua.ywh on 2017/2/14.
 */
public class DeprecatedDetector extends AbstractBaseDetector {

    public DeprecatedDetector(BugReporter reporter) {
        super(reporter);
    }

    @Override
    public void sawOpcode(int seen) {
        XClass operandClass = getXClassOperand();
        if (operandClass == null) {
            return;
        }
        ClassDescriptor operandClassDes = getClassDescriptorOperand();
        String operandClassName = operandClassDes.getClassName();
        if (operandClassName.startsWith("java/") || operandClassName.startsWith("android/")) {
            return;
        }
        if (operandClass.equals(getClassDescriptor())) {
            return;
        }
        ClassDescriptor deprecated = ClassDescriptor.createClassDescriptor("java/lang/Deprecated");
        if (operandClass.getAnnotation(deprecated) != null) {
            reportBug("DEPRECATED", HIGH_PRIORITY);
            return;
        }
        XField operandField = getXFieldOperand();
        if (operandField != null) {
            if (operandField.getAnnotation(deprecated) != null) {
                reportBug("DEPRECATED", HIGH_PRIORITY);
                return;
            }
        }
        try {
            XMethod operandMethod = getXMethodOperand();
            if (operandMethod != null) {
                if (operandMethod.getAnnotation(deprecated) != null) {
                    reportBug("DEPRECATED", HIGH_PRIORITY);
                    return;
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
