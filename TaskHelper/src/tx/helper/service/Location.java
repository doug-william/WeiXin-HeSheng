package tx.helper.service;

public class Location {
	String latitude;
	String longitude;
	String precision;
	
	public Location(String latitude,String longitude,String precision){
		this.latitude = latitude;
		this.longitude = longitude;
		this.precision = precision;
	}
	
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getPrecision() {
		return precision;
	}
	public void setPrecision(String precision) {
		this.precision = precision;
	}
	
	@Override
	public String toString(){
		return this.longitude+","+this.latitude;
	}
	
}
