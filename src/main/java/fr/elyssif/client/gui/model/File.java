package fr.elyssif.client.gui.model;

import java.util.Date;

import com.google.gson.JsonObject;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class File extends Model<File> {

	private SimpleStringProperty name;
	private SimpleStringProperty publicKey;
	private SimpleStringProperty privateKey;
	private SimpleStringProperty address;
	private SimpleStringProperty hash;
	private SimpleStringProperty hashCiphered;
	private SimpleObjectProperty<Date> cipheredAt;
	private SimpleObjectProperty<Date> decipheredAt;
	private SimpleDoubleProperty price;

	private SimpleIntegerProperty senderId;
	private SimpleIntegerProperty recipientId;

	private SimpleObjectProperty<User> sender;
	private SimpleObjectProperty<User> recipient;

	/**
	 * Create a new instance of File with a zero ID.
	 */
	public File() {
		this(0);
	}

	/**
	 * Create a new instance of a File.
	 * @param id the ID of the resource
	 */
	public File(Integer id) {
		super(id);
		name         = new SimpleStringProperty();
		publicKey    = new SimpleStringProperty();
		privateKey   = new SimpleStringProperty();
		address      = new SimpleStringProperty();
		hash         = new SimpleStringProperty();
		hashCiphered = new SimpleStringProperty();
		cipheredAt   = new SimpleObjectProperty<Date>();
		decipheredAt = new SimpleObjectProperty<Date>();
		price        = new SimpleDoubleProperty();
		senderId     = new SimpleIntegerProperty();
		recipientId  = new SimpleIntegerProperty();
		sender       = new SimpleObjectProperty<User>();
		recipient    = new SimpleObjectProperty<User>();
	}

	/**
	 * Create a new instance of a File and load it from
	 * the given json object.
	 * @param object
	 */
	public File(JsonObject object) {
		this();
		loadFromJsonObject(object);
	}

	public final SimpleStringProperty getName() {
		return name;
	}

	public final void setName(String name) {
		this.name.set(name);
	}

	public final SimpleStringProperty getPublicKey() {
		return publicKey;
	}

	public final void setPublicKey(String publicKey) {
		this.publicKey.set(publicKey);;
	}

	public final SimpleStringProperty getPrivateKey() {
		return privateKey;
	}

	public final void setPrivateKey(String privateKey) {
		this.privateKey.set(privateKey);
	}

	public final SimpleStringProperty getAddress() {
		return address;
	}

	public final void setAddress(String address) {
		this.address.set(address);
	}

	public final SimpleStringProperty getHash() {
		return hash;
	}

	public final void setHash(String hash) {
		this.hash.set(hash);
	}

	public final SimpleStringProperty getHashCiphered() {
		return hashCiphered;
	}

	public final void setHashCiphered(String hashCiphered) {
		this.hashCiphered.set(hashCiphered);
	}

	public final SimpleObjectProperty<Date> getCipheredAt() {
		return cipheredAt;
	}

	public final void setCipheredAt(Date cipheredAt) {
		this.cipheredAt.set(cipheredAt);
	}

	public final SimpleObjectProperty<Date> getDecipheredAt() {
		return decipheredAt;
	}

	public final void setDecipheredAt(Date decipheredAt) {
		this.decipheredAt.set(decipheredAt);
	}

	public final SimpleDoubleProperty getPrice() {
		return price;
	}

	public final void setPrice(double price) {
		this.price.set(price);
	}

	public final SimpleIntegerProperty getSenderId() {
		return senderId;
	}

	public final void setSenderId(int senderId) {
		this.senderId.set(senderId);
	}

	public final SimpleIntegerProperty getRecipientId() {
		return recipientId;
	}

	public final void setRecipientId(int recipientId) {
		this.recipientId.set(recipientId);
	}

	public final SimpleObjectProperty<User> getSender() {
		return sender;
	}

	public final void setSender(User sender) {
		this.sender.set(sender);
	}

	public final SimpleObjectProperty<User> getRecipient() {
		return recipient;
	}

	public final void setRecipient(User recipient) {
		this.recipient.set(recipient);
	}

}
