package com.groupon.seleniumgridextras.config.capabilities;

public class Edge extends Capability {
    @Override
    public String getWebDriverClass() {
        return "org.openqa.selenium.edge.EdgeDriver";
    }

    @Override
    public String getIcon() {
        return "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABmJLR0QA/wD/AP+gvaeTAAAACXBIWXMAABYlAAAWJQFJUiTwAAAAB3RJTUUH3wgMDTo3wNYYBgAAAfFJREFUOMuNk71rFFEUxX9ndjaraBQNgpWCQhqxSDZVGptZIyKsogQtJJ2FncWuFhICBiSzxCKlTRoL0cJYSsY/ISMEohaKhYiFpvCDIElmjsVO4mRjkdO8dy/3nnvfOTxRwnArCRFnbCaEzyOdANaxl5GeAS/TOPpa7tHWZeResj/PeGEYk+iBsQFYlTSbxtHDHQT1dtKfm08SAwC2M9CmRA6EhqrApZ4HaRxNAmi4nVSB50DTZk3yvK1E8hfQGvZxpJuGiYJEtjckRtK4sRyCB201wa8qFTWzjNNBwNWlmcZCMXEFeD3UWjyMdLnIVUGTwLXA1i3w5JtO40KeeUzSysGfTPeqIGnahRCSwFwBCIU/pp3GXL21eDSXFrC//T6kO/W7Sa1HxIF/oY0UAIRppzEHkEvzAiEdy3Nme+ZvTTbdmm2fQoBCyEtdctht427bdxDYnJQIiopfwBRQYw8Ii3OzlOtP4+gRe8T2SkPtZF1QLcKnaRzd+F9DvZ3I0AdspHGUhyWmGZv7ErZ9fai1eETSE9sfJP2xfQDplOGczVnwRWC1TDBlGAcPSsJmDIgEmzZWN1m1FEh+i5UDXeEAluIok2iC3pWcqCDVJPYZ+pACAUYViu8VlN+XxtF7m1GJ27Y/94gl4DvwWDBu8QPgL1MFzPkwOfRoAAAAAElFTkSuQmCC";
    }
}
