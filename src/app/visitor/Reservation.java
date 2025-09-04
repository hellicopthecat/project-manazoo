package app.visitor;

import app.common.ui.TableUtil;

public class Reservation {
	private String id; // 예약 번호
	private String name;
	private String phone;
	private String date;
	private int adultCount;
	private int childCount;
	private int totalPrice;

	public Reservation(String id, String name, String phone, String date, int adultCount, int childCount,
			int totalPrice) {
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.date = date;
		this.adultCount = adultCount;
		this.childCount = childCount;
		this.totalPrice = totalPrice;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public String getDate() {
		return date;
	}

	public int getAdultCount() {
		return adultCount;
	}

	public int getChildCount() {
		return childCount;
	}

	public int getTotalPrice() {
		return totalPrice;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setAdultCount(int adultCount) {
		this.adultCount = adultCount;
	}

	public void setChildCount(int childCount) {
		this.childCount = childCount;
	}

	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}

	@Override
	public String toString() {

		String[] headers = { "Booking Number", "Name", "Phone", "Visit Date", "Adult", "Child", "Total amount" };
		String[][] data = { { id, name, phone, date, Integer.toString(adultCount), Integer.toString(childCount),
				Integer.toString(totalPrice) } };

		TableUtil.printTable(" ", headers, data);

		return "";
	}

}
