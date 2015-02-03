package com.siddworks.simguard.bean;

public class KeyValue {
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	private String key;
	private String value;
	private long id;
	@Override
	public String toString() {
		return "KeyValue [key=" + key + ", value=" + value + "]";
	}
	
	public KeyValue(String key, String value)
	{
		this.key = key;
		this.value = value;
	}
	
	public KeyValue()
	{
		
	}
		
}
