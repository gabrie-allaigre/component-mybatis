package com.synaptix.entity;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.annotation.NlsColumn;

import javax.persistence.Column;

@SynaptixComponent
public interface INlsMessage extends IComponent {

	@Column(name = "MEANING", length = 70)
	@NlsColumn
	String getMeaning();

	void setMeaning(String meaning);

}
