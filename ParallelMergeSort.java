package mergeSort;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import tools.MyArrayUtil;

public class ParallelMergeSort<E extends Comparable> extends MergeSort<E> {
	private final BlockingQueue<ArraysRange> tasks; //arrayの位置情報を保存しておく
	private final AtomicInteger fixTimes;
	ExecutorService executor;
	int threadsNum;

	public ParallelMergeSort(){
		tasks = new LinkedBlockingQueue<ArraysRange>(Integer.MAX_VALUE);
		fixTimes = new AtomicInteger(0);
		threadsNum = Runtime.getRuntime().availableProcessors(); //修正
		//threadsNum = 2;
		executor = Executors.newCachedThreadPool();
	}

	public void sort(E[] array){
		this.array = array;

		final List<Callable<Object>> workers = new ArrayList<Callable<Object>>(threadsNum);
		workers.add(Executors.callable(new PartitionWorker()));
		for(int i = 1;i < threadsNum;i++)
			workers.add(Executors.callable(new SortWorker()));

		try {
			executor.invokeAll(workers);
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		executor.shutdown();
	}

	class PartitionWorker implements Runnable {
		public void run() {
//			System.out.println(Thread.currentThread().getName() + "  partition Start");
			partition(0,array.length - 1);
//			System.out.println(Thread.currentThread().getName() +    "  partition End");
		}
	}

	class SortWorker implements Runnable {
		public void run() {

			while(fixTimes.get() < array.length * 2){
				final ArraysRange arraysRange = tasks.poll();
//				System.out.println(Thread.currentThread().getName()+"  left : "+arraysRange.left+"  right : "+arraysRange.right);
//				MyArrayUtil.print(array, arraysRange.left, arraysRange.right);
				fixTimes.incrementAndGet();

				merge(arraysRange.left,arraysRange.mid,arraysRange.right);
//				MyArrayUtil.print(array, arraysRange.left, arraysRange.right);
			}
		}
	}


	public void partition(int left,int right){
		int mid = (left + right) / 2;

		if(right <= left)
			return;

		partition(left,mid);
		partition(mid + 1, right);

		tasks.offer(new ArraysRange(left,mid,right));
	}

	@Override
	public synchronized void merge(int left,int mid,int right){
		super.merge(left, mid, right);
	}

	static class ArraysRange {
		final int left,mid,right;

		ArraysRange(int left,int mid,int right){
			this.left = left;
			this.mid = mid;
			this.right = right;
		}
	}
}
