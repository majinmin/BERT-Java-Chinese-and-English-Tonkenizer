package com.text.bert.tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestTesT {
	
	public static void main(String[] args) {
		
		
		
		int[] test_array = new int[5];
		for (int i = 0; i < test_array.length; i++) {
			test_array[i] = i;
		}

		ArrayList<Integer> list = (ArrayList<Integer>)Arrays.stream(test_array).boxed().collect(Collectors.toList());
		list.add(0, 101);
		list.add(102);
		for (Integer integer : list) {
			
			System.out.println(integer);
		}
		
	}

}
