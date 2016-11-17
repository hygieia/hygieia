package com.capitalone.dashboard.model;

public class UpdateResult {

	private final int itemsAdded;
	private final int itemsDeleted;
	
	public UpdateResult(int itemsAdded, int itemsDeleted) {
		this.itemsAdded = itemsAdded;
		this.itemsDeleted = itemsDeleted;
	}

	public int getItemsAdded() {
		return itemsAdded;
	}

	public int getItemsDeleted() {
		return itemsDeleted;
	}

}
