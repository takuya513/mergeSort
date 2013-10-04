package mergeSort;

import java.util.LinkedList;

import sort.Sort;

public class MergeSort<E extends Comparable> implements Sort<E>{
	E[] array;
	LinkedList<E> buff;
	public MergeSort(){
		buff = new LinkedList<E>();
	}

	public void sort(E[] array){
		this.array = array;
		partition(0,array.length - 1);
	}

	public void partition(int left,int right){
		int mid = (left + right) / 2;

		if(right <= left)
			return;

		partition(left,mid);
		partition(mid + 1, right);

		merge(left,mid,right);
	}

	public void merge(int left,int mid,int right){
		//buff.clear();  //buff用のリストを初期化しておく.修正
		int i = left,j = mid + 1;

		while(i <= mid && j <= right) {
			if(array[i].compareTo(array[j]) < 0){
				buff.add(array[i]); i++;
			}else{
				buff.add(array[j]); j++;
			}
		}

		while(i <= mid) { buff.add(array[i]); i++;}
		while(j <= right) { buff.add(array[j]); j++;}
		for(i = left;i <= right; i++){ array[i] = buff.remove(0);}
	}

}
