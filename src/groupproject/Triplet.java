package groupproject;

/**
 * Describes a tuple of 3 values
 * @param <L> left object
 * @param <M> middle object
 * @param <R> right object
 */
public class Triplet<L, M, R> {
    private L l;
    private M m;
    private R r;

    public Triplet(L l, M m, R r){
        this.l = l;
        this.m = m;
        this.r = r;
    }

    public L getL(){ return l; }

    public M getM() {
        return m;
    }

    public R getR(){ return r; }

    public void setL(L l){ this.l = l; }

    public void setM(M m) {
        this.m = m;
    }

    public void setR(R r){ this.r = r; }

}
