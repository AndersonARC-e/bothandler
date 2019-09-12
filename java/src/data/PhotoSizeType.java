package data;

public enum PhotoSizeType {
    s('s'),m('m'),x('x'),o('o'),p('p'),q('q'),r('r'),y('y'),z('z'),w('w');

    private char type;

    public char asChar(){
        return type;
    }

    PhotoSizeType(char type) {

    }
}
