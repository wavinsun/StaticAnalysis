package cn.mutils.app.staticanalysis.findbugs;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import edu.umd.cs.findbugs.BugReporter;

/**
 * Created by wenhua.ywh on 2017/10/23.
 */
public class FindbugsExtDetector extends AbstractBaseDetector {

    public static final String BUG_TYPE_FB_EXT = "FB_EXT";

    private Map<String, BugItem> mBugMap = new ConcurrentHashMap<String, BugItem>();

    private File mLogFile = new File("build/reports/findbugs/FB_EXT.log");

    public FindbugsExtDetector(BugReporter reporter) {
        super(reporter);
        init();
    }

    private void init() {
        writeString(mLogFile, "", false);
        File jenkinsConfigFile = new File("findbugs_ext.ini");
        if (!jenkinsConfigFile.exists()) {
            return;
        }
        Properties properties = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(jenkinsConfigFile);
            properties.load(fis);
            String bugBegin = properties.getProperty("BUG_BEGIN", "");
            String bugEnd = properties.getProperty("BUG_END", "");
            if (bugBegin.isEmpty() || bugEnd.isEmpty()) {
                return;
            }
            int bugBeginIndex = Integer.parseInt(bugBegin);
            int bugEndIndex = Integer.parseInt(bugEnd);
            for (int i = bugBeginIndex; i <= bugEndIndex; i++) {
                String bugItemConfig = properties.getProperty("BUG_" + i, "");
                if (bugItemConfig.isEmpty()) {
                    continue;
                }
                String[] configs = bugItemConfig.split(":");
                if (configs == null || configs.length != 2) {
                    continue;
                }
                BugItem item = new BugItem();
                item.id = i;
                item.name = bugItemConfig;
                item.classOperand = configs[0];
                item.nameOperand = configs[1];
                mBugMap.put(item.name, item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(fis);
        }
    }

    @Override
    public void sawOpcode(int seen) {
        if (mBugMap.isEmpty()) {
            return;
        }
        String classOperand = "";
        String nameOperand = "";
        try {
            classOperand = getClassConstantOperand();
            nameOperand = getNameConstantOperand();
        } catch (Exception e) {
            // exception happens
        }
        if (classOperand.isEmpty() || nameOperand.isEmpty()) {
            return;
        }
        String name = classOperand + ":" + nameOperand;
        BugItem item = mBugMap.get(name);
        if (item == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getDottedClassName());
        sb.append(":");
        if (visitingMethod()) {
            sb.append(getMethodName());
        } else if (visitingField()) {
            sb.append(getFieldName());
        }
        sb.append("\n");
        reportBug(BUG_TYPE_FB_EXT, NORMAL_PRIORITY);
        writeString(mLogFile, sb.toString(), true);
    }

    public static boolean writeString(File file, String string, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, append);
            byte[] bytes = string.getBytes("UTF-8");
            fos.write(bytes);
            fos.flush();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            close(fos);
        }
    }

    public static void close(Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class BugItem {
        public int id;
        public String name;
        public String classOperand;
        public String nameOperand;
    }
}
