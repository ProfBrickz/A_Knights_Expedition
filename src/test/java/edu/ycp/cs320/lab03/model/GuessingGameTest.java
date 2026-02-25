package edu.ycp.cs320.lab03.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import edu.ycp.cs320.lab03.model.GuessingGame;

public class GuessingGameTest {
	private GuessingGame model;
	
	@BeforeEach
	public void setUp() {
		model = new GuessingGame();
	}
	
	@Test
	public void testSetMin() {
		model.setMin(1);
		Assertions.assertEquals(1, model.getMin());
	}

	@Test
	public void testSetMax() {
		model.setMax(100);
		Assertions.assertEquals(100, model.getMax());
	}
}
