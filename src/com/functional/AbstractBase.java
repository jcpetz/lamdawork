package com.functional;

import java.util.List;

public abstract class AbstractBase<T> {
	volatile boolean go = true;
	int iterations = 0;

	public abstract List<T> supplyData();

	public abstract void consumeData(T data);

	public void run() {
		while (isGo()) {
			List<T> data = supplyData();
			data.forEach(a -> consumeData(a));
			iterations++;
		}
	}
	

	public boolean isGo() {
		return go;
	}

	public void setGo(boolean go) {
		this.go = go;
	}

}
