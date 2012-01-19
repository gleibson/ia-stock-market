package net.sourceforge.jasa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class News {
	
	private double stockNewValue;
	private int receiversQuantity;
	private double receiversPer;
	private ArrayList <Integer> receivers;
	private int[] deliverTime;
	
	
	public News(double receiversPer, int totalAgents, int day, int time) {
		this.receivers = new ArrayList <Integer> ();
		this.receiversPer = receiversPer;
		this.receiversQuantity = (int) (totalAgents * receiversPer);
		this.deliverTime = new int[2];
		this.deliverTime[0] = day;
		this.deliverTime[1] = time;
		generateNewValue();
		generateReceivers(totalAgents);
		
	}

	private void generateNewValue(){
		Random random = new Random();
		int aux;
		do {
			aux = random.nextInt(11);
		} while(aux == 5);
		this.stockNewValue = -0.5 + (aux * 0.1);
		
	}
	
	public void generateReceivers (int total){
		Random random = new Random();
		int aux = random.nextInt(total);
		this.receivers.add(aux);
		
		for (int i = 0; i < this.receiversQuantity - 1; i++){
			do {
				aux = random.nextInt(total);
			} while(this.receivers.contains(aux));
			this.receivers.add(aux);
		}
	}

	public int[] getDeliverTime() {
		return deliverTime;
	}

	public void setDeliverTime(int[] deliverTime) {
		this.deliverTime = deliverTime;
	}

	@Override
	public String toString() {
		return "News [stockNewValue=" + stockNewValue + ", receiversQuantity="
				+ receiversQuantity + ", receiversPer=" + receiversPer
				+ ", deliverTime=" + Arrays.toString(deliverTime) + "]";
	}
	
	
	
}
	

