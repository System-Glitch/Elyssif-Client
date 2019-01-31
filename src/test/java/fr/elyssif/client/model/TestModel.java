package fr.elyssif.client.model;

import java.util.Date;

import com.google.gson.JsonObject;

import fr.elyssif.client.gui.model.Model;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class TestModel extends Model<TestModel> {

	private SimpleStringProperty string;
	private SimpleBooleanProperty bool;
	private SimpleIntegerProperty integer;
	private SimpleLongProperty lng;
	private SimpleFloatProperty flt;
	private SimpleDoubleProperty dbl;

	private SimpleObjectProperty<Date> dateAt;

	private ObservableList<String> stringList;
	private ObservableList<Integer> intList;

	private SimpleStringProperty name;

	private String notProperty;

	private SimpleObjectProperty<TestModel> nested;

	public TestModel() {
		this(0);
	}

	public TestModel(Integer id) {
		super(id);
		string = new SimpleStringProperty();
		bool = new SimpleBooleanProperty();
		integer = new SimpleIntegerProperty();
		lng = new SimpleLongProperty();
		flt = new SimpleFloatProperty();
		dbl = new SimpleDoubleProperty();
		dateAt = new SimpleObjectProperty<Date>();
		nested = new SimpleObjectProperty<TestModel>();
		name = new SimpleStringProperty();
		stringList = FXCollections.observableArrayList();
		intList = FXCollections.observableArrayList();
	}

	public TestModel(JsonObject object) {
		this();
		loadFromJsonObject(object);
	}

	public final SimpleStringProperty getString() {
		return string;
	}

	public final SimpleBooleanProperty getBool() {
		return bool;
	}

	public final SimpleIntegerProperty getInteger() {
		return integer;
	}

	public final SimpleLongProperty getLng() {
		return lng;
	}

	public final SimpleFloatProperty getFlt() {
		return flt;
	}

	public final SimpleDoubleProperty getDbl() {
		return dbl;
	}

	public final SimpleObjectProperty<Date> getDateAt() {
		return dateAt;
	}

	public final ObservableList<String> getStringList() {
		return stringList;
	}

	public final ObservableList<Integer> getIntList() {
		return intList;
	}

	public final SimpleStringProperty getName() {
		return name;
	}

	public final String getNotProperty() {
		return notProperty;
	}

	public final SimpleObjectProperty<TestModel> getNested() {
		return nested;
	}

}
