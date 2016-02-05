package com.synaptix.entity;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Version;

@SynaptixComponent
public interface IEntity extends IComponent {

	@EqualsKey
	@Id
	@Column(name = "ID", nullable = false)
	IId getId();

	void setId(IId id);

	@Version
	@Column(name = "VERSION", precision = 10, nullable = false)
	Integer getVersion();

	void setVersion(Integer version);

}
