package libs.trustconnector.scdp.testsuite;

class ParamInfo
{
    int beginIndex;
    int endIndex;
    int childCount;
    ParamInfo parent;
    
    ParamInfo(final int beginIndex) {
        this.beginIndex = beginIndex;
    }
    
    public void increseChildCount() {
        ++this.childCount;
        if (this.parent != null) {
            this.parent.increseChildCount();
        }
    }
}
