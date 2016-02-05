package com.synaptix.entity;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

import javax.persistence.Column;
import java.util.Date;

@SynaptixComponent
public interface ICancellable extends IComponent {

	@Column(name = "CHECK_CANCEL", nullable = false)
	boolean getCheckCancel();

	void setCheckCancel(boolean checkCancel);

	@Column(name = "CANCEL_DATE")
	Date getCancelDate();

	void setCancelDate(Date cancelDate);

	@Column(name = "CANCEL_BY", length = 240)
	String getCancelBy();

	void setCancelBy(String cancelBy);

}
