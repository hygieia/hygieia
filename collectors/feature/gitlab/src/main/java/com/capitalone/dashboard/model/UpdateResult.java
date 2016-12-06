package com.capitalone.dashboard.model;

public class UpdateResult {

	private int itemsAdded;
	private int itemsDeleted;
	
	public UpdateResult(int itemsAdded, int itemsDeleted) {
		this.itemsAdded = itemsAdded;
		this.itemsDeleted = itemsDeleted;
	}
	
	public UpdateResult add(UpdateResult result) {
		this.itemsAdded += result.getItemsAdded();
		this.itemsDeleted += result.getItemsDeleted();
		
		return this;
	}

	public int getItemsAdded() {
		return itemsAdded;
	}

	public int getItemsDeleted() {
		return itemsDeleted;
	}

}
