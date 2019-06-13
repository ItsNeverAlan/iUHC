package spg.lgdev.uhc.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import com.google.common.collect.Lists;

import spg.lgdev.uhc.iUHC;
import net.development.mitw.utils.FastRandom;

public class RandomListCollector<T> implements Collector<T, RandomListCollector.ListAccumulator<T>, List<T>> {

	private final FastRandom rand = iUHC.getRandom();
	private final int size;

	public RandomListCollector(final int size) {
		super();
		this.size = size;
	}

	@Override
	public Supplier<ListAccumulator<T>> supplier() {
		return () -> new ListAccumulator<>();
	}

	@Override
	public BiConsumer<ListAccumulator<T>, T> accumulator() {
		return (l, t) -> {
			if (l.size() < size) {
				l.add(t);
			} else if (rand.nextDouble() <= ((double) size) / (l.gSize() + 1)) {
				l.add(t);
				l.remove(rand.nextInt(size));
			} else {
				// in any case gSize needs to be incremented
				l.gSizeInc();
			}
		};

	}

	@Override
	public BinaryOperator<ListAccumulator<T>> combiner() {
		return (l1, l2) -> {
			final int lgSize = l1.gSize() + l2.gSize();
			final ListAccumulator<T> l = new ListAccumulator<>();
			if (l1.size() + l2.size() < size) {
				l.addAll(l1);
				l.addAll(l2);
			} else {
				while (l.size() < size) {
					if (l1.size() == 0 || l2.size() > 0 && rand.nextDouble() < (double) l2.gSize() / (l1.gSize() + l2.gSize())) {
						l.add(l2.remove(rand.nextInt(l2.size()), true));
					} else {
						l.add(l1.remove(rand.nextInt(l1.size()), true));
					}
				}
			}
			// set the gSize of l :
				l.gSize(lgSize);
			return l;

		};
	}

	@Override
	public Function<ListAccumulator<T>, List<T>> finisher() {

		return (la) -> la.list;
	}

	@Override
	public Set<Characteristics> characteristics() {
		return Collections.singleton(Characteristics.CONCURRENT);
	}

	static class ListAccumulator<T> implements Iterable<T> {
		List<T> list;
		volatile int gSize;

		public ListAccumulator() {
			list = Lists.newArrayList();
			gSize = 0;
		}

		public void addAll(final ListAccumulator<T> l) {
			list.addAll(l.list);
			gSize += l.gSize;

		}

		public T remove(final int index) {
			return remove(index, false);
		}

		public T remove(final int index, final boolean global) {
			final T t = list.remove(index);
			if (t != null && global) {
				gSize--;
			}
			return t;
		}

		public void add(final T t) {
			list.add(t);
			gSize++;

		}

		public int gSize() {
			return gSize;
		}

		public void gSize(final int gSize) {
			this.gSize = gSize;

		}

		public void gSizeInc() {
			gSize++;
		}

		public int size() {
			return list.size();
		}

		@Override
		public Iterator<T> iterator() {
			return list.iterator();
		}
	}

}