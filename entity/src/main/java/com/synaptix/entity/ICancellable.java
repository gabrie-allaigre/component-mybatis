package com.synaptix.entity;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.annotation.Column;

import java.util.Date;

@SynaptixComponent
public interface ICancellable extends IComponent {

	@Column(name = "CHECK_CANCEL")
	boolean getCheckCancel();

	void setCheckCancel(boolean checkCancel);

	@Column(name = "CANCEL_DATE")
	Date getCancelDate();

	void setCancelDate(Date cancelDate);

	@Column(name = "CANCEL_BY")
	String getCancelBy();

	void setCancelBy(String cancelBy);

}
