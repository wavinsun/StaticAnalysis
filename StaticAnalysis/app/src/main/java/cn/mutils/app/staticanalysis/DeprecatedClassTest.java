package cn.mutils.app.staticanalysis;

public class DeprecatedClassTest {

    private DeprecatedClass mMember;

    public static void test() {
        new DeprecatedClass();
    }

}
