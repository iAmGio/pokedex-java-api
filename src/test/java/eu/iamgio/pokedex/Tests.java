package eu.iamgio.pokedex;

import eu.iamgio.pokedex.lang.Language;
import eu.iamgio.pokedex.pokedex.Pokedex;
import eu.iamgio.pokedex.pokemon.*;
import eu.iamgio.pokedex.pokemon.encounter.Encounter;
import eu.iamgio.pokedex.pokemon.encounter.EncounterConditionValue;
import eu.iamgio.pokedex.pokemon.encounter.EncounterMethod;
import eu.iamgio.pokedex.pokemon.move.*;
import eu.iamgio.pokedex.version.Version;
import eu.iamgio.pokedex.version.VersionGroup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Gio
 */
class Tests {

    @Test
    void testBulbasaur() {
        Pokemon bulbasaur = Pokemon.fromName("bulbasaur");
        assertEquals("bulbasaur", bulbasaur.getName());
        assertEquals(1, bulbasaur.getId());
        assertEquals(7, bulbasaur.getHeight());
        assertEquals(69, bulbasaur.getWeight());
        assertEquals(64, bulbasaur.getBaseExperience());
        assertArrayEquals(new PokemonType[] {PokemonType.GRASS, PokemonType.POISON}, bulbasaur.getTypes());
        assertEquals(153, bulbasaur.getGameIndices().get(Version.YELLOW).intValue());
        assertEquals("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
                bulbasaur.getSprite(Sprite.Type.FRONT_DEFAULT).getUrl());
        PokemonPersonalMove.VersionGroupDetail leechSeed = bulbasaur.getMove("leech-seed").getGroupDetails().get(0);
        assertEquals(VersionGroup.OMEGA_RUBY_ALPHA_SAPPHIRE, leechSeed.getGroup());
        assertEquals(MoveLearnMethod.LEVEL_UP, leechSeed.getLearnMethod());
        assertEquals(7, leechSeed.getLevelLearnedAt());
        assertEquals(6, bulbasaur.getStats().length);
        assertEquals(45, bulbasaur.getStat(Stat.Type.SPEED).getBaseStat());
    }

    @Test
    void testLevitate() {
        PokemonAbility levitate = PokemonAbility.fromName("levitate");
        assertEquals(26, levitate.getId());
        assertEquals(true, levitate.isMainSeries());
        assertEquals(Generation.GENERATION_III, levitate.getGeneration());
        assertNotEquals("[]", levitate.getLocalizedNames().toString());
        assertNotEquals("[]", levitate.getFlavors().toString());
    }

    @Test
    void testDiamondPearl() {
        VersionGroup.LoadedVersionGroup group = VersionGroup.DIAMOND_PEARL.load();
        assertEquals(Generation.GENERATION_IV, group.getGeneration());
        assertEquals(8, group.getId());
        assertEquals(10, group.getOrder());
        assertArrayEquals(new Version[] {Version.DIAMOND, Version.PEARL}, group.getVersions());
        assertNotEquals("[]", group.getMoveLearnMethods());
    }

    @Test
    void testGen6() {
        Generation.LoadedGeneration gen = Generation.GENERATION_VI.load();
        assertEquals(PokemonType.FAIRY, gen.getTypes().get(0));
        assertEquals("kalos", gen.getMainRegion());
        assertEquals(VersionGroup.X_Y, gen.getGroups().get(0));
        assertEquals(27, gen.getAbilityNames().size());
        assertEquals(62, gen.getMoveNames().size());
        assertEquals(72, gen.getPokemonNames().size());
        assertEquals("Generation VI", gen.getLocalizedNames().get(Language.ENGLISH).getName());
    }

    @Test
    void testBodySlam() {
        PokemonMove move = PokemonMove.fromName("body-slam");
        assertEquals(100, move.getAccuracy());
        assertEquals(30, move.getEffectChance().intValue());
        assertEquals(85, move.getPower());
        assertEquals(15, move.getPowerPoints());
        assertEquals(0, move.getPriority());
        assertEquals(120, move.getMachines().get(VersionGroup.RED_BLUE).intValue());
        assertEquals(Generation.GENERATION_I, move.getGeneration());
        assertNotEquals("[]", move.getFlavors().toString());
        assertEquals("Body Slam", move.getLocalizedNames().get(Language.ENGLISH).getName());
        assertEquals(PokemonType.NORMAL, move.getType());
        assertEquals(MoveAilment.PARALYSIS, move.getAilment());
        assertEquals(30, move.getAilmentChance());
        assertEquals(MoveTarget.SELECTED_POKEMON, move.getTarget());
        assertEquals(MoveCategory.DAMAGE_AND_AILMENT, move.getCategory());
        assertEquals(MoveDamageClass.PHYSICAL, move.getDamageClass());
        assertEquals(0, move.getStatChanges().size());
    }

    @Test
    void testStarlyEncounter() {
        Encounter encounter = Encounter.fromPokemonName("starly").get(0);
        assertEquals("great-marsh-area-1", encounter.getLocationArea());
        Encounter.VersionDetail versionDetail = encounter.getDetails().get(0);
        assertEquals(20, versionDetail.getMaxChance());
        assertEquals(Version.DIAMOND, versionDetail.getVersion());
        Encounter.EncounterDetail encounterDetail = versionDetail.getEncounterDetails().get(0);
        assertEquals(10, encounterDetail.getChance());
        assertEquals(26, encounterDetail.getMaxLevel());
        assertEquals(26, encounterDetail.getMinLevel());
        assertEquals(EncounterMethod.WALK, encounterDetail.getMethod());
        assertEquals(EncounterConditionValue.TIME_MORNING, encounterDetail.getConditionValues().get(0));
    }

    @Test
    void testPikachuSpecies() {
        PokemonSpecies species = PokemonSpecies.fromPokemonName("pikachu");
        assertEquals(25, species.getId());
        assertEquals(70, species.getBaseHappiness());
        assertEquals(190, species.getCaptureRate());
        assertEquals(26, species.getOrder());
        assertEquals(10, species.getHatchCounter());
        assertEquals(4, species.getGenderRate());
        assertEquals(false, species.isBaby());
        assertEquals(false, species.isFormsSwitchable());
        assertEquals(true, species.hasGenderDifferences());
        assertEquals(2, species.getEggGroups().size());
        assertEquals(EggGroup.FAIRY, species.getEggGroups().get(0));
        assertEquals(163, species.getPokedexNumbers().get(Pokedex.Type.UPDATED_HOENN).intValue());
        assertEquals("Pikachu", species.getLocalizedNames().get(Language.ENGLISH).getName());
        assertEquals(true, species.getFlavors().get(Language.ENGLISH).getName().startsWith("It’s in"));
        assertEquals(Version.MOON, species.getFlavors().get(Language.ENGLISH).getVersion());
    }
}
