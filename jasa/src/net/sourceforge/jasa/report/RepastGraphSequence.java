/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2009 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package net.sourceforge.jasa.report;

import java.util.Iterator;

import net.sourceforge.jabm.report.DataWriter;

import uchicago.src.sim.analysis.Sequence;

/*
 * @author Steve Phelps
 * 
 * @version $Revision: 1.2 $
 */

public class RepastGraphSequence implements Sequence, DataWriter {

	protected double lastValue;

	protected String name;

	public RepastGraphSequence(String name) {
		this.name = name;
	}

	public double getSValue() {
		if (Double.isInfinite(lastValue) || Double.isNaN(lastValue)) {
			return 0;
		} else {
			return lastValue;
		}
	}

	public void newData(double data) {
		lastValue = data;
	}

	public void newData(Double data) {
		lastValue = data.doubleValue();
	}

	public void newData(Integer data) {
		lastValue = data.doubleValue();
	}

	public void newData(Long data) {
		lastValue = data.doubleValue();
	}

	public void newData(String data) {

	}

	public String getName() {
		return name;
	}

	public void close() {
	}

	public void flush() {
	}

	public void newData(boolean data) {

	}

	public void newData(float data) {
		newData(data);
	}

	public void newData(int data) {

	}

	public void newData(Iterator i) {

	}

	public void newData(long data) {

	}

	public void newData(Object data) {
		// TODO Auto-generated method stub

	}

	public void newData(Object[] data) {
		// TODO Auto-generated method stub

	}
}
