package spg.lgdev.uhc.util.memory;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemoryList<E> {

	private Object[] contents;
	private int i = -1;

	public static <E> MemoryList<E> copyOf(final List<E> copy) {
		final MemoryList<E> list = new MemoryList<>();
		list.contents = copy.toArray(new Object[copy.size()]);
		return list;
	}

	@SuppressWarnings("unchecked")
	public E getNext() {
		i++;
		final E content = (E) contents[i];
		contents[i] = null;
		return content;
	}

	public void clear() {
		contents = null;
	}

}
