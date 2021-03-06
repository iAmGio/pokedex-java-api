package eu.iamgio.pokedex.pokemon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.iamgio.pokedex.connection.HttpConnection;
import eu.iamgio.pokedex.exception.PokedexException;
import eu.iamgio.pokedex.item.ItemHold;
import eu.iamgio.pokedex.pokemon.move.PokemonPersonalMove;
import eu.iamgio.pokedex.util.JsonStream;
import eu.iamgio.pokedex.util.NamedResource;
import eu.iamgio.pokedex.util.Pair;
import eu.iamgio.pokedex.version.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a Pokémon from the Pokédex
 * @author Gio
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Pokemon {

    /**
     * Name of this Pokémon (lower case)
     */
    private String name;

    /**
     * Identifier of this Pokémon
     */
    private int id;

    /**
     * Order for sorting. Almost national order, except families are grouped together
     */
    private int order;

    /**
     * The height of this Pokémon in decimetres
     */
    private int height;

    /**
     * The weight of this Pokémon in hectograms
     */
    private int weight;

    /**
     * Base experience gained for defeating this Pokémon
     */
    private int baseExperience;

    /**
     * A pair of types this Pokémon has. Second may be null if it has only one type
     */
    private Pair<PokemonType, PokemonType> types;

    /**
     * A list of items this Pokémon may be holding when encountered
     */
    private List<ItemHold> heldItems;

    /**
     * A list of game indices relevent to Pokémon item by generation
     */
    private Map<Version, Integer> gameIndices;

    /**
     * A list of types this Pokémon can learn
     */
    private List<PokemonPersonalMove> moves;

    /**
     * The species this Pokémon belongs to
     */
    private String speciesName;

    /**
     * A list of base stat values for this Pokémon
     */
    private Stat[] stats;

    /**
     * A set of sprites used to depict this Pokémon in the game
     */
    private Sprite[] sprites;

    /**
     * @param name Move name
     * @return {@link PokemonPersonalMove} from name. <tt>null</tt> if this Pokémon isn't able to learn it
     */
    public PokemonPersonalMove getMove(String name) {
        return moves.stream().filter(move -> move.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * @param type Stat type
     * @return {@link Stat} of the specified type
     */
    public Stat getStat(Stat.Type type) {
        for(Stat stat : stats) {
            if(stat.getType() == type) {
                return stat;
            }
        }
        return null;
    }

    /**
     * @param type Sprite type
     * @return {@link Sprite} of the specified type
     */
    public Sprite getSprite(Sprite.Type type) {
        for(Sprite sprite : sprites) {
            if(sprite.getType() == type) {
                return sprite;
            }
        }
        return null;
    }

    /**
     * @param name Name of the Pokémon
     * @return Pokémon whose name matches <tt>name</tt>
     * @throws PokedexException if <tt>name</tt> doesn't match a Pokémon name
     */
    public static Pokemon fromName(String name) throws PokedexException {
        JsonObject json;
        try {
            json = new HttpConnection("pokemon/" + name + "/").getJson();
        } catch(RuntimeException e) {
            throw new PokedexException("Could not find Pokémon with name/ID " + name);
        }
        List<PokemonType> types = new JsonStream(json.getAsJsonArray("types"))
                .stream()
                .map(type -> new NamedResource(type.getAsJsonObject().get("type")).toEnumName())
                .map(PokemonType::valueOf)
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());
        JsonArray statsArray = json.getAsJsonArray("stats");
        Stat[] stats = new Stat[statsArray.size()];
        for(int i = 0; i < statsArray.size(); i++) {
            JsonObject stat = statsArray.get(i).getAsJsonObject();
            stats[i] = new Stat(
                    Stat.Type.valueOf(new NamedResource(stat.get("stat")).toEnumName()),
                    stat.get("effort").getAsInt(),
                    stat.get("base_stat").getAsInt()
            );
        }
        Sprite[] sprites = new Sprite[Sprite.Type.values().length];
        JsonObject spritesObject = json.getAsJsonObject("sprites");
        for(int i = 0; i < Sprite.Type.values().length; i++) {
            Sprite.Type type = Sprite.Type.values()[i];
            JsonElement urlElement = spritesObject.get(type.name().toLowerCase());
            sprites[i] = new Sprite(type, urlElement.isJsonNull() ? null : urlElement.getAsString());
        }
        return new Pokemon(
                json.get("name").getAsString(),
                json.get("id").getAsInt(),
                json.get("order").getAsInt(),
                json.get("height").getAsInt(),
                json.get("weight").getAsInt(),
                json.get("base_experience").getAsInt(),
                new Pair<>(types.get(0), types.size() > 1 ? types.get(1) : null),
                ItemHold.fromJson(json.getAsJsonArray("held_items"), true),
                new JsonStream(json.getAsJsonArray("game_indices"))
                        .stream()
                        .map(JsonElement::getAsJsonObject)
                        .collect(Collectors.toMap(
                                indice -> Version.valueOf(new NamedResource(indice.get("version")).toEnumName()),
                                indice -> indice.get("game_index").getAsInt()
                        )),
                new JsonStream(json.getAsJsonArray("moves"))
                    .stream()
                    .map(JsonElement::getAsJsonObject)
                    .map(PokemonPersonalMove::fromJson)
                    .collect(Collectors.toList()),
                new NamedResource(json.get("species")).getName(),
                stats,
                sprites
        );
    }

    /**
     * @param id Identifier of the Pokémon
     * @return Pokémon whose ID matches <tt>id</tt>
     * @throws PokedexException if <tt>id</tt> is 0 or less or doesn't match a Pokémon ID
     */
    public static Pokemon fromId(Number id) throws PokedexException {
        return fromName(String.valueOf(id));
    }
}
