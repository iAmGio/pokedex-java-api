package eu.iamgio.pokedex.lang;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

/**
 * List of localized names
 * @author Gio
 */
@SuppressWarnings("unchecked")
public class LocalizedNameList<T extends LocalizedName> extends ArrayList<T> {

    /**
     * Loads a list of {@link LocalizedName} from JSON
     * @param array JSON array containing localized names
     * @param key Name of the JSON key
     * @param flavor -1 if not a flavor. 0 if flavor based on version groups. 1 if flavor based on versions
     */
    protected LocalizedNameList(JsonArray array, String key, byte flavor) {
        this();
        if(this instanceof LocalizedNames) {
            for(JsonElement json : array) {
                add((T) new LocalizedName(json.getAsJsonObject(), key));
            }
        } else if(flavor >= 0) {
            for(JsonElement json : array) {
                add((T) new Flavor(json.getAsJsonObject(), key, flavor == 0));
            }
        }
    }

    /**
     * Loads a list of {@link LocalizedName} from another list
     * @param list List
     */
    public LocalizedNameList(List<T> list) {
        super(list);
    }

    public LocalizedNameList() {}

    /**
     * @param language Target language
     * @return {@link LocalizedName} using that language
     */
    public T get(Language language) {
        for(T name : this) {
            if(name.getLanguage() == language) {
                return name;
            }
        }
        return null;
    }
}
