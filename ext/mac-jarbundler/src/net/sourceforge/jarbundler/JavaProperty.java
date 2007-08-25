package net.sourceforge.jarbundler;

public class JavaProperty {

	/** The JavaProperties' name and value */

	private String name = null;
	private String value = null;

	/**
	 * Construct an empty JavaProperty
	 */

	public JavaProperty() {
	}

	/**
	 * Set the JavaProperties's name; required
	 * 
	 * @param name
	 *            the JavaProperties' name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the JavaProperties' name
	 * 
	 * @return the JavaProperties' name.
	 */
	public String getName() {

		if (this.name == null)
			return null;

		return this.name.trim();
	}

	/**
	 * Set the JavaProperties' value; required
	 * 
	 * @param value
	 *            the JavaProperties' value
	 */

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Get the JavaProperties' value.
	 * 
	 * @return the JavaProperties' value.
	 */
	public String getValue() {

		if (this.value == null)
			return null;

		return this.value.trim();
	}

}
