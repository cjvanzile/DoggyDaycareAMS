/**
 * This is a utility class to override the standard JComboBox, replacing the string with a key/value pair.
 */
public class ComboItem
{
    private String key;
    private String value;

    /**
     * Replace String with key/value pair.
     * @param key
     * @param value
     */
    public ComboItem(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    /**
     * This overrides the toString to return only the key, of the key/value pair.
     * @return Returns a String containing the key.
     */
    @Override
    public String toString()
    {
        return key;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}