package com.givers.initializer;

import java.util.List;

public interface Generator<T> {
	List<T> generate(int limit);
}
