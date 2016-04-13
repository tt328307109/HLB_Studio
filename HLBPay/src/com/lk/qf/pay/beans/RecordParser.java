package com.lk.qf.pay.beans;

import java.io.InputStream;
import java.util.List;

public interface RecordParser {
	public List<Record> parse(InputStream is) throws Exception;
	public String serialize(List<Record> books) throws Exception;   

}
