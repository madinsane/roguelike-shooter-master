package groupproject;

/**
 * Describes a 2 item tuple
 * @param <L> left item
 * @param <R> right item
 */
public class Pair<L, R> {
	private L l;
	private R r;

	public Pair(L l, R r){
		this.l = l;
		this.r = r;
	}

	public L getL(){ return l; }

	public R getR(){ return r; }

	public void setL(L l){ this.l = l; }

	public void setR(R r){ this.r = r; }

}

