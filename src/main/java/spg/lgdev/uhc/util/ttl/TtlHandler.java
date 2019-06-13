package spg.lgdev.uhc.util.ttl;

public interface TtlHandler<E> {

	void onExpire(E element);

	long getTimestamp(E element);

}